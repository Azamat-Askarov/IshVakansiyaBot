package uz.code.ishvakansiyabot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.code.ishvakansiyabot.dto.UserDTO;
import uz.code.ishvakansiyabot.dto.VacancyDTO;
import uz.code.ishvakansiyabot.entity.VacancyEntity;
import uz.code.ishvakansiyabot.enums.GeneralStatus;
import uz.code.ishvakansiyabot.enums.UserStep;
import uz.code.ishvakansiyabot.repository.VacancyRepository;
import uz.code.ishvakansiyabot.util.InlineKeyBoardUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class VacancyService {
    @Autowired
    VacancyRepository vacancyRepository;
    @Autowired
    UserService userService;
    public Map<Long, VacancyDTO> currentVacancy = new HashMap<>();

    public SendMessage create(Long userId) {
        /** add vacancy to HashMap */
        VacancyDTO vacancy = new VacancyDTO();
        vacancy.setEmployerId(userId);
        currentVacancy.put(userId, vacancy);
        /** change currentUser's step */
        UserDTO user = userService.getById(userId);
        user.setStep(UserStep.ADD_VACANCY);
        userService.update(user);
        /**  show "enter employerName" msg  */
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83C\uDFE2  Ish beruvchi nomi . .");
        sendMessage.setChatId(userId);
        /** Remove ReplyButtons */
        ReplyKeyboardRemove removeButton = new ReplyKeyboardRemove();
        removeButton.setSelective(true);
        removeButton.setRemoveKeyboard(true);
        sendMessage.setReplyMarkup(removeButton);
        return sendMessage;
    }

    public VacancyDTO getById(Integer id) {
        Optional<VacancyEntity> optional = vacancyRepository.findById(id);
        VacancyEntity entity = optional.get();
        if (entity == null) {
            return null;
        }
        VacancyDTO dto = new VacancyDTO();
        dto.setId(entity.getId());
        dto.setEmployerId(entity.getEmployerId());
        dto.setEmployerName(entity.getEmployerName());
        dto.setStatus(entity.getStatus());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setSpecialty1(entity.getSpecialty1());
        dto.setSpecialty2(entity.getSpecialty2());
        dto.setPosition(entity.getPosition());
        dto.setSalary(entity.getSalary());
        dto.setWorkRegion(entity.getWorkRegion());
        dto.setWorkDistinct(entity.getWorkDistinct());
        dto.setConnectAddress(entity.getConnectAddress());
        dto.setExtraInfo(entity.getExtraInfo());
        return dto;
    }

    public SendMessage setEmployerName(Message message) {
        currentVacancy.get(message.getChatId()).setEmployerName(message.getText());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83D\uDD37 Ish beruvchi : " + message.getText() + "\n\uD83D\uDD36 Manzil :  ...");
        sendMessage.setChatId(message.getChatId());
        sendMessage.setReplyMarkup(InlineKeyBoardUtil.regionsButtons());
        return sendMessage;
    }

    public EditMessageText setVacancyRegion(CallbackQuery callbackQuery) {
        /** set region to vacancy */
        currentVacancy.get(callbackQuery.getFrom().getId()).setWorkRegion(callbackQuery.getData());
        /**  remove regions and show districts BUTTONS  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83D\uDD37 Ish beruvchi : " + currentVacancy.get(callbackQuery.getFrom().getId()).getEmployerName() + "\n\uD83D\uDD36 Manzil : " + callbackQuery.getData() + ",  ...");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        /**  . .make distinct buttons  */
        editMessageText.setReplyMarkup(InlineKeyBoardUtil.districtButtons(callbackQuery));
        return editMessageText;
    }

    public EditMessageText setVacancyDistinct(CallbackQuery callbackQuery) {
        /** set distinct to vacancy */
        currentVacancy.get(callbackQuery.getFrom().getId()).setWorkDistinct(callbackQuery.getData());
        /**  remove districts button and show specialty buttons  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83D\uDD37 Ish beruvchi : " + currentVacancy.get(callbackQuery.getFrom().getId()).getEmployerName() + "\n\uD83D\uDD36 Manzil : " + currentVacancy.get(callbackQuery.getFrom().getId()).getWorkRegion() + ", " + callbackQuery.getData() + "\n\uD83D\uDD37 Yo'nalish :   ...");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        /**  . .make specialty buttons(1)  */
        editMessageText.setReplyMarkup(InlineKeyBoardUtil.specialtyButtons(callbackQuery));
        return editMessageText;
    }

    public EditMessageText setSpecialty1(CallbackQuery callbackQuery) {
        /** set specialty to vacancy */
        currentVacancy.get(callbackQuery.getFrom().getId()).setSpecialty1(callbackQuery.getData());
        /**  remove Sspecialty(1) button and show specialty(2) buttons  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83D\uDD37 Ish beruvchi : " + currentVacancy.get(callbackQuery.getFrom().getId()).getEmployerName() + "\n\uD83D\uDD36 Manzil : " + currentVacancy.get(callbackQuery.getFrom().getId()).getWorkRegion() + ", " + currentVacancy.get(callbackQuery.getFrom().getId()).getWorkDistinct() + "\n\uD83D\uDD37 Yo'nalish : " + callbackQuery.getData()+", . .");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        /**  . .make specialty(2) buttons  */
        editMessageText.setReplyMarkup(InlineKeyBoardUtil.talimButtons(callbackQuery));
        return editMessageText;
    }

    public EditMessageText setSpecialty2(CallbackQuery callbackQuery) {
        /** set specialty to vacancy */
        currentVacancy.get(callbackQuery.getFrom().getId()).setSpecialty2(callbackQuery.getData());
        /**  remove Sspecialty(1) button and show specialty(2) buttons  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83D\uDD37 Ish beruvchi : " + currentVacancy.get(callbackQuery.getFrom().getId()).getEmployerName() + "\n\uD83D\uDD36 Manzil : " + currentVacancy.get(callbackQuery.getFrom().getId()).getWorkRegion() + ", " + currentVacancy.get(callbackQuery.getFrom().getId()).getWorkDistinct() + "\n\uD83D\uDD37 Yo'nalish : " + currentVacancy.get(callbackQuery.getFrom().getId()).getSpecialty1() + ", " + callbackQuery.getData() + "\n\n\uD83D\uDD30 Lavozimni kiriting (qo'shimcha)\uD83D\uDD30");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setReplyMarkup(null);
        return editMessageText;
    }

    public SendMessage setPosition(Message message) {
        /**  set position to vacancy */
        currentVacancy.get(message.getChatId()).setPosition(message.getText());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Haftasiga nechi soat ish talab qilasiz ?");
        sendMessage.setChatId(message.getChatId());
        return sendMessage;
    }

    public SendMessage setWorkTime(Message message) {
        /** set setWorkTime to vacancy */
        currentVacancy.get(message.getChatId()).setWorkTime(message.getText());
        /**  enter salary msg */
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(" \uD83D\uDD30  Oylik maoshni kiriting   \uD83D\uDD30\n\n‼\uFE0F  so'm yoki dollarda aniq qilib kiriting.");
        sendMessage.setChatId(message.getChatId());
        return sendMessage;
    }

    public SendMessage setSalary(Message message) {
        /** set salary to vacancy */
        currentVacancy.get(message.getChatId()).setSalary(message.getText());
        /**  enter call link msg */
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("\uD83D\uDCE8  Aloqaga chiqish uchun link yoki tel raqam yozib qoldiring.");
        sendMessage.setChatId(message.getChatId());
        return sendMessage;
    }

    public SendMessage setConnectAddress(Message message) {
        /** set connect address to vacancy */
        currentVacancy.get(message.getChatId()).setConnectAddress(message.getText());
        /**  enter extra info msg */
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("\uD83D\uDCCB  Xodimdan nimalarni talab qilishingiz, uning ish tajribasi, darajasi, xodimga nimalarni taklif qila olasiz va korxona haqida ba'zi ma'lumotlar . .\nShu kabi ma'lumotlarni kiritishingizni iltimos qilamiz.\n\n✍\uD83C\uDFFB  Qo'shimcha ma'lumotlarni kiriting.");
        sendMessage.setChatId(message.getChatId());
        return sendMessage;
    }

    public SendMessage acceptingVacancy(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        /**  check extra info to 128 sybols */
        if (message.getText().length() >= 127) {
            /**  set extra info to vacancy */
            currentVacancy.get(message.getChatId()).setExtraInfo(message.getText());
            sendMessage.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83D\uDD37 Ish beruvchi : " + currentVacancy.get(message.getChatId()).getEmployerName() + "\n\uD83D\uDD36 Manzil : " + currentVacancy.get(message.getChatId()).getWorkRegion() + ", " + currentVacancy.get(message.getChatId()).getWorkDistinct() + "\n\uD83D\uDD37 Yo'nalish : " + currentVacancy.get(message.getChatId()).getSpecialty1() + ", " + currentVacancy.get(message.getChatId()).getSpecialty2() + "\n\uD83D\uDD36 Lavozim : " + currentVacancy.get(message.getChatId()).getPosition() + "\n\uD83D\uDD37 Maosh : " + currentVacancy.get(message.getChatId()).getSalary() + "\n\uD83D\uDD36 Haftalik ish soati : "+currentVacancy.get(message.getChatId()).getWorkTime()+"\n\uD83D\uDD37 Aloqa : " + currentVacancy.get(message.getChatId()).getConnectAddress() + "\n\n\uD83D\uDD36 Qo'shimcha : " + currentVacancy.get(message.getChatId()).getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
            /** change user's step */
            UserDTO userDTO = userService.getById(message.getChatId());
            userDTO.setStep(UserStep.ACCEPTING_VACANCY);
            userService.update(userDTO);
        } else {
            sendMessage.setText("‼\uFE0F  Vakansiya tushunarli va aniq bo'lishi uchun ko'proq ma'lumot kiriting.\n✍\uD83C\uDFFB . . .");
        }
        return sendMessage;
    }

    public synchronized EditMessageText save(CallbackQuery callbackQuery) {
        /** change user's step */
        UserDTO user = userService.getById(callbackQuery.getFrom().getId());
        user.setStep(UserStep.END);
        userService.update(user);
        /**  get currentVacancy to DTO */
        VacancyDTO dto = currentVacancy.get(callbackQuery.getFrom().getId());
        /**  create vacancyEntity */
        VacancyEntity entity = new VacancyEntity();
        /** setting vacancy's fields */
        entity.setEmployerId(callbackQuery.getFrom().getId());
        entity.setEmployerName(dto.getEmployerName());
        entity.setSpecialty1(dto.getSpecialty1());
        entity.setSpecialty2(dto.getSpecialty2());
        entity.setWorkTime(dto.getWorkTime());
        entity.setWorkRegion(dto.getWorkRegion());
        entity.setWorkDistinct(dto.getWorkDistinct());
        entity.setPosition(dto.getPosition());
        entity.setSalary(dto.getSalary());
        entity.setConnectAddress(dto.getConnectAddress());
        entity.setExtraInfo(dto.getExtraInfo());
        entity=vacancyRepository.save(entity);
        dto.setId(entity.getId());
        /** send total Vacancy Msg  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("#" + dto.getId() + "  \uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n\uD83D\uDD37 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDD36 Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDD37 Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDD36 Lavozim : " + dto.getPosition() + "\n\uD83D\uDD37 Maosh : " + dto.getSalary() + "\n\uD83D\uDD36 Haftalik ish soati : "+dto.getWorkTime()+"\n\uD83D\uDD37 Aloqa : " + dto.getConnectAddress() + "\n\n\uD83D\uDD36 Qo'shimcha : " + dto.getExtraInfo() + "\n\n✅ Vakansiya tizimga yuklandi.");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        /**  remove vacancy from HashMap    */
        currentVacancy.remove(callbackQuery.getFrom().getId());
        return editMessageText;
    }

    public void editVacancy(CallbackQuery callbackQuery) {

    }
}
