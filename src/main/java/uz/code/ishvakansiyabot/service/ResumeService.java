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
import uz.code.ishvakansiyabot.util.InlineKeyBoardUtil;
import uz.code.ishvakansiyabot.util.InlineKeyboardButtonUtil;

import java.util.*;

@Service
public class ResumeService {
    @Autowired
    MapRepository mapRepository;
    @Autowired
    ResumeRepository resumeRepository;
    @Autowired
    UserService userService;

    public SendMessage create(Long userId) {
        /** add vacancy to HashMap */
        ResumeDTO resume = new ResumeDTO();
        resume.setEmployeeId(userId);
        mapRepository.currentResume.put(userId, resume);
        /** change currentUser's step */
        UserDTO user = userService.getById(userId);
        user.setStep(UserStep.ADD_RESUME);
        userService.update(user);
        /**  show "enter employerName" msg  */
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("\uD83D\uDD30 Rezyume joylash \uD83D\uDD30\n\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC  Ism :  ...");
        sendMessage.setChatId(userId);
        /** Remove ReplyButtons */
        ReplyKeyboardRemove removeButton = new ReplyKeyboardRemove();
        removeButton.setSelective(true);
        removeButton.setRemoveKeyboard(true);
        sendMessage.setReplyMarkup(userService.cancelButton());
        return sendMessage;
    }

    public ResumeDTO toDTO(Integer id) {
        Optional<ResumeEntity> optional = resumeRepository.findById(id);
        ResumeEntity entity = optional.get();
        if (entity == null) {
            return null;
        }
        ResumeDTO dto = new ResumeDTO();
        dto.setId(entity.getId());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setEmployeeName(entity.getEmployeeName());
        dto.setStatus(entity.getStatus());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setSpecialty1(entity.getSpecialty1());
        dto.setSpecialty2(entity.getSpecialty2());
        dto.setTechnologies(entity.getTechnologies());
        dto.setSalary(entity.getSalary());
        dto.setWorkRegion(entity.getWorkRegion());
        dto.setWorkDistinct(entity.getWorkDistinct());
        dto.setConnectAddress(entity.getConnectAddress());
        dto.setExtraInfo(entity.getExtraInfo());
        return dto;
    }

