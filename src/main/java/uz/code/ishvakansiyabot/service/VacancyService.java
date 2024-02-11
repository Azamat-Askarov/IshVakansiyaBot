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
import uz.code.ishvakansiyabot.repository.MapRepository;
import uz.code.ishvakansiyabot.repository.ResumeRepository;
import uz.code.ishvakansiyabot.repository.VacancyRepository;
import uz.code.ishvakansiyabot.util.InlineKeyBoardUtil;
import uz.code.ishvakansiyabot.util.InlineKeyboardButtonUtil;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class VacancyService {
    @Autowired
    MapRepository mapRepository;
    @Autowired
    VacancyRepository vacancyRepository;
    @Autowired
    UserService userService;
    @Autowired
    ResumeRepository resumeRepository;

    public SendMessage create(Long userId) {
        /** add vacancy to HashMap */
        VacancyDTO vacancy = new VacancyDTO();
        vacancy.setEmployerId(userId);
        mapRepository.currentVacancy.put(userId, vacancy);
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
        sendMessage.setReplyMarkup(userService.cancelButton());
        return sendMessage;
    }

    public SendMessage setEmployerName(Message message) {
        mapRepository.currentVacancy.get(message.getChatId()).setEmployerName(message.getText());
        /**   get currentVacancy */
        VacancyDTO dto = mapRepository.currentVacancy.get(message.getChatId());
        //.........................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        if (dto.getWorkRegion() == null) {
            sendMessage.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83C\uDFE2 Ish beruvchi : " + message.getText() + "\n\uD83D\uDDFA Manzil :  ...");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.regionsButtons());
        } else {
            UserDTO userDTO = userService.getById(message.getChatId());
            userDTO.setStep(UserStep.ACCEPTING_VACANCY);
            userService.update(userDTO);
            sendMessage.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83C\uDFE2 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + dto.getPosition() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F  Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return sendMessage;
    }

    public EditMessageText setVacancyRegion(CallbackQuery callbackQuery) {
        /** set region to vacancy */
        mapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setWorkRegion(callbackQuery.getData());
        /**  remove regions and show districts BUTTONS  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83C\uDFE2 Ish beruvchi : " + mapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).getEmployerName() + "\n\uD83D\uDDFA Manzil : " + callbackQuery.getData() + ",  ...");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        /**  . .make distinct buttons  */
        editMessageText.setReplyMarkup(InlineKeyBoardUtil.districtButtons(callbackQuery));
        return editMessageText;
    }

    public EditMessageText setVacancyDistinct(CallbackQuery callbackQuery) {
        /** set distinct to vacancy */
        mapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setWorkDistinct(callbackQuery.getData());
        /**  get currentVacancy */
        VacancyDTO dto = mapRepository.currentVacancy.get(callbackQuery.getFrom().getId());
        //...............................................//
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        if (dto.getSpecialty1() == null) {
            /**  remove districts button and show specialty buttons  */
            editMessageText.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83C\uDFE2 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + callbackQuery.getData() + "\n\uD83D\uDCCB Yo'nalish :   ...");
            /**  . .make specialty buttons(1)  */
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.specialtyButtons());
        } else {
            UserDTO userDTO = userService.getById(callbackQuery.getFrom().getId());
            userDTO.setStep(UserStep.ACCEPTING_VACANCY);
            userService.update(userDTO);
            editMessageText.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83C\uDFE2 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + dto.getPosition() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F  Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return editMessageText;
    }

    public EditMessageText setSpecialty1(CallbackQuery callbackQuery) {
        /** set specialty to vacancy */
        mapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setSpecialty1(callbackQuery.getData());
        /**  remove Sspecialty(1) button and show specialty(2) buttons  */
        VacancyDTO dto = mapRepository.currentVacancy.get(callbackQuery.getFrom().getId());
        //......................................................//
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83C\uDFE2 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + callbackQuery.getData() + ", . .");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        /**  . .make specialty(2) buttons  */
        editMessageText.setReplyMarkup(InlineKeyBoardUtil.chooseSpecialty2FromCallBackQuery(callbackQuery));
        return editMessageText;
    }

    public EditMessageText setSpecialty2(CallbackQuery callbackQuery) {
        /** set specialty to vacancy */
        mapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setSpecialty2(callbackQuery.getData());
        /**  get currentVacancy */
        VacancyDTO dto = mapRepository.currentVacancy.get(callbackQuery.getFrom().getId());
        //...............................................//
        /**  remove Sspecialty(1) button and show specialty(2) buttons  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        if (dto.getPosition() == null) {
            editMessageText.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83C\uDFE2 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + callbackQuery.getData() + "\n\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozimni kiriting . .\n\uD83D\uDD30 yoki talab qilinadigan texnologiyalar(qisqacha) \uD83D\uDD30");
            editMessageText.setReplyMarkup(null);
        } else {
            UserDTO userDTO = userService.getById(callbackQuery.getFrom().getId());
            userDTO.setStep(UserStep.ACCEPTING_VACANCY);
            userService.update(userDTO);
            editMessageText.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83C\uDFE2 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + dto.getPosition() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F  Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return editMessageText;
    }

    public SendMessage setPosition(Message message) {
        /**  set position to vacancy */
        mapRepository.currentVacancy.get(message.getChatId()).setPosition(message.getText());
        /**   get vcurrentVacancy */
        VacancyDTO dto = mapRepository.currentVacancy.get(message.getChatId());
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
            sendMessage.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83C\uDFE2 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + dto.getPosition() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F  Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return sendMessage;
    }

    public SendMessage setWorkTime(Message message) {
        /**   get vcurrentVacancy */
        VacancyDTO dto = mapRepository.currentVacancy.get(message.getChatId());
        //.......................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        if (userService.isDigit(message.getText())) {
            if (Integer.parseInt(message.getText()) >= 7 && Integer.parseInt(message.getText()) <= 70) {
                /** set setWorkTime to vacancy */
                mapRepository.currentVacancy.get(message.getChatId()).setWorkTime(message.getText());
                if (dto.getSalary() == null) {
                    /**  enter salary msg */
                    sendMessage.setText(" \uD83D\uDD30  Oylik maoshni kiriting  \uD83D\uDD30\n\n‼\uFE0F  so'm yoki dollarda aniq qilib kiriting.");
                } else {
                    UserDTO userDTO = userService.getById(message.getChatId());
                    userDTO.setStep(UserStep.ACCEPTING_VACANCY);
                    userService.update(userDTO);
                    sendMessage.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83C\uDFE2 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + dto.getPosition() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F  Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
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
        VacancyDTO dto = mapRepository.currentVacancy.get(message.getChatId());
        //..............................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        /** set salary to vacancy */
        mapRepository.currentVacancy.get(message.getChatId()).setSalary(message.getText());
        /**  enter call link msg */
        if (dto.getConnectAddress() == null) {
            sendMessage.setText("\uD83D\uDCE8  Aloqaga chiqish uchun link yoki tel raqam yozib qoldiring.");
        } else {
            UserDTO userDTO = userService.getById(message.getChatId());
            userDTO.setStep(UserStep.ACCEPTING_VACANCY);
            userService.update(userDTO);
            sendMessage.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83C\uDFE2 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + dto.getPosition() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F  Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return sendMessage;
    }

    public SendMessage setConnectAddress(Message message) {
        /**   get currentVacancy */
        VacancyDTO dto = mapRepository.currentVacancy.get(message.getChatId());
        //......................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        /** set connect address to vacancy */
        mapRepository.currentVacancy.get(message.getChatId()).setConnectAddress(message.getText());
        if (dto.getExtraInfo() == null) {
            /**  enter extra info msg */
            sendMessage.setText("‼\uFE0F Xodimdan nimalarni talab qilishingiz, uning ish tajribasi, darajasi, xodimga nimalarni taklif qila olasiz va korxona haqida ba'zi ma'lumotlar . .\nShu kabi ma'lumotlarni kiritishingizni iltimos qilamiz.\n\n✍\uD83C\uDFFB . . .");
        } else {
            UserDTO userDTO = userService.getById(message.getChatId());
            userDTO.setStep(UserStep.ACCEPTING_VACANCY);
            userService.update(userDTO);
            sendMessage.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83C\uDFE2 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + dto.getPosition() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return sendMessage;
    }

    public SendMessage acceptingVacancy(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        /**  check extra info to 128 sybols */
        if (message.getText().length() >= 31) {
            /**  set extra info to vacancy */
            mapRepository.currentVacancy.get(message.getChatId()).setExtraInfo(message.getText());
            VacancyDTO dto = mapRepository.currentVacancy.get(message.getChatId());
            //...............................................................//
            sendMessage.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83C\uDFE2 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + dto.getPosition() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
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
        VacancyDTO dto = mapRepository.currentVacancy.get(callbackQuery.getFrom().getId());
        /** send total Vacancy Msg  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("  \uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n\uD83C\uDFE2 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + dto.getPosition() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDD30 Qaysi birini tahrirlamoqchisiz ? \uD83D\uDD30");
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
            mapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setEmployerName(null);
            editMessageText.setText("\uD83C\uDFE2  Ish beruvchi nomi . .");
        } else if (data.equals("address")) {
            mapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setWorkRegion(null);
            mapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setWorkDistinct(null);
            editMessageText.setText("\uD83D\uDDFA Manzil . .");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.regionsButtons());
        } else if (data.equals("specialty")) {
            mapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setSpecialty1(null);
            mapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setSpecialty2(null);
            editMessageText.setText("\uD83D\uDCCB Yo'nalish . .");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.specialtyButtons());
        } else if (data.equals("position")) {
            mapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setPosition(null);
            editMessageText.setText("\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim . .");
        } else if (data.equals("workTime")) {
            mapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setWorkTime(null);
            editMessageText.setText("\uD83D\uDD5E Haftalik ish soati . .");
        } else if (data.equals("salary")) {
            mapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setSalary(null);
            editMessageText.setText("\uD83D\uDCB0 Maosh . .");
        } else if (data.equals("callAddress")) {
            mapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setConnectAddress(null);
            editMessageText.setText("\uD83D\uDCE8  Aloqaga chiqish uchun link yoki tel raqam yozib qoldiring.");
        } else if (data.equals("extraInfo")) {
            mapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setExtraInfo(null);
            editMessageText.setText("‼\uFE0F \uD83D\uDCCB  Xodimdan nimalarni talab qilishingiz, uning ish tajribasi, darajasi, xodimga nimalarni taklif qila olasiz va korxona haqida ba'zi ma'lumotlar . .\n" + "Shu kabi ma'lumotlarni kiritishingizni iltimos qilamiz.\n\n✍\uD83C\uDFFB . . .");
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
        mapRepository.currentVacancy.remove(message.getChatId());
        return editMessageText;
    }

    public synchronized VacancyDTO save(CallbackQuery callbackQuery) {
        /** change user's step */
        UserDTO user = userService.getById(callbackQuery.getFrom().getId());
        user.setStep(UserStep.END);
        userService.update(user);
        /**  get currentVacancy to DTO */
        VacancyDTO dto = mapRepository.currentVacancy.get(callbackQuery.getFrom().getId());
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
        //...............................................//
        String createdDate = String.valueOf(LocalDateTime.now());
        String s = createdDate.substring(0, 10) + " " + createdDate.substring(11, 16);
        entity.setCreatedDate(s);
        entity = vacancyRepository.save(entity);
        dto.setId(entity.getId());
        dto.setCreatedDate(entity.getCreatedDate());
        /**  remove vacancy from HashMap    */
        mapRepository.currentVacancy.remove(callbackQuery.getFrom().getId());
        return dto;
    }

    public EditMessageText delete(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        if (callbackQuery.getData().startsWith("getDeletingVacancy")) {
            Integer vacancyId = Integer.parseInt(callbackQuery.getData().substring(18));
            Optional<VacancyEntity> optional = vacancyRepository.findById(vacancyId);
            VacancyEntity entity = optional.get();
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n\uD83C\uDFE2 Ish beruvchi : " + entity.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + entity.getPosition() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + entity.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + entity.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + entity.getExtraInfo() + "\n\n\uD83D\uDD30 Vakansiya tizimdan o'chirilishiga rozimisiz ? \uD83D\uDD30");
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Ha, roziman", "getDeleteVacancy" + entity.getId())), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Bekor qilish", "getLessVacancy" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("getDeleteVacancy")) {
            Integer vacancyId = Integer.parseInt(callbackQuery.getData().substring(16));
            //..................................................//
            Optional<VacancyEntity> optional = vacancyRepository.findById(vacancyId);
            VacancyEntity entity = optional.get();
            /**   change vacancy's status to DELETED  */
            vacancyRepository.changeVacancyStatus(vacancyId, GeneralStatus.DELETED);
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30  Vakansiya  \uD83D\uDD30" + "\n\uD83C\uDFE2 Ish beruvchi : " + entity.getEmployerName() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\n❌ Vakansiya tizimdan o'chirildi !");
        }
        return editMessageText;
    }

    public EditMessageText getById(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        //............................................................................//
        Integer vacancyId;
        if (callbackQuery.getData().startsWith("getMoreVacancy")) {
            vacancyId = Integer.parseInt(callbackQuery.getData().substring(14));
            /**  get vacancyEntity from DB */
            Optional<VacancyEntity> optional = vacancyRepository.findById(vacancyId);
            VacancyEntity entity = optional.get();
            //........................................................................//
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n\uD83C\uDFE2 Ish beruvchi : " + entity.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + entity.getPosition() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + entity.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + entity.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + entity.getExtraInfo() + "\n\n《《   @IshVakansiyaBot   》》");
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Vakansiyani o'chirish", "getDeletingVacancy" + entity.getId())), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Xabarni qisqartirish", "getLessVacancy" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("getLessVacancy")) {
            vacancyId = Integer.parseInt(callbackQuery.getData().substring(14));
            /**  get vacancyEntity from DB */
            Optional<VacancyEntity> optional = vacancyRepository.findById(vacancyId);
            VacancyEntity entity = optional.get();
            /**  get created date */
            String date = String.valueOf(entity.getCreatedDate());
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30  Vakansiya  \uD83D\uDD30" + "\n\uD83C\uDFE2 Ish beruvchi : " + entity.getEmployerName() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDDD3 Created Date : " + entity.getCreatedDate());
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("To'liq ko'rsatish", "getMoreVacancy" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("getMoreNewVacancy")) {
            vacancyId = Integer.parseInt(callbackQuery.getData().substring(17));
            /**  get vacancyEntity from DB */
            Optional<VacancyEntity> optional = vacancyRepository.findById(vacancyId);
            VacancyEntity entity = optional.get();
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n\uD83C\uDFE2 Ish beruvchi : " + entity.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + entity.getPosition() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + entity.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + entity.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + entity.getExtraInfo() + "\n\n《《   @IshVakansiyaBot   》》");
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Xabarni qisqartirish", "getLessNewVacancy" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("getLessNewVacancy")) {
            vacancyId = Integer.parseInt(callbackQuery.getData().substring(17));
            /**  get vacancyEntity from DB */
            Optional<VacancyEntity> optional = vacancyRepository.findById(vacancyId);
            VacancyEntity entity = optional.get();
            /**  get created date */
            String date = String.valueOf(entity.getCreatedDate());
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30  Vakansiya  \uD83D\uDD30" + "\n\uD83C\uDFE2 Ish beruvchi : " + entity.getEmployerName() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDDD3 Created Date : " + entity.getCreatedDate());
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("To'liq ko'rsatish", "getMoreNewVacancy" + entity.getId())))));
        }
        return editMessageText;
    }

    public List<SendMessage> getMyVacancies(Long userId) {
        List<VacancyEntity> vacancyEntityList = vacancyRepository.findByEmployerIdAndStatus(userId, GeneralStatus.ACTIVE);
        List<SendMessage> vacancyMsgList = new LinkedList<>();
        VacancyEntity entity;
        for (int i = 0; i < vacancyEntityList.size(); i++) {
            entity = vacancyEntityList.get(i);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(userId);
            sendMessage.setText("#" + entity.getId() + "  \uD83D\uDD30  Vakansiya  \uD83D\uDD30" + "\n\uD83C\uDFE2 Ish beruvchi : " + entity.getEmployerName() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDDD3 Created Date : " + entity.getCreatedDate());
            sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("To'liq ko'rsatish", "getMoreVacancy" + entity.getId())))));
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

    public List<SendMessage> sendingVacancyToEmployees(VacancyDTO dto) {
        // String date = String.valueOf(dto.getCreatedDate());
        List<Long> employeeList = resumeRepository.getEmployeeIdByWorkRegionAndSpecialty2AndStatus(dto.getWorkRegion(), dto.getSpecialty2(), GeneralStatus.ACTIVE);
        List<SendMessage> sendMessageList = new LinkedList<>();
        for (int i = 0; i < employeeList.size(); i++) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(employeeList.get(i));
            sendMessage.setText("#" + dto.getId() + "  \uD83D\uDD30  Vakansiya  \uD83D\uDD30" + "\n\uD83C\uDFE2 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDDD3 Created date : " + dto.getCreatedDate());
            sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("To'liq ko'rsatish", "getMoreNewVacancy" + dto.getId())))));
            sendMessageList.add(sendMessage);
        }
        return sendMessageList;
    }
}
