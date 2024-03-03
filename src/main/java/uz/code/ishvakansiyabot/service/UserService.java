package uz.code.ishvakansiyabot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.code.ishvakansiyabot.dto.SendMsgDTO;
import uz.code.ishvakansiyabot.dto.UserDTO;
import uz.code.ishvakansiyabot.entity.UserEntity;
import uz.code.ishvakansiyabot.enums.GeneralStatus;
import uz.code.ishvakansiyabot.enums.UserRole;
import uz.code.ishvakansiyabot.enums.UserStep;
import uz.code.ishvakansiyabot.repository.MapRepository;
import uz.code.ishvakansiyabot.repository.ResumeRepository;
import uz.code.ishvakansiyabot.repository.UserRepository;
import uz.code.ishvakansiyabot.repository.VacancyRepository;
import uz.code.ishvakansiyabot.util.InlineKeyBoardUtil;
import uz.code.ishvakansiyabot.util.ReplyButtons;

import java.util.Comparator;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    VacancyRepository vacancyRepository;
    @Autowired
    ResumeRepository resumeRepository;

    public EditMessageText checkingPost(CallbackQuery callbackQuery) {
        Integer postId;
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        if (callbackQuery.getData().startsWith("notDeleteVacancy")) {
            postId = Integer.parseInt(callbackQuery.getData().substring(16));
            vacancyRepository.changeVacancyStatus(postId, GeneralStatus.ACTIVE);
            editMessageText.setText("✅ Vakansiya qayta faollashtirildi.");
        } else if (callbackQuery.getData().startsWith("notDeleteResume")) {
            postId = Integer.parseInt(callbackQuery.getData().substring(15));
            resumeRepository.changeResumeStatus(postId, GeneralStatus.ACTIVE);
            editMessageText.setText("✅ Rezyume qayta faollashtirildi.");
        }
        return editMessageText;
    }

    public Boolean isDigit(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public UserDTO getById(Long id) {
        UserEntity entity = userRepository.findByTgId(id);
        if (entity == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setTgId(entity.getTgId());
        userDTO.setBotId(entity.getBotId());
        userDTO.setAge(entity.getAge());
        userDTO.setName(entity.getName());
        userDTO.setBalance(entity.getBalance());
        userDTO.setAddress(entity.getAddress());
        userDTO.setCreatedDate(entity.getCreatedDate());
        userDTO.setStatus(entity.getStatus());
        userDTO.setStep(entity.getStep());
        userDTO.setRole(entity.getRole());
        return userDTO;
    }

    public void update(UserDTO dto) {
        UserEntity entity = userRepository.findByTgId(dto.getTgId());
        entity.setName(dto.getName());
        entity.setAge(dto.getAge());
        entity.setAddress(dto.getAddress());
        entity.setBalance(dto.getBalance());
        entity.setRole(dto.getRole());
        entity.setStep(dto.getStep());
        entity.setStatus(dto.getStatus());
        entity.setCreatedDate(dto.getCreatedDate());
        userRepository.save(entity);
    }

    public void delete(Long userId) {
        UserDTO dto = getById(userId);
        dto.setStatus(GeneralStatus.DELETED);
        dto.setName(null);
        dto.setAge(null);
        dto.setAddress(null);
        update(dto);
    }

    public void changeStep(Long userId, UserStep userStep) {
        userRepository.changeUserStep(userId, userStep);
    }

    public SendMessage cancelPosting(UserDTO currentUser) {
        changeStep(currentUser.getTgId(), UserStep.END);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(currentUser.getTgId());
        if (currentUser.getStep().equals(UserStep.ADD_RESUME) || currentUser.getStep().equals(UserStep.ACCEPTING_RESUME) || currentUser.getStep().equals(UserStep.EDIT_RESUME)) {
            sendMessage.setText("❌ Rezyume bekor qilindi.");
            /**   remove vacancy from vacancyMap */
            MapRepository.currentResume.remove(currentUser.getTgId());
        } else if (currentUser.getStep().equals(UserStep.ADD_VACANCY) || currentUser.getStep().equals(UserStep.ACCEPTING_VACANCY) || currentUser.getStep().equals(UserStep.EDIT_VACANCY)) {
            sendMessage.setText("❌ Vakansiya bekor qilindi.");
            /**   remove vacancy from vacancyMap */
            MapRepository.currentVacancy.remove(currentUser.getTgId());
        } else if (currentUser.getStep().equals(UserStep.SEARCH_VACANCY) || currentUser.getStep().equals(UserStep.SEARCH_RESUME)) {
            sendMessage.setText("❌ Qidiruv bekor qilindi.");
            /**   remove user from searcherMap */
            MapRepository.currentSearcherMap.remove(currentUser.getTgId());
        } else if (currentUser.getStep().equals(UserStep.SEND_FEEDBACK)) {
            MapRepository.currentFeedbackMap.remove(currentUser.getTgId());
            sendMessage.setText("❌ Feedback bekor qilindi.");
            userRepository.changeUserStep(currentUser.getTgId(), UserStep.END);
        }
        sendMessage.setReplyMarkup(ReplyButtons.mainMenuButtons());
        //..................................................//
        if (currentUser.getStep().equals(UserStep.CREATING) || currentUser.getStep().equals(UserStep.ACCEPTING_NEW_USER)) {
            sendMessage.setText("❌ Ro'yxatdan o'tish bekor qilindi.");
            sendMessage.setReplyMarkup(ReplyButtons.startButton());
            /** delete user from DB */
            delete(currentUser.getTgId());
        }

        return sendMessage;
    }

    public SendMessage botSupport(Message message) {
        userRepository.changeUserStep(message.getChatId(), UserStep.SEND_FEEDBACK);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("❇\uFE0F Texnik xatolik \n" + "❇\uFE0F Shikoyat yoki taklif \n" + "❇\uFE0F Reklama yoki hamkorlik masalasi\n" + "❇\uFE0F Hisobni to'ldirish\n" + "❇\uFE0F bemani yoki haqoratomuz e'lon\n" + "❇\uFE0F Soxta e'lon yoki shunga o'xshash holat\n" + ". . . . lar haqida adminlarga xabar yubormqchi bo'lsangiz bu haqida to'liq va aniq qilib bayon qiling yoki rasm/skrinshot yuboring. Adminlarimiz 24 soat ichida sizga javob xabarini yuborishadi !\n\n✍\uD83C\uDFFB . . .");
        sendMessage.setReplyMarkup(ReplyButtons.cancelButton());
        return sendMessage;
    }

    public SendMsgDTO sendFeedback(Message message) {
        User user = message.getFrom();
        SendMsgDTO sendMsgDTO = new SendMsgDTO();
        if (message.hasText() && message.getText().equals("✅ Tasdiqlashㅤ")) {
            userRepository.changeUserStep(message.getChatId(), UserStep.END);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(user.getId());
            sendMessage.setText("✅ Xabar adminga yuborildi.");
            sendMessage.setReplyMarkup(ReplyButtons.mainMenuButtons());
            sendMsgDTO.setText(sendMessage);
        } else if (message.hasText() && message.getText().equals("⚠\uFE0F Tahrirlashㅤ")) {
            MapRepository.currentFeedbackMap.remove(user.getId());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText("Xabaringizni kiriting . . .");
            sendMessage.setReplyMarkup(ReplyButtons.cancelButton());
            sendMsgDTO.setText(sendMessage);
        } else {
            SendMsgDTO feedBack = new SendMsgDTO();
            if (message.hasText()) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(5952923848L);
                sendMessage.setText("#feedback  ID: " + message.getChatId() + "\n\n" + message.getText());
                feedBack.setText(sendMessage);
            } else if (message.hasPhoto()) {
                var photo = message.getPhoto().stream().max(Comparator.comparingInt(p -> p.getWidth() * p.getHeight())).orElse(null);
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(5952923848L);
                sendPhoto.setPhoto(new InputFile(photo.getFileId()));
                sendPhoto.setCaption("#feedback  ID: " + message.getChatId() + "\n\n" + message.getCaption());
                feedBack.setPhoto(sendPhoto);
            }
            MapRepository.currentFeedbackMap.put(user.getId(), feedBack);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(user.getId());
            sendMessage.setText("Feedback adminga yuborilsinmi ? \uD83D\uDC47\uD83C\uDFFB");
            sendMessage.setReplyMarkup(ReplyButtons.acceptingButtons());
            sendMsgDTO.setText(sendMessage);
        }
        return sendMsgDTO;
    }

    public SendMessage settings(Message message) {
        userRepository.changeUserStep(message.getChatId(), UserStep.SETTINGS);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Sozlamalar");
        sendMessage.setReplyMarkup(ReplyButtons.settingsButtons());
        return sendMessage;
    }

    public SendMsgDTO statistics(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Foydalanuvchilar : " + userRepository.countAllUsers() + " ta\nActive : " + userRepository.countActiveUsers() + " ta\nNot Active : " + userRepository.countDeletedUsers() + " ta\n\nVakansiyalar : " + vacancyRepository.countAllVacancies() + " ta\nActive : " + vacancyRepository.countActiveVacancies() + " ta\nDeleted : " + vacancyRepository.countDeletedVacancies() + " ta\n\nRezyumelar : " + resumeRepository.countAllResumes() + " ta\nActive : " + resumeRepository.countActiveResumes() + " ta\nDeleted : " + resumeRepository.countDeletedResumes() + " ta");
        SendMsgDTO sendMsgDTO = new SendMsgDTO();
        sendMsgDTO.setText(sendMessage);
        return sendMsgDTO;
    }

    public SendMsgDTO getProfile(Message message) {
        userRepository.changeUserStep(message.getChatId(), UserStep.PROFILE);
        UserDTO dto = getById(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Bot ID : " + dto.getBotId() + "\n\uD83D\uDD37 Ism : " + dto.getName() + "\n\uD83D\uDD36 Yosh : " + dto.getAge() + "\n\uD83D\uDD37 Manzil : " + dto.getAddress() + "\n\uD83D\uDD36 Balans : " + dto.getBalance() + "\n\uD83D\uDD37 Created date : " + dto.getCreatedDate() + "\n\uD83D\uDD36 Vakansiyalar : " + userRepository.countAllVacanciesFromUser(message.getChatId()) + "\n\uD83D\uDD37 Rezyumelar : " + userRepository.countAllResumesFromUser(message.getChatId()) + "\n\nHisobingizni to'ldirish uchun ixtiyoriy mobile app yordamida  ushbu  5614680004553372  karta raqamiga pul o'tkazing va botning \"Admin\" bo'limiga kirib check skrinshotini yuboring. Adminlarimiz check skrishotni 24 soat ichida ko'rib chiqishadi va tasdiqlansa pul sizning hisobingizga qo'shiladi.");
        sendMessage.setReplyMarkup(ReplyButtons.profileButtons());
        SendMsgDTO sendMsgDTO = new SendMsgDTO();
        sendMsgDTO.setText(sendMessage);
        return sendMsgDTO;
    }

    public SendMsgDTO editProfile(Message message) {
        userRepository.changeUserStep(message.getChatId(), UserStep.EDIT_PROFILE);
        UserDTO dto = getById(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Profilni tahrirlash");
        sendMessage.setReplyMarkup(ReplyButtons.editProfileButtons());
        SendMsgDTO sendMsgDTO = new SendMsgDTO();
        sendMsgDTO.setText(sendMessage);
        return sendMsgDTO;
    }

    public SendMsgDTO deleteProfile(Message message) {
        SendMsgDTO sendMsgDTO = new SendMsgDTO();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        if (message.getText().equals("✅ Tasdiqlashㅤ")) {
            userRepository.changeUserName(message.getChatId(), null);
            userRepository.changeUserAge(message.getChatId(), null);
            userRepository.changeUserAddress(message.getChatId(), null);
            userRepository.changeUserStatus(message.getChatId(), GeneralStatus.DELETED);
            vacancyRepository.changeVacanciesStatusByEmployerId(message.getChatId(), GeneralStatus.DELETED);
            resumeRepository.changeResumesStatusByEmployeeId(message.getChatId(), GeneralStatus.DELETED);
            sendMessage.setText("Account has been deleted. !!");
            sendMessage.setReplyMarkup(ReplyButtons.startButton());
            sendMsgDTO.setText(sendMessage);
        } else {
            userRepository.changeUserStep(message.getChatId(), UserStep.DELETING_PROFILE);
            sendMessage.setText("Profilni o'chirish");
            sendMessage.setReplyMarkup(ReplyButtons.editProfileButtons());
            sendMsgDTO.setText(sendMessage);
        }
        return sendMsgDTO;
    }

    public SendMessage setName(String text, Long tgId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgId);
        if (text.contains("0") || text.contains("1") || text.contains("2") || text.contains("3") || text.contains("4") || text.contains("5") || text.contains("6") || text.contains("7") || text.contains("8") || text.contains("9")) {
            sendMessage.setText("⚠\uFE0F Raqamlardan foydalanmang.\n\n✍\uD83C\uDFFB Ismingiz . .");
        } else if (text.length() < 3 || text.length() > 16) {
            sendMessage.setText("⚠\uFE0F Ism uzunligi [3 ; 16] oraliqda bo'lsin\n✍\uD83C\uDFFB Ismingiz . .");
        } else {
            userRepository.changeUserName(tgId, text);
            sendMessage.setText("✅ Ism o'zgartirildi");
            sendMessage.setChatId(tgId);
            sendMessage.setReplyMarkup(ReplyButtons.editProfileButtons());
            userRepository.changeUserStep(tgId, UserStep.EDIT_PROFILE);
        }
        return sendMessage;
    }

    public SendMessage setAge(String text, Long tgId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgId);
        if (isDigit(text) && Long.parseLong(text) >= 10 && Long.parseLong(text) <= 60) {
            userRepository.changeUserAge(tgId, text);
            sendMessage.setText("✅ Yosh o'zgartirildi");
            sendMessage.setChatId(tgId);
            sendMessage.setReplyMarkup(ReplyButtons.editProfileButtons());
            userRepository.changeUserStep(tgId, UserStep.EDIT_PROFILE);
        } else {
            sendMessage.setText("⚠\uFE0F Yoshingizni to'g'ri kiriting . .");
        }
        return sendMessage;
    }
}
