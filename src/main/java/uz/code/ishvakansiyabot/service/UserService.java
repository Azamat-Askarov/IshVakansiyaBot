package uz.code.ishvakansiyabot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.code.ishvakansiyabot.dto.SendMsgDTO;
import uz.code.ishvakansiyabot.dto.UserDTO;
import uz.code.ishvakansiyabot.entity.UserEntity;
import uz.code.ishvakansiyabot.enums.GeneralStatus;
import uz.code.ishvakansiyabot.enums.UserStep;
import uz.code.ishvakansiyabot.repository.MapRepository;
import uz.code.ishvakansiyabot.repository.ResumeRepository;
import uz.code.ishvakansiyabot.repository.UserRepository;
import uz.code.ishvakansiyabot.repository.VacancyRepository;
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
        sendMessage.setText("❇\uFE0F Texnik xatolik \n" +
                "❇\uFE0F Shikoyat yoki taklif \n" +
                "❇\uFE0F Reklama yoki hamkorlik masalasi\n" +
                "❇\uFE0F bemani yoki haqoratomuz e'lon\n" +
                "❇\uFE0F Soxta e'lon yoki shunga o'xshash holat\n" +
                ". . . . lar haqida ma'murlarga xabar yubormqchi bo'lsangiz bu haqida to'liq va aniq qilib bayon qiling yoki rasm/skrinshot yuboring. Admin tez orada sizga javob xabarini yuboradi !\n\n✍\uD83C\uDFFB . . .");
        sendMessage.setReplyMarkup(ReplyButtons.cancelButton());
        return sendMessage;
    }

    public SendMsgDTO sendFeedback(Message message) {
        User user = message.getFrom();
        SendMsgDTO sendMsgDTO = new SendMsgDTO();
        if (message.getText().equals("✅ Tasdiqlashㅤ")) {
            userRepository.changeUserStep(message.getChatId(), UserStep.END);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(user.getId());
            sendMessage.setText("✅ Xabar adminga yuborildi.");
            sendMessage.setReplyMarkup(ReplyButtons.mainMenuButtons());
            sendMsgDTO.setText(sendMessage);
        } else if (message.getText().equals("⚠\uFE0F Tahrirlashㅤ")) {
            MapRepository.currentFeedbackMap.remove(user.getId());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText("Xabaringizni qaytadan kiriting . . .");
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
                var photo = message.getPhoto().stream()
                        .max(Comparator.comparingInt(p -> p.getWidth() * p.getHeight()))
                        .orElse(null);
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(5952923848L);
                sendPhoto.setPhoto(new InputFile(photo.getFileId()));
                sendPhoto.setCaption("#feedback  ID: " + message.getChatId() + "\n\n" + message.getCaption());
                feedBack.setPhoto(sendPhoto);
            } else if (message.hasVideo()) {
                SendVideo sendVideo = new SendVideo();
                sendVideo.setChatId(5952923848L);
                sendVideo.setCaption("#feedback  ID: " + message.getChatId() + "\n\n" + message.getCaption());
                sendVideo.setVideo(new InputFile(message.getVideo().getFileId()));
                feedBack.setVideo(sendVideo);
            } else if (message.hasAudio()) {
                SendAudio sendAudio = new SendAudio();
                sendAudio.setChatId(5952923848L);
                sendAudio.setCaption("#feedback  ID: " + message.getChatId() + "\n\n" + message.getCaption());
                sendAudio.setAudio(new InputFile(message.getAudio().getFileId()));
                feedBack.setAudio(sendAudio);
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
}
