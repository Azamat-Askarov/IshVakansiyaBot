package uz.code.ishvakansiyabot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.code.ishvakansiyabot.dto.ResumeDTO;
import uz.code.ishvakansiyabot.dto.SearcherDTO;
import uz.code.ishvakansiyabot.dto.UserDTO;
import uz.code.ishvakansiyabot.entity.ResumeEntity;
import uz.code.ishvakansiyabot.enums.GeneralStatus;
import uz.code.ishvakansiyabot.enums.SearchMethodType;
import uz.code.ishvakansiyabot.enums.SearchPostType;
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
        /** add resume to HashMap */
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
            /**  get resumeId from callBackQuery */
            resumeId = Integer.parseInt(callbackQuery.getData().substring(13));
            /**  get resumeEntity from DB */
            Optional<ResumeEntity> optional = resumeRepository.findById(resumeId);
            ResumeEntity entity = optional.get();
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30 Rezyume \uD83D\uDD30\n\n\uD83D\uDC64 Ism : " + entity.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + entity.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + entity.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + entity.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + entity.getExtraInfo() + "\n\n《《   @IshVakansiyaBot   》》");
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Rezyumeni o'chirish", "getDeletingResume" + entity.getId())), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Xabarni qisqartirish", "getLessResume" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("getLessResume")) {
            /**  get resumeId from callBackQuery */
            resumeId = Integer.parseInt(callbackQuery.getData().substring(13));
            /**  get resumeEntity from DB */
            Optional<ResumeEntity> optional = resumeRepository.findById(resumeId);
            ResumeEntity entity = optional.get();
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30  Rezyume  \uD83D\uDD30" + "\n\uD83D\uDC64 Ism : " + entity.getEmployeeName() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDDD3 Created Date : " + entity.getCreatedDate());
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Batafsil", "getMoreResume" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("getMoreNewResume")) {
            /**  get resumeId from callBackQuery */
            resumeId = Integer.parseInt(callbackQuery.getData().substring(16));
            /**  get resumeEntity from DB */
            Optional<ResumeEntity> optional = resumeRepository.findById(resumeId);
            ResumeEntity entity = optional.get();
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30 Rezyume \uD83D\uDD30\n\n\uD83D\uDC64 Ism : " + entity.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + entity.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + entity.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + entity.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + entity.getExtraInfo() + "\n\n《《   @IshVakansiyaBot   》》");
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Xabarni qisqartirish", "getLessNewResume" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("getLessNewResume")) {
            /**  get resumeId from callBackQuery */
            resumeId = Integer.parseInt(callbackQuery.getData().substring(16));
            /**  get resumeEntity from DB */
            Optional<ResumeEntity> optional = resumeRepository.findById(resumeId);
            ResumeEntity entity = optional.get();
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30  Rezyume  \uD83D\uDD30" + "\n\uD83D\uDC64 Ism : " + entity.getEmployeeName() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDDD3 Created Date : " + entity.getCreatedDate());
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Batafsil", "getMoreNewResume" + entity.getId())))));
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
            sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Batafsil", "getMoreResume" + entity.getId())))));
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
        /** set region to resume */
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
        /** set distinct to resume */
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
        /** set specialty to resume */
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
        /** set specialty to resume */
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
            /**  set technologies to resume */
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
                /** set setWorkTime to resume */
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
        /** set salary to resume */
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
        /** set connect address to resume */
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
            /**  set extra info to resume */
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
        /** send total resume Msg  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("  \uD83D\uDD30 Rezyume \uD83D\uDD30\n\n\uD83D\uDC64 Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E  Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDD30 Qaysi birini tahrirlamoqchisiz ? \uD83D\uDD30");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setReplyMarkup(InlineKeyBoardUtil.editingResumeButtons());
        return editMessageText;
    }

    public EditMessageText editResume(CallbackQuery callbackQuery) {
        /** change user's step to ADD_RESUME */
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
        /** removing resumeTotalMsg */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(message.getChatId());
        editMessageText.setText("❌  Rezyume bekor qilindi.");
        editMessageText.setMessageId(message.getMessageId());
        /**  remove resume from HashMap   */
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
        /**  create resumeEntity */
        ResumeEntity entity = new ResumeEntity();
        /** setting resume's fields */
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
        /**  remove resume from HashMap    */
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
            /**   change resume's status to DELETED  */
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
            sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Batafsil", "getMoreNewResume" + dto.getId())))));
            sendMessageList.add(sendMessage);
        }
        return sendMessageList;
    }

    public SendMessage createSearchMethod(Long userId) {
        SearcherDTO searcherDTO = MapRepository.currentSearcherMap.get(userId);
        if (searcherDTO != null) {
            MapRepository.currentSearcherMap.remove(userId);
        }
        searcherDTO = new SearcherDTO();
        searcherDTO.setUserId(userId);
        searcherDTO.setSearchPostType(SearchPostType.RESUME);
        userService.changeStep(userId, UserStep.SEARCH_RESUME);
        MapRepository.currentSearcherMap.put(userId, searcherDTO);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText("\uD83D\uDD30  Kerakli filtrni tanlang  \uD83D\uDD30");
        sendMessage.setReplyMarkup(InlineKeyBoardUtil.searchButtons());
        return sendMessage;
    }

    public EditMessageText search(CallbackQuery callbackQuery) {
        User userDTO = callbackQuery.getFrom();
        //.....................................//
        SearchMethodType searchMethodType = MapRepository.currentSearcherMap.get(userDTO.getId()).getSearchMethodType();
        //.....................................//
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(userDTO.getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        if (callbackQuery.getData().equals("search1")) {
            MapRepository.currentSearcherMap.get(userDTO.getId()).setSearchMethodType(SearchMethodType.SEARCH1);
            editMessageText.setText("❇️ Yo'nalish : . . .");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.specialtyButtons());
        } else if (callbackQuery.getData().equals("search2")) {
            MapRepository.currentSearcherMap.get(userDTO.getId()).setSearchMethodType(SearchMethodType.SEARCH2);
            editMessageText.setText("❇️ Yo'nalish : . . .");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.specialtyButtons());
        } else if (callbackQuery.getData().equals("search3")) {
            MapRepository.currentSearcherMap.get(userDTO.getId()).setSearchMethodType(SearchMethodType.SEARCH3);
            editMessageText.setText("❇️ Yo'nalish : . . .");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.specialtyButtons());
        } else if (searchMethodType.equals(SearchMethodType.SEARCH1)) {
            if (MapRepository.currentSearcherMap.get(userDTO.getId()).getSpecialty1() == null) {
                MapRepository.currentSearcherMap.get(userDTO.getId()).setSpecialty1(callbackQuery.getData());
                editMessageText.setText("❇️ Yo'nalish : " + MapRepository.currentSearcherMap.get(userDTO.getId()).getSpecialty1() + ", . . .");
                editMessageText.setReplyMarkup(InlineKeyBoardUtil.chooseSpecialty2FromCallBackQuery(callbackQuery));
            } else if (MapRepository.currentSearcherMap.get(userDTO.getId()).getSpecialty2() == null) {
                MapRepository.currentSearcherMap.get(userDTO.getId()).setSpecialty2(callbackQuery.getData());
                editMessageText = searchResumeTotalMsg(callbackQuery);
                userService.changeStep(userDTO.getId(), UserStep.END);
            }
        } else if (searchMethodType.equals(SearchMethodType.SEARCH2)) {
            if (MapRepository.currentSearcherMap.get(userDTO.getId()).getSpecialty1() == null) {
                MapRepository.currentSearcherMap.get(userDTO.getId()).setSpecialty1(callbackQuery.getData());
                editMessageText.setText("❇️ Yo'nalish : " + MapRepository.currentSearcherMap.get(userDTO.getId()).getSpecialty1() + ", . . .");
                editMessageText.setReplyMarkup(InlineKeyBoardUtil.chooseSpecialty2FromCallBackQuery(callbackQuery));
            } else if (MapRepository.currentSearcherMap.get(userDTO.getId()).getSpecialty2() == null) {
                MapRepository.currentSearcherMap.get(userDTO.getId()).setSpecialty2(callbackQuery.getData());
                editMessageText.setText("❇️ Yo'nalish : " + MapRepository.currentSearcherMap.get(userDTO.getId()).getSpecialty1() + ", " + MapRepository.currentSearcherMap.get(userDTO.getId()).getSpecialty2() + "\n\uD83D\uDDFA Manzil : . . .");
                editMessageText.setReplyMarkup(InlineKeyBoardUtil.regionsButtons());
            } else if (MapRepository.currentSearcherMap.get(userDTO.getId()).getRegion() == null) {
                MapRepository.currentSearcherMap.get(userDTO.getId()).setRegion(callbackQuery.getData());
                editMessageText = searchResumeTotalMsg(callbackQuery);
                userService.changeStep(userDTO.getId(), UserStep.END);
            }
        } else if (searchMethodType.equals(SearchMethodType.SEARCH3)) {
            if (MapRepository.currentSearcherMap.get(userDTO.getId()).getSpecialty1() == null) {
                MapRepository.currentSearcherMap.get(userDTO.getId()).setSpecialty1(callbackQuery.getData());
                editMessageText.setText("❇️ Yo'nalish : " + MapRepository.currentSearcherMap.get(userDTO.getId()).getSpecialty1() + ", . . .");
                editMessageText.setReplyMarkup(InlineKeyBoardUtil.chooseSpecialty2FromCallBackQuery(callbackQuery));
            } else if (MapRepository.currentSearcherMap.get(userDTO.getId()).getSpecialty2() == null) {
                MapRepository.currentSearcherMap.get(userDTO.getId()).setSpecialty2(callbackQuery.getData());
                editMessageText.setText("❇️ Yo'nalish : " + MapRepository.currentSearcherMap.get(userDTO.getId()).getSpecialty1() + ", " + MapRepository.currentSearcherMap.get(userDTO.getId()).getSpecialty2() + "\n\uD83D\uDDFA Manzil : ");
                editMessageText.setReplyMarkup(InlineKeyBoardUtil.regionsButtons());
            } else if (MapRepository.currentSearcherMap.get(userDTO.getId()).getRegion() == null) {
                MapRepository.currentSearcherMap.get(userDTO.getId()).setRegion(callbackQuery.getData());
                editMessageText.setText("❇️ Yo'nalish : " + MapRepository.currentSearcherMap.get(userDTO.getId()).getSpecialty1() + ", " + MapRepository.currentSearcherMap.get(userDTO.getId()).getSpecialty2() + "\n\n\uD83D\uDDFA Manzil : " + MapRepository.currentSearcherMap.get(userDTO.getId()).getRegion() + ", . . .");
                editMessageText.setReplyMarkup(InlineKeyBoardUtil.districtButtons(callbackQuery));
            } else if (MapRepository.currentSearcherMap.get(userDTO.getId()).getDistinct() == null) {
                MapRepository.currentSearcherMap.get(userDTO.getId()).setDistinct(callbackQuery.getData());
                editMessageText = searchResumeTotalMsg(callbackQuery);
                userService.changeStep(userDTO.getId(), UserStep.END);
            }
        }
        return editMessageText;
    }

    public EditMessageText searchResumeTotalMsg(CallbackQuery callbackQuery) {
        if (callbackQuery.getData().startsWith("getPageNext")) {
            int i = Integer.parseInt(callbackQuery.getData().substring(11));
            MapRepository.currentSearcherMap.get(callbackQuery.getFrom().getId()).setCurrentResumePageIndex(i + 1);
        } else if (callbackQuery.getData().startsWith("getPageBack")) {
            int i = Integer.parseInt(callbackQuery.getData().substring(11));
            MapRepository.currentSearcherMap.get(callbackQuery.getFrom().getId()).setCurrentResumePageIndex(i - 1);
        }
        SearcherDTO searcherDTO = MapRepository.currentSearcherMap.get(callbackQuery.getFrom().getId());
        int currentPageIndex = searcherDTO.getCurrentResumePageIndex();
        List<ResumeDTO> resultList = getResultBySearchMethods(searcherDTO);
        Long userId = searcherDTO.getUserId();
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(userId);
        /**   make resumeListOfCurrentPage */
        List<ResumeDTO> currentPageList = new LinkedList<>();
        if (!resultList.isEmpty()) {
            for (int i = 0; i < 10; i++) {
                if (resultList.size() == (currentPageIndex * 10) + i) {
                    break;
                }
                currentPageList.add(resultList.get((currentPageIndex * 10) + i));
            }
        }
        int totalElementSize = MapRepository.searchResumeResultMap.get(userId).size();
        String msg = "Natijalar :  " + (currentPageIndex * 10 + 1) + " - " + (currentPageIndex * 10 + currentPageList.size()) + " / " + totalElementSize + "\n\n";
        /**   make resultText */
        if (resultList.isEmpty()) {
            msg = "Bu filtr bo'yicha rezyumelar topilmadi !";
        } else {
            for (int i = 0; i < 10; i++) {
                if (currentPageList.size() == i) {
                    break;
                }
                //ResumeDTO dto= new ResumeDTO();
                ResumeDTO dto;
                dto = currentPageList.get(i);
                msg += (i + 1) + ". Ism : " + dto.getEmployeeName() + ",   Texnologiya : " + dto.getTechnologies() + "\n";
            }
        }
        /**   make searchResultButtons */
        editMessageText.setText(msg);
        InlineKeyboardMarkup inlineKeyboardMarkup = searchResultButtons(currentPageList, searcherDTO.getCurrentResumePageIndex(), resultList.size());
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        return editMessageText;
    }

    public List<ResumeDTO> getResultBySearchMethods(SearcherDTO searcherDTO) {
        SearchMethodType searchMethodType = searcherDTO.getSearchMethodType();
        List<ResumeEntity> resumeEntityList = new LinkedList<>();
        if (searchMethodType.equals(SearchMethodType.SEARCH1)) {
            resumeEntityList = resumeRepository.findBySpecialty2AndStatus(searcherDTO.getSpecialty2(), GeneralStatus.ACTIVE);
        } else if (searchMethodType.equals(SearchMethodType.SEARCH2)) {
            resumeEntityList = resumeRepository.findByWorkRegionAndSpecialty2AndStatus(searcherDTO.getRegion(), searcherDTO.getSpecialty2(), GeneralStatus.ACTIVE);
        } else if (searchMethodType.equals(SearchMethodType.SEARCH3)) {
            resumeEntityList = resumeRepository.findByWorkDistinctAndSpecialty2AndStatus(searcherDTO.getDistinct(), searcherDTO.getSpecialty2(), GeneralStatus.ACTIVE);
        }
        List<ResumeDTO> resumeDTOList = new LinkedList<>();
        for (ResumeEntity entity : resumeEntityList) {
            ResumeDTO dto = new ResumeDTO();
            dto.setId(entity.getId());
            dto.setEmployeeId(entity.getEmployeeId());
            dto.setStatus(entity.getStatus());
            dto.setCreatedDate(entity.getCreatedDate());
            dto.setEmployeeName(entity.getEmployeeName());
            dto.setSpecialty1(entity.getSpecialty1());
            dto.setSpecialty2(entity.getSpecialty2());
            dto.setTechnologies(entity.getTechnologies());
            dto.setSalary(entity.getSalary());
            dto.setWorkRegion(entity.getWorkRegion());
            dto.setWorkDistinct(entity.getWorkDistinct());
            dto.setConnectAddress(entity.getConnectAddress());
            dto.setWorkTime(entity.getWorkTime());
            dto.setExtraInfo(entity.getExtraInfo());
            resumeDTOList.add(dto);
        }
        /** current result resume larni map ga saqlab qo'yish */
        MapRepository.searchResumeResultMap.put(searcherDTO.getUserId(), resumeDTOList);
        return resumeDTOList;
    }

    public InlineKeyboardMarkup searchResultButtons(List<ResumeDTO> currentPageList, int pageIndex, int sizeOfResultList) {
        int buttonRowSize = currentPageList.size() / 5;
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        for (int i = 0; i <= buttonRowSize; i++) {
            List<InlineKeyboardButton> row = new LinkedList<>();
            for (int j = 0; j < 5; j++) {
                if (currentPageList.size() == 5 * i + j) {
                    break;
                }
                InlineKeyboardButton button = InlineKeyboardButtonUtil.button(String.valueOf((5 * i) + j + 1), "getLessSearchResume" + currentPageList.get(5 * i + j).getId());
                row.add(button);
            }
            rowList.add(row);
        }
        List<InlineKeyboardButton> row = new LinkedList<>();
        if (pageIndex > 0) {
            InlineKeyboardButton button = InlineKeyBoardUtil.button("⬅\uFE0F", "getPageBack" + pageIndex);
            row.add(button);
        }
        if (pageIndex < sizeOfResultList / 10) {
            InlineKeyboardButton button = InlineKeyBoardUtil.button("➡\uFE0F", "getPageNext" + pageIndex);
            row.add(button);
        }
        rowList.add(row);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public SendMessage getSearchResultResume(CallbackQuery callbackQuery) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(callbackQuery.getFrom().getId());
        Integer resumeId = Integer.parseInt(callbackQuery.getData().substring(19));
        /**  get resumeEntity from DB */
        Optional<ResumeEntity> optional = resumeRepository.findById(resumeId);
        ResumeEntity entity = optional.get();
        /**  get created date */
        String date = String.valueOf(entity.getCreatedDate());
        sendMessage.setText("#" + entity.getId() + "  \uD83D\uDD30  Rezyume  \uD83D\uDD30" + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDDD3 Created Date : " + entity.getCreatedDate());
        sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Batafsil", "getMoreNewResume" + entity.getId())))));
        return sendMessage;
    }
}