    public EditMessageText getById(CallbackQuery callbackQuery) {
        /**  create editMsg */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        /**  get vacancyId from callBackQuery */
        Integer resumeId = Integer.parseInt(callbackQuery.getData().substring(13));
        /**  get resumeEntity from DB */
        Optional<ResumeEntity> optional = resumeRepository.findById(resumeId);
        ResumeEntity entity = null;
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (callbackQuery.getData().startsWith("getMoreResume")) {
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30 Rezyume \uD83D\uDD30\n\n\uD83D\uDC64 Ism : " + entity.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + entity.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + entity.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + entity.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + entity.getExtraInfo() + "\n\n《《   @IshVakansiyaBot   》》");
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Rezyumeni o'chirish", "getDeletingResume" + entity.getId())), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Xabarni qisqartirish", "getLessResume" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("getLessResume")) {
            /**  get created date */
            String date = String.valueOf(entity.getCreatedDate());
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30  Rezyume  \uD83D\uDD30" + "\n\uD83D\uDC64 Ism : " + entity.getEmployeeName() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDDD3 Created Date : " + date.substring(0, 10) + " " + date.substring(11, 16));
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("To'liq ko'rsatish", "getMoreResume" + entity.getId())))));
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
        mapRepository.currentResume.get(message.getChatId()).setEmployeeName(message.getText());
        /**   get currentResume */
        ResumeDTO dto = mapRepository.currentResume.get(message.getChatId());
        //.........................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        if (dto.getWorkRegion() == null) {
            sendMessage.setText("\uD83D\uDD30 Rezyume joylash \uD83D\uDD30\n\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism : " + message.getText() + "\n\uD83D\uDDFA Manzil :  ...");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.regionsButtons());
        } else {
            UserDTO userDTO = userService.getById(message.getChatId());
            userDTO.setStep(UserStep.ACCEPTING_RESUME);
            userService.update(userDTO);
            sendMessage.setText("\uD83D\uDD30 Rezyume \uD83D\uDD30\n\n" + "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiya : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return sendMessage;
    }

    public EditMessageText setResumeRegion(CallbackQuery callbackQuery) {
        /** set region to vacancy */
        mapRepository.currentResume.get(callbackQuery.getFrom().getId()).setWorkRegion(callbackQuery.getData());
        /**  remove regions and show districts BUTTONS  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("\uD83D\uDD30 Rezyume joylash \uD83D\uDD30\n\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism : " + mapRepository.currentResume.get(callbackQuery.getFrom().getId()).getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + callbackQuery.getData() + ",  ...");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        /**  . .make distinct buttons  */
        editMessageText.setReplyMarkup(InlineKeyBoardUtil.districtButtons(callbackQuery));
        return editMessageText;
    }

    public EditMessageText setResumeDistinct(CallbackQuery callbackQuery) {
        /** set distinct to vacancy */
        mapRepository.currentResume.get(callbackQuery.getFrom().getId()).setWorkDistinct(callbackQuery.getData());
        /**  get currentResume */
        ResumeDTO dto = mapRepository.currentResume.get(callbackQuery.getFrom().getId());
        //...............................................//
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        if (dto.getSpecialty1() == null) {
            /**  remove districts button and show specialty buttons  */
            editMessageText.setText("\uD83D\uDD30 Rezyume joylash \uD83D\uDD30\n\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism" + " : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + callbackQuery.getData() + "\n\uD83D\uDCCB Yo'nalish :   ...");
            /**  . .make specialty buttons(1)  */
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.specialtyButtons());
        } else {
            UserDTO userDTO = userService.getById(callbackQuery.getFrom().getId());
            userDTO.setStep(UserStep.ACCEPTING_RESUME);
            userService.update(userDTO);
            editMessageText.setText("\uD83D\uDD30 Rezyume \uD83D\uDD30\n\n" + "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return editMessageText;
    }

    public EditMessageText setSpecialty1(CallbackQuery callbackQuery) {
        /** set specialty to vacancy */
        mapRepository.currentResume.get(callbackQuery.getFrom().getId()).setSpecialty1(callbackQuery.getData());
        /**  remove Sspecialty(1) button and show specialty(2) buttons  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        ResumeDTO dto = mapRepository.currentResume.get(callbackQuery.getFrom().getId());
        //...............................................................//
        editMessageText.setText("\uD83D\uDD30 Rezyume joylash \uD83D\uDD30\n\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + callbackQuery.getData() + ", ...");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        /**  . .make specialty(2) buttons  */
        editMessageText.setReplyMarkup(InlineKeyBoardUtil.chooseSpecialty2FromCallBackQuery(callbackQuery));
        return editMessageText;
    }

    public EditMessageText setSpecialty2(CallbackQuery callbackQuery) {
        /** set specialty to vacancy */
        mapRepository.currentResume.get(callbackQuery.getFrom().getId()).setSpecialty2(callbackQuery.getData());
        /**  get currentResume */
        ResumeDTO dto = mapRepository.currentResume.get(callbackQuery.getFrom().getId());
        //...............................................//
        /**  remove Sspecialty(1) button and show specialty(2) buttons  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        if (dto.getTechnologies() == null) {
            editMessageText.setText("\uD83D\uDD30 Rezyume joylash \uD83D\uDD30\n\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + callbackQuery.getData() + "\n\n❇\uFE0F Qaysi texnologiyalarni bilasiz ?\n❇\uFE0F Kasbga oid qanday ko'nikmalarga egasiz ? \uD83D\uDD30 . . . . \uD83D\uDD30");
            editMessageText.setReplyMarkup(null);
        } else {
            UserDTO userDTO = userService.getById(callbackQuery.getFrom().getId());
            userDTO.setStep(UserStep.ACCEPTING_RESUME);
            userService.update(userDTO);
            editMessageText.setText("\uD83D\uDD30 Rezyume \uD83D\uDD30\n\n" + "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return editMessageText;
    }

    public SendMessage setTechnologies(Message message) {
        /**   get currentResume */
        ResumeDTO dto = mapRepository.currentResume.get(message.getChatId());
        //.......................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        if (message.getText().length() < 32) {
            sendMessage.setText("⚠\uFE0F Rezyume aniq va tushunarli bo'lishi uchun ko'proq ma'lumot kiriting.\n\n✍\uD83C\uDFFB . . .");
        } else {
            /**  set technologies to vacancy */
            mapRepository.currentResume.get(message.getChatId()).setTechnologies(message.getText());
            if (dto.getSalary() == null) {
                /**  "enter work time" msg */
                sendMessage.setText("\uD83D\uDD5E Haftalik ish soati . .");
            } else {
                UserDTO userDTO = userService.getById(message.getChatId());
                userDTO.setStep(UserStep.ACCEPTING_RESUME);
                userService.update(userDTO);
                sendMessage.setText("\uD83D\uDD30 Rezyume \uD83D\uDD30\n\n" + "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
                sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
            }
        }
        return sendMessage;
    }

    public SendMessage setWorkTime(Message message) {
        /**   get currentResume */
        ResumeDTO dto = mapRepository.currentResume.get(message.getChatId());
        //.......................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        if (userService.isDigit(message.getText())) {
            if (Integer.parseInt(message.getText()) >= 7 && Integer.parseInt(message.getText()) <= 70) {
                /** set setWorkTime to vacancy */
                mapRepository.currentResume.get(message.getChatId()).setWorkTime(message.getText());
                if (dto.getSalary() == null) {
                    /**  enter salary msg */
                    sendMessage.setText(" \uD83D\uDD30  Oylik maoshni kiriting  \uD83D\uDD30\n\n‼\uFE0F  so'm yoki dollarda aniq qilib kiriting.");
                } else {
                    UserDTO userDTO = userService.getById(message.getChatId());
                    userDTO.setStep(UserStep.ACCEPTING_RESUME);
                    userService.update(userDTO);
                    sendMessage.setText("\uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n" + "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
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
        ResumeDTO dto = mapRepository.currentResume.get(message.getChatId());
        //..............................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        /** set salary to vacancy */
        mapRepository.currentResume.get(message.getChatId()).setSalary(message.getText());
        /**  enter call link msg */
        if (dto.getConnectAddress() == null) {
            sendMessage.setText("\uD83D\uDCE8  Aloqaga chiqish uchun link yoki tel raqam yozib qoldiring.");
        } else {
            UserDTO userDTO = userService.getById(message.getChatId());
            userDTO.setStep(UserStep.ACCEPTING_RESUME);
            userService.update(userDTO);
            sendMessage.setText("\uD83D\uDD30 Rezyume \uD83D\uDD30\n\n" + "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDDFA Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return sendMessage;
    }

    public SendMessage setConnectAddress(Message message) {
        /**   get currentResume */
        ResumeDTO dto = mapRepository.currentResume.get(message.getChatId());
        //......................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        /** set connect address to vacancy */
        mapRepository.currentResume.get(message.getChatId()).setConnectAddress(message.getText());
        if (dto.getExtraInfo() == null) {
            /**  enter extra info msg */
            sendMessage.setText("‼\uFE0F Ish beruvchidan nimalarni talab qilishingiz, o'z ish tajribangiz darajasi,  korxonaga nimalarni taklif qila olasiz va o'zingiz haqida ba'zi ma'lumotlar . .\n" + "Shu kabi ma'lumotlarni kiritishingizni iltimos qilamiz.\n\n✍\uD83C\uDFFB  Qo'shimcha ma'lumotlarni kiriting.");
        } else {
            UserDTO userDTO = userService.getById(message.getChatId());
            userDTO.setStep(UserStep.ACCEPTING_RESUME);
            userService.update(userDTO);
            sendMessage.setText("\uD83D\uDD30 Rezyume \uD83D\uDD30\n\n" + "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());
        }
        return sendMessage;
    }

    public SendMessage acceptingResume(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        /**  check extra info to 128 sybols */
        if (message.getText().length() >= 63) {
            /**  set extra info to vacancy */
            mapRepository.currentResume.get(message.getChatId()).setExtraInfo(message.getText());
            ResumeDTO dto = mapRepository.currentResume.get(message.getChatId());
            sendMessage.setText("\uD83D\uDD30 Rezyume \uD83D\uDD30\n\n" + "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDCCB Ushbu ma'lumotlarni tasdiqlaysizmi ?");
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
        ResumeDTO dto = mapRepository.currentResume.get(callbackQuery.getFrom().getId());
        /** send total Vacancy Msg  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("  \uD83D\uDD30 Rezyume \uD83D\uDD30\n\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E  Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n\uD83D\uDD30 Qaysi birini tahrirlamoqchisiz ? \uD83D\uDD30");
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
            mapRepository.currentResume.get(callbackQuery.getFrom().getId()).setEmployeeName(null);
            editMessageText.setText("\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism : ...");
        } else if (data.equals("address")) {
            mapRepository.currentResume.get(callbackQuery.getFrom().getId()).setWorkRegion(null);
            mapRepository.currentResume.get(callbackQuery.getFrom().getId()).setWorkDistinct(null);
            editMessageText.setText("\uD83D\uDDFA  Manzil : ...");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.regionsButtons());
        } else if (data.equals("specialty")) {
            mapRepository.currentResume.get(callbackQuery.getFrom().getId()).setSpecialty1(null);
            mapRepository.currentResume.get(callbackQuery.getFrom().getId()).setSpecialty2(null);
            editMessageText.setText("\uD83D\uDCCB  Yo'nalish : ...");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.specialtyButtons());
        } else if (data.equals("technologies")) {
            mapRepository.currentResume.get(callbackQuery.getFrom().getId()).setTechnologies(null);
            editMessageText.setText("❇\uFE0F  Texnologiyalar : ...");
        } else if (data.equals("workTime")) {
            mapRepository.currentResume.get(callbackQuery.getFrom().getId()).setWorkTime(null);
            editMessageText.setText("\uD83D\uDD5E  Haftalik ish soati : ...");
        } else if (data.equals("salary")) {
            mapRepository.currentResume.get(callbackQuery.getFrom().getId()).setSalary(null);
            editMessageText.setText("\uD83D\uDCB0  Maosh : ...");
        } else if (data.equals("callAddress")) {
            mapRepository.currentResume.get(callbackQuery.getFrom().getId()).setConnectAddress(null);
            editMessageText.setText("\uD83D\uDCE8  Aloqaga chiqish uchun link yoki tel raqam yozib qoldiring.");
        } else if (data.equals("extraInfo")) {
            mapRepository.currentResume.get(callbackQuery.getFrom().getId()).setExtraInfo(null);
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
        mapRepository.currentResume.remove(message.getChatId());
        return editMessageText;
    }

    public synchronized EditMessageText save(CallbackQuery callbackQuery) {
        /** change user's step */
        UserDTO user = userService.getById(callbackQuery.getFrom().getId());
        user.setStep(UserStep.END);
        userService.update(user);
        /**  get currentResume to DTO */
        ResumeDTO dto = mapRepository.currentResume.get(callbackQuery.getFrom().getId());
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
        entity = resumeRepository.save(entity);
        dto.setId(entity.getId());
        /** send total Vacancy Msg  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("#" + dto.getId() + "  \uD83D\uDD30 Rezyume \uD83D\uDD30\n\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n《《   @IshVakansiyaBot   》》");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        /**  remove vacancy from HashMap    */
        mapRepository.currentResume.remove(callbackQuery.getFrom().getId());
        return editMessageText;
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
            resumeRepository.changeResumeStatus(resumeId, GeneralStatus.DELETED);
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30  Rezyume  \uD83D\uDD30" + "\n\uD83D\uDC64 Ism : " + entity.getEmployeeName() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\n❌ Rezyume tizimdan o'chirildi !");
        }
        return editMessageText;
    }

}
