package uz.code.ishvakansiyabot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.code.ishvakansiyabot.dto.ResumeDTO;
import uz.code.ishvakansiyabot.dto.UserDTO;
import uz.code.ishvakansiyabot.entity.ResumeEntity;
import uz.code.ishvakansiyabot.entity.VacancyEntity;
import uz.code.ishvakansiyabot.enums.GeneralStatus;
import uz.code.ishvakansiyabot.enums.UserStep;
import uz.code.ishvakansiyabot.repository.MapRepository;
import uz.code.ishvakansiyabot.repository.ResumeRepository;
import uz.code.ishvakansiyabot.repository.VacancyRepository;
import uz.code.ishvakansiyabot.util.InlineKeyBoardUtil;
import uz.code.ishvakansiyabot.util.InlineKeyboardButtonUtil;
import uz.code.ishvakansiyabot.util.ReplyButtons;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ResumeService {
    @Autowired
    ResumeRepository resumeRepository;
    @Autowired
    UserService userService;
    @Autowired
    VacancyRepository vacancyRepository;

    public SendMessage create(Long userId) {
        /** add vacancy to HashMap */
        ResumeDTO resume = new ResumeDTO();
        resume.setEmployeeId(userId);
        MapRepository.currentResume.put(userId, resume);
        /** change currentUser's step */
        UserDTO user = userService.getById(userId);
        user.setStep(UserStep.ADD_RESUME);
        userService.update(user);
        /**  show "enter employerName" msg  */
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("\uD83D\uDD30 Rezyume joylash \uD83D\uDD30\n\n\uD83D\uDC64  Ism :  ...");
        sendMessage.setChatId(userId);
        /** Remove ReplyButtons */
        ReplyKeyboardRemove removeButton = new ReplyKeyboardRemove();
        removeButton.setSelective(true);
        removeButton.setRemoveKeyboard(true);
        sendMessage.setReplyMarkup(ReplyButtons.cancelButton());
        return sendMessage;
    }

    public EditMessageText getById(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        //......................................................//
        Integer resumeId;
        if (callbackQuery.getData().startsWith("getMoreResume")) {
            /**  get vacancyId from callBackQuery */
            resumeId = Integer.parseInt(callbackQuery.getData().substring(13));
            /**  get resumeEntity from DB */
            Optional<ResumeEntity> optional = resumeRepository.findById(resumeId);
            ResumeEntity entity = optional.get();
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30 Rezyume \uD83D\uDD30\n\n\uD83D\uDC64 Ism : " + entity.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + entity.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + entity.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + entity.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + entity.getExtraInfo() + "\n\n《《   @IshVakansiyaBot   》》");
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Rezyumeni o'chirish", "getDeletingResume" + entity.getId())), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Xabarni qisqartirish", "getLessResume" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("getLessResume")) {
            /**  get vacancyId from callBackQuery */
            resumeId = Integer.parseInt(callbackQuery.getData().substring(13));
            /**  get resumeEntity from DB */
            Optional<ResumeEntity> optional = resumeRepository.findById(resumeId);
            ResumeEntity entity = optional.get();
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30  Rezyume  \uD83D\uDD30" + "\n\uD83D\uDC64 Ism : " + entity.getEmployeeName() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDDD3 Created Date : " + entity.getCreatedDate());
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("To'liq ko'rsatish", "getMoreResume" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("getMoreNewResume")) {
            /**  get vacancyId from callBackQuery */
            resumeId = Integer.parseInt(callbackQuery.getData().substring(16));
            /**  get resumeEntity from DB */
            Optional<ResumeEntity> optional = resumeRepository.findById(resumeId);
            ResumeEntity entity = optional.get();
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30 Rezyume \uD83D\uDD30\n\n\uD83D\uDC64 Ism : " + entity.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + entity.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + entity.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + entity.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + entity.getExtraInfo() + "\n\n《《   @IshVakansiyaBot   》》");
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Xabarni qisqartirish", "getLessNewResume" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("getLessNewResume")) {
            /**  get vacancyId from callBackQuery */
            resumeId = Integer.parseInt(callbackQuery.getData().substring(16));
            /**  get resumeEntity from DB */
            Optional<ResumeEntity> optional = resumeRepository.findById(resumeId);
            ResumeEntity entity = optional.get();
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30  Rezyume  \uD83D\uDD30" + "\n\uD83D\uDC64 Ism : " + entity.getEmployeeName() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDDD3 Created Date : " + entity.getCreatedDate());
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("To'liq ko'rsatish", "getMoreNewResume" + entity.getId())))));
        }
        return editMessageText;
    }

    public List<SendMessage> getMyResumes(Long userId) {
        List<ResumeEntity> resumeEntityList = resumeRepository.findByEmployeeIdAndStatus(userId, GeneralStatus.ACTIVE);
        List<SendMessage> resumeMsgList = new LinkedList<>();
        ResumeEntity entity;
        String date;
        for (int i = 0; i < resumeEntityList.size(); i++) {
            entity = resumeEntityList.get(i);
            date = String.valueOf(entity.getCreatedDate());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(userId);
            sendMessage.setText("#" + entity.getId() + "  \uD83D\uDD30  Rezyume  \uD83D\uDD30" + "\n\uD83D\uDC64 Ism : " + entity.getEmployeeName() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDDD3 Created Date : " + date.substring(0, 10) + " " + date.substring(11, 16));
            sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("To'liq ko'rsatish", "getMoreResume" + entity.getId())))));
            resumeMsgList.add(sendMessage);
        }
        if (resumeMsgList.isEmpty()) {
            SendMessage send = new SendMessage();
            send.setChatId(userId);
            send.setText("Sizda rezyumelar mavjud emas !");
            resumeMsgList.add(send);
        }
        return resumeMsgList;
    }

    public SendMessage setEmployeeName(Message message) {
        MapRepository.currentResume.get(message.getChatId()).setEmployeeName(message.getText());
        /**   get currentResume */
        ResumeDTO dto = MapRepository.currentResume.get(message.getChatId());
        //.........................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        if (dto.getWorkRegion() == null) {
            sendMessage.setText("\uD83D\uDD30 Rezyume joylash \uD83D\uDD30\n\n\uD83D\uDC64 Ism : " + message.getText() + "\n\uD83D\uDDFA Manzil :  ...");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.regionsButtons());
        } else {
            UserDTO userDTO = userService.getById(message.getChatId());
            userDTO.setStep(UserStep.ACCEPTING_RESUME);
            userService.update(userDTO);
            sendMessage.setText("\uD83D\uDD30 Rezyume \uD83D\uDD30\n\n" + "\uD83D\uDC64 Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiya : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return sendMessage;
    }

    public EditMessageText setResumeRegion(CallbackQuery callbackQuery) {
        /** set region to vacancy */
        MapRepository.currentResume.get(callbackQuery.getFrom().getId()).setWorkRegion(callbackQuery.getData());
        /**  remove regions and show districts BUTTONS  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("\uD83D\uDD30 Rezyume joylash \uD83D\uDD30\n\n\uD83D\uDC64 Ism : " + MapRepository.currentResume.get(callbackQuery.getFrom().getId()).getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + callbackQuery.getData() + ",  ...");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        /**  . .make distinct buttons  */
        editMessageText.setReplyMarkup(InlineKeyBoardUtil.districtButtons(callbackQuery));
        return editMessageText;
    }

    public EditMessageText setResumeDistinct(CallbackQuery callbackQuery) {
        /** set distinct to vacancy */
        MapRepository.currentResume.get(callbackQuery.getFrom().getId()).setWorkDistinct(callbackQuery.getData());
        /**  get currentResume */
        ResumeDTO dto = MapRepository.currentResume.get(callbackQuery.getFrom().getId());
        //...............................................//
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        if (dto.getSpecialty1() == null) {
            /**  remove districts button and show specialty buttons  */
            editMessageText.setText("\uD83D\uDD30 Rezyume joylash \uD83D\uDD30\n\n\uD83D\uDC64 Ism" + " : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + callbackQuery.getData() + "\n\uD83D\uDCCB Yo'nalish :   ...");
            /**  . .make specialty buttons(1)  */
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.specialtyButtons());
        } else {
            UserDTO userDTO = userService.getById(callbackQuery.getFrom().getId());
            userDTO.setStep(UserStep.ACCEPTING_RESUME);
            userService.update(userDTO);
            editMessageText.setText("\uD83D\uDD30 Rezyume \uD83D\uDD30\n\n" + "\uD83D\uDC64 Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return editMessageText;
    }

    public EditMessageText setSpecialty1(CallbackQuery callbackQuery) {
        /** set specialty to vacancy */
        MapRepository.currentResume.get(callbackQuery.getFrom().getId()).setSpecialty1(callbackQuery.getData());
        /**  remove Sspecialty(1) button and show specialty(2) buttons  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        ResumeDTO dto = MapRepository.currentResume.get(callbackQuery.getFrom().getId());
        //...............................................................//
        editMessageText.setText("\uD83D\uDD30 Rezyume joylash \uD83D\uDD30\n\n\uD83D\uDC64 Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + callbackQuery.getData() + ", ...");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        /**  . .make specialty(2) buttons  */
        editMessageText.setReplyMarkup(InlineKeyBoardUtil.chooseSpecialty2FromCallBackQuery(callbackQuery));
        return editMessageText;
    }

    public EditMessageText setSpecialty2(CallbackQuery callbackQuery) {
        /** set specialty to vacancy */
        MapRepository.currentResume.get(callbackQuery.getFrom().getId()).setSpecialty2(callbackQuery.getData());
        /**  get currentResume */
        ResumeDTO dto = MapRepository.currentResume.get(callbackQuery.getFrom().getId());
        //...............................................//
        /**  remove Sspecialty(1) button and show specialty(2) buttons  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        if (dto.getTechnologies() == null) {
            editMessageText.setText("\uD83D\uDD30 Rezyume joylash \uD83D\uDD30\n\n\uD83D\uDC64 Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + callbackQuery.getData() + "\n\n❇\uFE0F Qaysi texnologiyalarni bilasiz ?\n❇\uFE0F Kasbga oid qanday ko'nikmalarga egasiz ? \uD83D\uDD30 . . . . \uD83D\uDD30");
            editMessageText.setReplyMarkup(null);
        } else {
            UserDTO userDTO = userService.getById(callbackQuery.getFrom().getId());
            userDTO.setStep(UserStep.ACCEPTING_RESUME);
            userService.update(userDTO);
            editMessageText.setText("\uD83D\uDD30 Rezyume \uD83D\uDD30\n\n" + "\uD83D\uDC64 Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return editMessageText;
    }

    public SendMessage setTechnologies(Message message) {
        /**   get currentResume */
        ResumeDTO dto = MapRepository.currentResume.get(message.getChatId());
        //.......................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        if (message.getText().length() < 31) {
            sendMessage.setText("⚠\uFE0F Rezyume aniq va tushunarli bo'lishi uchun ko'proq ma'lumot kiriting.\n\n✍\uD83C\uDFFB . . .");
        } else {
            /**  set technologies to vacancy */
            MapRepository.currentResume.get(message.getChatId()).setTechnologies(message.getText());
            if (dto.getSalary() == null) {
                /**  "enter work time" msg */
                sendMessage.setText("\uD83D\uDD5E Haftalik ish soati . .");
            } else {
                UserDTO userDTO = userService.getById(message.getChatId());
                userDTO.setStep(UserStep.ACCEPTING_RESUME);
                userService.update(userDTO);
                sendMessage.setText("\uD83D\uDD30 Rezyume \uD83D\uDD30\n\n" + "\uD83D\uDC64 Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
                sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
            }
        }
        return sendMessage;
    }

    public SendMessage setWorkTime(Message message) {
        /**   get currentResume */
        ResumeDTO dto = MapRepository.currentResume.get(message.getChatId());
        //.......................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        if (userService.isDigit(message.getText())) {
            if (Integer.parseInt(message.getText()) >= 7 && Integer.parseInt(message.getText()) <= 70) {
                /** set setWorkTime to vacancy */
                MapRepository.currentResume.get(message.getChatId()).setWorkTime(message.getText());
                if (dto.getSalary() == null) {
                    /**  enter salary msg */
                    sendMessage.setText(" \uD83D\uDD30  Oylik maoshni kiriting  \uD83D\uDD30\n\n‼\uFE0F  so'm yoki dollarda aniq qilib kiriting.");
                } else {
                    UserDTO userDTO = userService.getById(message.getChatId());
                    userDTO.setStep(UserStep.ACCEPTING_RESUME);
                    userService.update(userDTO);
                    sendMessage.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83D\uDC64 Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
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
        /**   get currentResume */
        ResumeDTO dto = MapRepository.currentResume.get(message.getChatId());
        //..............................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        /** set salary to vacancy */
        MapRepository.currentResume.get(message.getChatId()).setSalary(message.getText());
        /**  enter call link msg */
        if (dto.getConnectAddress() == null) {
            sendMessage.setText("\uD83D\uDCE8  Aloqaga chiqish uchun link yoki tel raqam yozib qoldiring.");
        } else {
            UserDTO userDTO = userService.getById(message.getChatId());
            userDTO.setStep(UserStep.ACCEPTING_RESUME);
            userService.update(userDTO);
            sendMessage.setText("\uD83D\uDD30 Rezyume \uD83D\uDD30\n\n" + "\uD83D\uDC64 Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDDFA Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return sendMessage;
    }

    public SendMessage setConnectAddress(Message message) {
        /**   get currentResume */
        ResumeDTO dto = MapRepository.currentResume.get(message.getChatId());
        //......................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        /** set connect address to vacancy */
        MapRepository.currentResume.get(message.getChatId()).setConnectAddress(message.getText());
        if (dto.getExtraInfo() == null) {
            /**  enter extra info msg */
            sendMessage.setText("‼\uFE0F Ish beruvchidan nimalarni talab qilishingiz, o'z ish tajribangiz darajasi,  korxonaga nimalarni taklif qila olasiz va o'zingiz haqida ba'zi ma'lumotlar . .\n" + "Shu kabi ma'lumotlarni kiritishingizni iltimos qilamiz.\n\n✍\uD83C\uDFFB . . .");
        } else {
            UserDTO userDTO = userService.getById(message.getChatId());
            userDTO.setStep(UserStep.ACCEPTING_RESUME);
            userService.update(userDTO);
            sendMessage.setText("\uD83D\uDD30 Rezyume \uD83D\uDD30\n\n" + "\uD83D\uDC64 Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return sendMessage;
    }

    public SendMessage acceptingResume(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        /**  check extra info to 128 sybols */
        if (message.getText().length() >= 31) {
            /**  set extra info to vacancy */
            MapRepository.currentResume.get(message.getChatId()).setExtraInfo(message.getText());
            ResumeDTO dto = MapRepository.currentResume.get(message.getChatId());
            sendMessage.setText("\uD83D\uDD30 Rezyume \uD83D\uDD30\n\n" + "\uD83D\uDC64 Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
            /** change user's step */
            UserDTO userDTO = userService.getById(message.getChatId());
            userDTO.setStep(UserStep.ACCEPTING_RESUME);
            userService.update(userDTO);
        } else {
            sendMessage.setText("‼\uFE0F  Rezyume tushunarli va aniq bo'lishi uchun ko'proq ma'lumot kiriting.\n✍\uD83C\uDFFB . . .");
        }
        return sendMessage;
    }

    public EditMessageText editResumeButtons(CallbackQuery callbackQuery) {
        /** change user's step */
        UserDTO user = userService.getById(callbackQuery.getFrom().getId());
        user.setStep(UserStep.EDIT_RESUME);
        userService.update(user);
        /**  get currentResume to DTO */
        ResumeDTO dto = MapRepository.currentResume.get(callbackQuery.getFrom().getId());
        /** send total Vacancy Msg  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("  \uD83D\uDD30 Rezyume \uD83D\uDD30\n\n\uD83D\uDC64 Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E  Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDD30 Qaysi birini tahrirlamoqchisiz ? \uD83D\uDD30");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setReplyMarkup(InlineKeyBoardUtil.editingResumeButtons());
        return editMessageText;
    }

    public EditMessageText editResume(CallbackQuery callbackQuery) {
        /** change user's step to ADD_VACANCY */
        UserDTO user = userService.getById(callbackQuery.getFrom().getId());
        user.setStep(UserStep.ADD_RESUME);
        userService.update(user);
        //...............................................//
        String data = callbackQuery.getData();
        //...............................................//
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        if (data.equals("employeeName")) {
            MapRepository.currentResume.get(callbackQuery.getFrom().getId()).setEmployeeName(null);
            editMessageText.setText("\uD83D\uDC64 Ism : ...");
        } else if (data.equals("address")) {
            MapRepository.currentResume.get(callbackQuery.getFrom().getId()).setWorkRegion(null);
            MapRepository.currentResume.get(callbackQuery.getFrom().getId()).setWorkDistinct(null);
            editMessageText.setText("\uD83D\uDDFA  Manzil : ...");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.regionsButtons());
        } else if (data.equals("specialty")) {
            MapRepository.currentResume.get(callbackQuery.getFrom().getId()).setSpecialty1(null);
            MapRepository.currentResume.get(callbackQuery.getFrom().getId()).setSpecialty2(null);
            editMessageText.setText("\uD83D\uDCCB  Yo'nalish : ...");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.specialtyButtons());
        } else if (data.equals("technologies")) {
            MapRepository.currentResume.get(callbackQuery.getFrom().getId()).setTechnologies(null);
            editMessageText.setText("❇\uFE0F  Texnologiyalar : ...");
        } else if (data.equals("workTime")) {
            MapRepository.currentResume.get(callbackQuery.getFrom().getId()).setWorkTime(null);
            editMessageText.setText("\uD83D\uDD5E  Haftalik ish soati : ...");
        } else if (data.equals("salary")) {
            MapRepository.currentResume.get(callbackQuery.getFrom().getId()).setSalary(null);
            editMessageText.setText("\uD83D\uDCB0  Maosh : ...");
        } else if (data.equals("callAddress")) {
            MapRepository.currentResume.get(callbackQuery.getFrom().getId()).setConnectAddress(null);
            editMessageText.setText("\uD83D\uDCE8  Aloqaga chiqish uchun link yoki tel raqam yozib qoldiring.");
        } else if (data.equals("extraInfo")) {
            MapRepository.currentResume.get(callbackQuery.getFrom().getId()).setExtraInfo(null);
            editMessageText.setText("‼\uFE0F  Ish beruvchidan nimalarni talab qilishingiz, o'z ish tajribangiz darajasi,  korxonaga nimalarni taklif qila olasiz va o'zingiz haqida ba'zi ma'lumotlar . .\n" + "Shu kabi ma'lumotlarni kiritishingizni iltimos qilamiz.\n\n✍\uD83C\uDFFB . . .");
        }
        return editMessageText;
    }

    public EditMessageText cancelResume(Message message) {
        /** change user's step */
        UserDTO user = userService.getById(message.getChatId());
        user.setStep(UserStep.END);
        userService.update(user);
        /** removing vacancyTotalMsg */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(message.getChatId());
        editMessageText.setText("❌  Rezyume bekor qilindi.");
        editMessageText.setMessageId(message.getMessageId());
        /**  remove vacancy from HashMap   */
        MapRepository.currentResume.remove(message.getChatId());
        return editMessageText;
    }

    public synchronized ResumeDTO save(CallbackQuery callbackQuery) {
        /** change user's step */
        UserDTO user = userService.getById(callbackQuery.getFrom().getId());
        user.setStep(UserStep.END);
        userService.update(user);
        /**  get currentResume to DTO */
        ResumeDTO dto = MapRepository.currentResume.get(callbackQuery.getFrom().getId());
        /**  create vacancyEntity */
        ResumeEntity entity = new ResumeEntity();
        /** setting vacancy's fields */
        entity.setEmployeeId(callbackQuery.getFrom().getId());
        entity.setEmployeeName(dto.getEmployeeName());
        entity.setSpecialty1(dto.getSpecialty1());
        entity.setSpecialty2(dto.getSpecialty2());
        entity.setWorkTime(dto.getWorkTime());
        entity.setWorkRegion(dto.getWorkRegion());
        entity.setWorkDistinct(dto.getWorkDistinct());
        entity.setTechnologies(dto.getTechnologies());
        entity.setSalary(dto.getSalary());
        entity.setConnectAddress(dto.getConnectAddress());
        entity.setExtraInfo(dto.getExtraInfo());
        //.....................................//
        String createdDate = String.valueOf(LocalDateTime.now());
        String s = createdDate.substring(0, 10) + " " + createdDate.substring(11, 16);
        entity.setCreatedDate(s);
        entity = resumeRepository.save(entity);
        dto.setId(entity.getId());
        dto.setCreatedDate(entity.getCreatedDate());
        /**  remove vacancy from HashMap    */
        MapRepository.currentResume.remove(callbackQuery.getFrom().getId());
        return dto;
    }

    public EditMessageText delete(CallbackQuery callbackQuery) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        if (callbackQuery.getData().startsWith("getDeletingResume")) {
            Integer resumeId = Integer.parseInt(callbackQuery.getData().substring(17));
            Optional<ResumeEntity> optional = resumeRepository.findById(resumeId);
            ResumeEntity entity = optional.get();
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30 Rezyume \uD83D\uDD30\n\n\uD83D\uDC64 Ism : " + entity.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + entity.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + entity.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + entity.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + entity.getExtraInfo() + "\n\n\uD83D\uDD30 Rezyume tizimdan o'chirilishiga rozimisiz ? \uD83D\uDD30");
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Ha, roziman", "getDeleteResume" + entity.getId())), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Bekor qilish", "getLessResume" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("getDeleteResume")) {
            Integer resumeId = Integer.parseInt(callbackQuery.getData().substring(15));
            //..................................................//
            Optional<ResumeEntity> optional = resumeRepository.findById(resumeId);
            ResumeEntity entity = optional.get();
            /**   change vacancy's status to DELETED  */
            resumeRepository.changeStatus(resumeId, GeneralStatus.DELETED);
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30  Rezyume  \uD83D\uDD30" + "\n\uD83D\uDC64 Ism : " + entity.getEmployeeName() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\n❌ Rezyume tizimdan o'chirildi !");
        }
        return editMessageText;
    }

    public List<SendMessage> sendingResumeToEmployers(ResumeDTO dto) {
        List<Long> employerList = vacancyRepository.getEmployerIdByWorkRegionAndSpecialty2AndStatus(dto.getWorkRegion(), dto.getSpecialty2(), GeneralStatus.ACTIVE);
        List<SendMessage> sendMessageList = new LinkedList<>();
        for (int i = 0; i < employerList.size(); i++) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(employerList.get(i));
            sendMessage.setText("#" + dto.getId() + "  \uD83D\uDD30  Rezyume  \uD83D\uDD30" + "\n\uD83D\uDC64 Ism : " + dto.getEmployeeName() + "\n❇\uFE0F Texnologiyalar : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDDD3 Created date : " + dto.getCreatedDate());
            sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("To'liq ko'rsatish", "getMoreNewResume" + dto.getId())))));
            sendMessageList.add(sendMessage);
        }
        return sendMessageList;
    }
}
