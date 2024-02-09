package uz.code.ishvakansiyabot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.code.ishvakansiyabot.dto.UserDTO;
import uz.code.ishvakansiyabot.dto.VacancyDTO;
import uz.code.ishvakansiyabot.entity.UserEntity;
import uz.code.ishvakansiyabot.entity.VacancyEntity;
import uz.code.ishvakansiyabot.enums.GeneralStatus;
import uz.code.ishvakansiyabot.enums.UserStep;
import uz.code.ishvakansiyabot.repository.VacancyRepository;
import uz.code.ishvakansiyabot.util.InlineKeyBoardUtil;
import uz.code.ishvakansiyabot.util.InlineKeyboardButtonUtil;

import java.util.*;

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

    public EditMessageText getById(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        //..............................................//
        Integer vacancyId = Integer.parseInt(callbackQuery.getData().substring(15));
        /**  get vacancyEntity from DB */
        Optional<VacancyEntity> optional = vacancyRepository.findById(vacancyId);
        VacancyEntity entity = optional.get();
        if (callbackQuery.getData().startsWith("showMoreVacancy")) {
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n\uD83C\uDFE2 Ish beruvchi : " + entity.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + entity.getPosition() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + entity.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + entity.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + entity.getExtraInfo() + "\n\n《《   @IshVakansiyaBot   》》");
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Vakansiyani o'chirish", "deletingVacancy" + entity.getId())), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Xabarni qisqartirish", "showLessVacancy" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("showLessVacancy")) {
            /**  get created date */
            String date = String.valueOf(entity.getCreatedDate());
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30  Vakansiya  \uD83D\uDD30" + "\n\uD83C\uDFE2 Ish beruvchi : " + entity.getEmployerName() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDDD3 Created Date : " + date.substring(0, 10) + " " + date.substring(11, 16));
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("To'liq ko'rsatish", "showMoreVacancy" + entity.getId())))));
        }
        return editMessageText;
    }

    public List<SendMessage> getMyVacancies(Long userId) {
        List<VacancyEntity> vacancyEntityList = vacancyRepository.findByEmployerIdAndStatus(userId, GeneralStatus.ACTIVE);
        List<SendMessage> vacancyMsgList = new LinkedList<>();
        VacancyEntity entity;
        String date;
        for (int i = 0; i < vacancyEntityList.size(); i++) {
            entity = vacancyEntityList.get(i);
            date = String.valueOf(entity.getCreatedDate());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(userId);
            sendMessage.setText("#" + entity.getId() + "  \uD83D\uDD30  Vakansiya  \uD83D\uDD30" + "\n\uD83C\uDFE2 Ish beruvchi : " + entity.getEmployerName() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDDD3 Created Date : " + date.substring(0, 10) + " " + date.substring(11, 16));
            sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("To'liq ko'rsatish", "showMoreVacancy" + entity.getId())))));
            vacancyMsgList.add(sendMessage);
        }
        if (vacancyMsgList.isEmpty()) {
            SendMessage send = new SendMessage();
            send.setChatId(userId);
            send.setText("Sizda vakansiyalar mavjud emas !");
            vacancyMsgList.add(send);
        }
        return vacancyMsgList;
    }

    public SendMessage setEmployerName(Message message) {
        currentVacancy.get(message.getChatId()).setEmployerName(message.getText());
        /**   get currentVacancy */
        VacancyDTO dto = currentVacancy.get(message.getChatId());
        //.........................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        if (dto.getWorkRegion() == null) {
            sendMessage.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83D\uDD37 Ish beruvchi : " + message.getText() + "\n\uD83D\uDD36 Manzil :  ...");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.regionsButtons());
        } else {
            UserDTO userDTO = userService.getById(message.getChatId());
            userDTO.setStep(UserStep.ACCEPTING_VACANCY);
            userService.update(userDTO);
            sendMessage.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83D\uDD37 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDD36 Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDD37 Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDD36 Lavozim : " + dto.getPosition() + "\n\uD83D\uDD37 Maosh : " + dto.getSalary() + "\n\uD83D\uDD36 Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDD37 Aloqa : " + dto.getConnectAddress() + "\n\n\uD83D\uDD36 Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
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
        /**  get currentVacancy */
        VacancyDTO dto = currentVacancy.get(callbackQuery.getFrom().getId());
        //...............................................//
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        if (dto.getSpecialty1() == null) {
            /**  remove districts button and show specialty buttons  */
            editMessageText.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83D\uDD37 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDD36 Manzil : " + dto.getWorkRegion() + ", " + callbackQuery.getData() + "\n\uD83D\uDD37 Yo'nalish :   ...");
            /**  . .make specialty buttons(1)  */
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.specialtyButtons());
        } else {
            UserDTO userDTO = userService.getById(callbackQuery.getFrom().getId());
            userDTO.setStep(UserStep.ACCEPTING_VACANCY);
            userService.update(userDTO);
            editMessageText.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83D\uDD37 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDD36 Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDD37 Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDD36 Lavozim : " + dto.getPosition() + "\n\uD83D\uDD37 Maosh : " + dto.getSalary() + "\n\uD83D\uDD36 Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDD37 Aloqa : " + dto.getConnectAddress() + "\n\n\uD83D\uDD36 Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return editMessageText;
    }

    public EditMessageText setSpecialty1(CallbackQuery callbackQuery) {
        /** set specialty to vacancy */
        currentVacancy.get(callbackQuery.getFrom().getId()).setSpecialty1(callbackQuery.getData());
        /**  remove Sspecialty(1) button and show specialty(2) buttons  */
        VacancyDTO dto = currentVacancy.get(callbackQuery.getFrom().getId());
        //......................................................//
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83D\uDD37 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDD36 Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDD37 Yo'nalish : " + callbackQuery.getData() + ", . .");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        /**  . .make specialty(2) buttons  */
        editMessageText.setReplyMarkup(InlineKeyBoardUtil.chooseSpecialty2FromCallBackQuery(callbackQuery));
        return editMessageText;
    }

    public EditMessageText setSpecialty2(CallbackQuery callbackQuery) {
        /** set specialty to vacancy */
        currentVacancy.get(callbackQuery.getFrom().getId()).setSpecialty2(callbackQuery.getData());
        /**  get currentVacancy */
        VacancyDTO dto = currentVacancy.get(callbackQuery.getFrom().getId());
        //...............................................//
        /**  remove Sspecialty(1) button and show specialty(2) buttons  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        if (dto.getPosition() == null) {
            editMessageText.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83D\uDD37 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDD36 Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDD37 Yo'nalish : " + dto.getSpecialty1() + ", " + callbackQuery.getData() + "\n\n\uD83D\uDD30 Lavozimni kiriting (qo'shimcha)\uD83D\uDD30");
            editMessageText.setReplyMarkup(null);
        } else {
            UserDTO userDTO = userService.getById(callbackQuery.getFrom().getId());
            userDTO.setStep(UserStep.ACCEPTING_VACANCY);
            userService.update(userDTO);
            editMessageText.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83D\uDD37 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDD36 Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDD37 Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDD36 Lavozim : " + dto.getPosition() + "\n\uD83D\uDD37 Maosh : " + dto.getSalary() + "\n\uD83D\uDD36 Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDD37 Aloqa : " + dto.getConnectAddress() + "\n\n\uD83D\uDD36 Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return editMessageText;
    }

    public SendMessage setPosition(Message message) {
        /**  set position to vacancy */
        currentVacancy.get(message.getChatId()).setPosition(message.getText());
        /**   get vcurrentVacancy */
        VacancyDTO dto = currentVacancy.get(message.getChatId());
        //.......................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        if (dto.getSalary() == null) {
            /**  "enter work time" msg */
            sendMessage.setText("\uD83D\uDD5E Haftalik ish soati . .");
        } else {
            UserDTO userDTO = userService.getById(message.getChatId());
            userDTO.setStep(UserStep.ACCEPTING_VACANCY);
            userService.update(userDTO);
            sendMessage.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83D\uDD37 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDD36 Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDD37 Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDD36 Lavozim : " + dto.getPosition() + "\n\uD83D\uDD37 Maosh : " + dto.getSalary() + "\n\uD83D\uDD36 Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDD37 Aloqa : " + dto.getConnectAddress() + "\n\n\uD83D\uDD36 Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return sendMessage;
    }

    public SendMessage setWorkTime(Message message) {
        /**   get vcurrentVacancy */
        VacancyDTO dto = currentVacancy.get(message.getChatId());
        //.......................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        if (userService.isDigit(message.getText())) {
            if (Integer.parseInt(message.getText()) >= 7 && Integer.parseInt(message.getText()) <= 70) {
                /** set setWorkTime to vacancy */
                currentVacancy.get(message.getChatId()).setWorkTime(message.getText());
                if (dto.getSalary() == null) {
                    /**  enter salary msg */
                    sendMessage.setText(" \uD83D\uDD30  Oylik maoshni kiriting  \uD83D\uDD30\n\n‼\uFE0F  so'm yoki dollarda aniq qilib kiriting.");
                } else {
                    UserDTO userDTO = userService.getById(message.getChatId());
                    userDTO.setStep(UserStep.ACCEPTING_VACANCY);
                    userService.update(userDTO);
                    sendMessage.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83D\uDD37 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDD36 Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDD37 Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDD36 Lavozim : " + dto.getPosition() + "\n\uD83D\uDD37 Maosh : " + dto.getSalary() + "\n\uD83D\uDD36 Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDD37 Aloqa : " + dto.getConnectAddress() + "\n\n\uD83D\uDD36 Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
                    sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
                }
            } else {
                sendMessage.setText("⚠\uFE0F Haftalik ish soati [7S ; 70S] orasida bo'lsin.\n\n\uD83D\uDD5E Haftalik ish soati . .");
            }
        } else {
            sendMessage.setText("⚠\uFE0F Faqat raqamlardan foydalaning.\n\n\uD83D\uDD5E Haftalik ish soati . .");
        }
        return sendMessage;
    }

    public SendMessage setSalary(Message message) {
        /**   get currentVacancy */
        VacancyDTO dto = currentVacancy.get(message.getChatId());
        //..............................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        /** set salary to vacancy */
        currentVacancy.get(message.getChatId()).setSalary(message.getText());
        /**  enter call link msg */
        if (dto.getConnectAddress() == null) {
            sendMessage.setText("\uD83D\uDCE8  Aloqaga chiqish uchun link yoki tel raqam yozib qoldiring.");
        } else {
            UserDTO userDTO = userService.getById(message.getChatId());
            userDTO.setStep(UserStep.ACCEPTING_VACANCY);
            userService.update(userDTO);
            sendMessage.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83D\uDD37 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDD36 Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDD37 Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDD36 Lavozim : " + dto.getPosition() + "\n\uD83D\uDD37 Maosh : " + dto.getSalary() + "\n\uD83D\uDD36 Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDD37 Aloqa : " + dto.getConnectAddress() + "\n\n\uD83D\uDD36 Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return sendMessage;
    }

    public SendMessage setConnectAddress(Message message) {
        /**   get currentVacancy */
        VacancyDTO dto = currentVacancy.get(message.getChatId());
        //......................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        /** set connect address to vacancy */
        currentVacancy.get(message.getChatId()).setConnectAddress(message.getText());
        if (dto.getExtraInfo() == null) {
            /**  enter extra info msg */
            sendMessage.setText("\uD83D\uDCCB  Xodimdan nimalarni talab qilishingiz, uning ish tajribasi, darajasi, xodimga nimalarni taklif qila olasiz va korxona haqida ba'zi ma'lumotlar . .\nShu kabi ma'lumotlarni kiritishingizni iltimos qilamiz.\n\n✍\uD83C\uDFFB  Qo'shimcha ma'lumotlarni kiriting.");
        } else {
            UserDTO userDTO = userService.getById(message.getChatId());
            userDTO.setStep(UserStep.ACCEPTING_VACANCY);
            userService.update(userDTO);
            sendMessage.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83D\uDD37 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDD36 Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDD37 Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDD36 Lavozim : " + dto.getPosition() + "\n\uD83D\uDD37 Maosh : " + dto.getSalary() + "\n\uD83D\uDD36 Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDD37 Aloqa : " + dto.getConnectAddress() + "\n\n\uD83D\uDD36 Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return sendMessage;
    }

    public SendMessage acceptingVacancy(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        /**  check extra info to 128 sybols */
        if (message.getText().length() >= 127) {
            /**  set extra info to vacancy */
            currentVacancy.get(message.getChatId()).setExtraInfo(message.getText());
            VacancyDTO dto = currentVacancy.get(message.getChatId());
            //...............................................................//
            sendMessage.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83D\uDD37 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDD36 Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDD37 Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDD36 Lavozim : " + dto.getPosition() + "\n\uD83D\uDD37 Maosh : " + dto.getSalary() + "\n\uD83D\uDD36 Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDD37 Aloqa : " + dto.getConnectAddress() + "\n\n\uD83D\uDD36 Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
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

    public EditMessageText editVacancyButtons(CallbackQuery callbackQuery) {
        /** change user's step */
        UserDTO user = userService.getById(callbackQuery.getFrom().getId());
        user.setStep(UserStep.EDIT_VACANCY);
        userService.update(user);
        /**  get currentVacancy to DTO */
        VacancyDTO dto = currentVacancy.get(callbackQuery.getFrom().getId());
        /** send total Vacancy Msg  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("  \uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n\uD83D\uDD37 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDD36 Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDD37 Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDD36 Lavozim : " + dto.getPosition() + "\n\uD83D\uDD37 Maosh : " + dto.getSalary() + "\n\uD83D\uDD36 Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDD37 Aloqa : " + dto.getConnectAddress() + "\n\n\uD83D\uDD36 Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDD30 Qaysi birini tahrirlamoqchisiz ? \uD83D\uDD30");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setReplyMarkup(InlineKeyBoardUtil.editingVacancyButtons());
        return editMessageText;
    }

    public EditMessageText editVacancy(CallbackQuery callbackQuery) {
        /** change user's step to ADD_VACANCY */
        UserDTO user = userService.getById(callbackQuery.getFrom().getId());
        user.setStep(UserStep.ADD_VACANCY);
        userService.update(user);
        //...............................................//
        String data = callbackQuery.getData();
        //...............................................//
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        if (data.equals("employerName")) {
            currentVacancy.get(callbackQuery.getFrom().getId()).setEmployerName(null);
            editMessageText.setText("\uD83C\uDFE2  Ish beruvchi nomi . .");
        } else if (data.equals("address")) {
            currentVacancy.get(callbackQuery.getFrom().getId()).setWorkRegion(null);
            currentVacancy.get(callbackQuery.getFrom().getId()).setWorkDistinct(null);
            editMessageText.setText("\uD83D\uDDFA Manzil . .");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.regionsButtons());
        } else if (data.equals("specialty")) {
            currentVacancy.get(callbackQuery.getFrom().getId()).setSpecialty1(null);
            currentVacancy.get(callbackQuery.getFrom().getId()).setSpecialty2(null);
            editMessageText.setText("\uD83D\uDCCB Yo'nalish . .");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.specialtyButtons());
        } else if (data.equals("position")) {
            currentVacancy.get(callbackQuery.getFrom().getId()).setPosition(null);
            editMessageText.setText("\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim . .");
        } else if (data.equals("workTime")) {
            currentVacancy.get(callbackQuery.getFrom().getId()).setWorkTime(null);
            editMessageText.setText("\uD83D\uDD5E Haftalik ish soati . .");
        } else if (data.equals("salary")) {
            currentVacancy.get(callbackQuery.getFrom().getId()).setSalary(null);
            editMessageText.setText("\uD83D\uDCB0 Maosh . .");
        } else if (data.equals("callAddress")) {
            currentVacancy.get(callbackQuery.getFrom().getId()).setConnectAddress(null);
            editMessageText.setText("\uD83D\uDCE8  Aloqaga chiqish uchun link yoki tel raqam yozib qoldiring.");
        } else if (data.equals("extraInfo")) {
            currentVacancy.get(callbackQuery.getFrom().getId()).setExtraInfo(null);
            editMessageText.setText("‼\uFE0F \uD83D\uDCCB  Xodimdan nimalarni talab qilishingiz, uning ish tajribasi, darajasi, xodimga nimalarni taklif qila olasiz va korxona haqida ba'zi ma'lumotlar . .\n" + "Shu kabi ma'lumotlarni kiritishingizni iltimos qilamiz.\n\n✍\uD83C\uDFFB  Qo'shimcha ma'lumotlarni kiriting.");
        }
        return editMessageText;
    }

    public EditMessageText cancelVacancy(Message message) {
        /** change user's step */
        UserDTO user = userService.getById(message.getChatId());
        user.setStep(UserStep.END);
        userService.update(user);
        /** removing vacancyTotalMsg */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(message.getChatId());
        editMessageText.setText("❌  Vakansiya bekor qilindi.");
        editMessageText.setMessageId(message.getMessageId());
        /**  remove vacancy from HashMap   */
        currentVacancy.remove(message.getChatId());
        return editMessageText;
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
        entity = vacancyRepository.save(entity);
        dto.setId(entity.getId());
        /** send total Vacancy Msg  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("#" + dto.getId() + "  \uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n\uD83D\uDD37 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDD36 Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDD37 Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDD36 Lavozim : " + dto.getPosition() + "\n\uD83D\uDD37 Maosh : " + dto.getSalary() + "\n\uD83D\uDD36 Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDD37 Aloqa : " + dto.getConnectAddress() + "\n\n\uD83D\uDD36 Qo'shimcha : " + dto.getExtraInfo() + "\n\n《《   @IshVakansiyaBot   》》");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        /**  remove vacancy from HashMap    */
        currentVacancy.remove(callbackQuery.getFrom().getId());
        return editMessageText;
    }

    public EditMessageText delete(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        if (callbackQuery.getData().startsWith("deletingVacancy")) {
            Integer vacancyId = Integer.parseInt(callbackQuery.getData().substring(15));
            Optional<VacancyEntity> optional = vacancyRepository.findById(vacancyId);
            VacancyEntity entity = optional.get();
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n\uD83C\uDFE2 Ish beruvchi : " + entity.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + entity.getPosition() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + entity.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + entity.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + entity.getExtraInfo() + "\n\n\uD83D\uDD30 Vakansiya tizimdan o'chirilishiga rozimisiz ? \uD83D\uDD30");
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Ha, roziman", "deleteVacancy" + entity.getId())), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Bekor qilish", "showLessVacancy" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("deleteVacancy")) {
            Integer vacancyId = Integer.parseInt(callbackQuery.getData().substring(13));
            //..................................................//
            Optional<VacancyEntity> optional = vacancyRepository.findById(vacancyId);
            VacancyEntity entity = optional.get();
            /**   change vacancy's status to DELETED  */
            updateStatus(vacancyId, GeneralStatus.DELETED);
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30  Vakansiya  \uD83D\uDD30" + "\n\uD83C\uDFE2 Ish beruvchi : " + entity.getEmployerName() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\n❌ Vakansiya tizimdan o'chirildi !");
        }
        return editMessageText;
    }

    public void updateStatus(Integer id, GeneralStatus generalStatus) {
        Optional<VacancyEntity> optional = vacancyRepository.findById(id);
        VacancyEntity entity = optional.get();
        entity.setStatus(generalStatus);
        vacancyRepository.save(entity);
    }

    public VacancyDTO getById(Integer id) {
        Optional<VacancyEntity> optional = vacancyRepository.findById(id);
        VacancyEntity entity = optional.get();
        VacancyDTO vacancyDTO = new VacancyDTO();

        vacancyDTO.setId(entity.getId());
        vacancyDTO.setCreatedDate(entity.getCreatedDate());
        vacancyDTO.setEmployerName(entity.getEmployerName());
        vacancyDTO.setEmployerId(entity.getEmployerId());
        vacancyDTO.setConnectAddress(entity.getConnectAddress());
        vacancyDTO.setExtraInfo(entity.getExtraInfo());
        vacancyDTO.setPosition(entity.getPosition());
        vacancyDTO.setSalary(entity.getSalary());
        vacancyDTO.setSpecialty1(entity.getSpecialty1());
        vacancyDTO.setSpecialty2(entity.getSpecialty2());
        vacancyDTO.setStatus(entity.getStatus());
        vacancyDTO.setWorkRegion(entity.getWorkRegion());
        vacancyDTO.setWorkDistinct(entity.getWorkDistinct());
        vacancyDTO.setWorkTime(entity.getWorkTime());
        return vacancyDTO;
    }

    public void update(VacancyDTO dto) {
        Optional<VacancyEntity> optional = vacancyRepository.findById(dto.getId());
        VacancyEntity entity = optional.get();
        entity.setId(dto.getId());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setEmployerName(dto.getEmployerName());
        entity.setEmployerId(dto.getEmployerId());
        entity.setConnectAddress(dto.getConnectAddress());
        entity.setExtraInfo(dto.getExtraInfo());
        entity.setPosition(dto.getPosition());
        entity.setSalary(dto.getSalary());
        entity.setSpecialty1(dto.getSpecialty1());
        entity.setSpecialty2(dto.getSpecialty2());
        entity.setStatus(dto.getStatus());
        entity.setWorkDistinct(dto.getWorkDistinct());
        entity.setWorkTime(dto.getWorkTime());
        vacancyRepository.save(entity);
    }
}
