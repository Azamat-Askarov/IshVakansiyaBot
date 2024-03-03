package uz.code.ishvakansiyabot.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.code.ishvakansiyabot.config.BotConfig;
import uz.code.ishvakansiyabot.dto.ResumeDTO;
import uz.code.ishvakansiyabot.dto.SendMsgDTO;
import uz.code.ishvakansiyabot.dto.UserDTO;
import uz.code.ishvakansiyabot.dto.VacancyDTO;
import uz.code.ishvakansiyabot.entity.ResumeEntity;
import uz.code.ishvakansiyabot.entity.VacancyEntity;
import uz.code.ishvakansiyabot.enums.GeneralStatus;
import uz.code.ishvakansiyabot.enums.SearchPostType;
import uz.code.ishvakansiyabot.enums.UserStep;
import uz.code.ishvakansiyabot.repository.MapRepository;
import uz.code.ishvakansiyabot.repository.ResumeRepository;
import uz.code.ishvakansiyabot.repository.UserRepository;
import uz.code.ishvakansiyabot.repository.VacancyRepository;
import uz.code.ishvakansiyabot.service.AuthService;
import uz.code.ishvakansiyabot.service.ResumeService;
import uz.code.ishvakansiyabot.service.UserService;
import uz.code.ishvakansiyabot.service.VacancyService;
import uz.code.ishvakansiyabot.util.InlineKeyboardButtonUtil;
import uz.code.ishvakansiyabot.util.ReplyButtons;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class MainController extends TelegramLongPollingBot {
    @Autowired
    BotConfig botConfig;
    @Autowired
    AuthService authService;
    @Autowired
    UserService userService;
    @Autowired
    VacancyService vacancyService;
    @Autowired
    ResumeService resumeService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    VacancyRepository vacancyRepository;
    @Autowired
    ResumeRepository resumeRepository;

    @Scheduled(cron = "00 32 12 * * *")
    public void checkingVacancies() {
        List<VacancyEntity> vacancyEntityList = vacancyService.checkingVacanciesDate();
        for (int i = 0; i < vacancyEntityList.size(); i++) {
            VacancyEntity vacancyEntity = vacancyEntityList.get(i);
            long countDays = ChronoUnit.DAYS.between(LocalDate.parse(vacancyEntity.getCreatedDate().substring(0, 10)), LocalDate.now());
            if (countDays!=0&&countDays % 10 == 0) {
                vacancyRepository.changeVacancyStatus(vacancyEntity.getId(), GeneralStatus.DELETED);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(vacancyEntity.getEmployerId());
                sendMessage.setText("#" + vacancyEntity.getId() + "  \uD83D\uDD30  Vakansiya  \uD83D\uDD30" + "\n\uD83C\uDFE2 Ish beruvchi : " + vacancyEntity.getEmployerName() + "\n\uD83D\uDCCB Yo'nalish : " + vacancyEntity.getSpecialty1() + ", " + vacancyEntity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + vacancyEntity.getSalary() + "\n\uD83D\uDDD3 Created date : " + vacancyEntity.getCreatedDate() + "\n\n⚠\uFE0F Hurmatli foydalanuvchi ushbu vakansiya tizimga qo'shilganiga " + countDays + " kun bo'ldi.\nUshbu sababdan biz ushbu vakansiyani tizimdan o'chiryapmiz.\nAgar hali ham vakansiyaga mos xodim topmagan bo'lsangiz \"O'chirilmasin\" tugmasini bosing.");
                sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("O'chirilmasin", "notDeleteVacancy" + vacancyEntity.getId())))));
                sendMsg(sendMessage);
            }
        }
    }

    @Scheduled(cron = "00 30 03 * * *")
    public void checkingResumes() {
        List<ResumeEntity> resumeEntityList = resumeService.checkingResumesDate();
        for (int i = 0; i < resumeEntityList.size(); i++) {
            ResumeEntity resumeEntity = resumeEntityList.get(i);
            long countDays = ChronoUnit.DAYS.between(LocalDate.parse(resumeEntity.getCreatedDate().substring(0, 10)), LocalDate.now());
            if (countDays!=0&&countDays % 15 == 0) {
                resumeRepository.changeResumeStatus(resumeEntity.getId(), GeneralStatus.DELETED);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(resumeEntity.getEmployeeId());
                sendMessage.setText("#" + resumeEntity.getId() + "  \uD83D\uDD30  Rezyume  \uD83D\uDD30" + "\n\uD83D\uDC64 Ism : " + resumeEntity.getEmployeeName() + "\n\uD83D\uDCCB Yo'nalish : " + resumeEntity.getSpecialty1() + ", " + resumeEntity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + resumeEntity.getSalary() + "\n\uD83D\uDDD3 Created Date : " + resumeEntity.getCreatedDate() + "\n\n⚠\uFE0F Hurmatli foydalanuvchi ushbu rezyume tizimga qo'shilganiga " + countDays + " kun bo'ldi.\nUshbu sababdan biz ushbu rezyumeni tizimdan o'chiryapmiz.\nAgar hali ham rezyumega mos ish topmagan bo'lsangiz \"O'chirilmasin\" tugmasini bosing.");
                sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("O'chirilmasin", "notDeleteResume" + resumeEntity.getId())))));
                sendMsg(sendMessage);
            }
        }
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            User user = message.getFrom();
            UserDTO currentUser = authService.getById(user.getId());
            if (message.hasText() && message.getText().equals("Bekor qilishㅤ")) {
                /**   oxirgi msg ni delete qilish  */
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(user.getId());
                deleteMessage.setMessageId(message.getMessageId() - 1);
                execute(deleteMessage);
                /**   "calcelPosting" msg  */
                sendMsg(userService.cancelPosting(currentUser));
            } else if (currentUser == null || currentUser.getStatus().equals(GeneralStatus.DELETED)) {
                /** hello user...... */
                sendMsg(authService.helloUser(user));
            } else if (currentUser.getStep().equals(UserStep.SEND_FEEDBACK)) {
                sendMsgDTO(userService.sendFeedback(message));
                if (userService.getById(message.getChatId()).getStep().equals(UserStep.END)) {
                    sendMsgDTO(MapRepository.currentFeedbackMap.get(user.getId()));
                }
            } else if (message.getText().equals("/start")) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(user.getId());
                if (currentUser.getStep().equals(UserStep.END)) {
                    sendMessage.setText("Bosh menyu");
                    sendMessage.setReplyMarkup(ReplyButtons.mainMenuButtons());
                } else if (currentUser.getStep().equals(UserStep.SETTINGS)) {
                    sendMessage.setText("Sozlamalar");
                    sendMessage.setReplyMarkup(ReplyButtons.settingsButtons());
                } else if (currentUser.getStep().equals(UserStep.PROFILE) || currentUser.getStep().equals(UserStep.DELETING_PROFILE)) {
                    sendMessage.setText("\uD83D\uDC64 Profile");
                    sendMessage.setReplyMarkup(ReplyButtons.profileButtons());
                } else if (currentUser.getStep().equals(UserStep.EDIT_PROFILE)) {
                    sendMessage.setText("Profilni tahrirlash");
                    sendMessage.setReplyMarkup(ReplyButtons.editProfileButtons());
                } else if (currentUser.getStep().equals(UserStep.ADD_VACANCY) || currentUser.getStep().equals(UserStep.ACCEPTING_VACANCY) || currentUser.getStep().equals(UserStep.SEARCH_VACANCY) || currentUser.getStep().equals(UserStep.EDIT_VACANCY) || currentUser.getStep().equals(UserStep.ADD_RESUME) || currentUser.getStep().equals(UserStep.ACCEPTING_RESUME) || currentUser.getStep().equals(UserStep.EDIT_RESUME) || currentUser.getStep().equals(UserStep.SEARCH_RESUME)) {
                    sendMessage.setText(". . . \uD83D\uDC47\uD83C\uDFFB");
                    sendMessage.setReplyMarkup(ReplyButtons.cancelButton());
                } else if (currentUser.getStep().equals(UserStep.EDIT_NAME)) {
                    sendMessage.setText("✍\uD83C\uDFFB Ismingiz . . .");
                } else if (currentUser.getStep().equals(UserStep.EDIT_AGE)) {
                    sendMessage.setText("✍\uD83C\uDFFB Yoshingiz . . .");
                } else if (currentUser.getStep().equals(UserStep.EDIT_ADDRESS)) {
                    sendMessage.setText("\uD83D\uDDFA Yashash manzilingiz . . .");
                    sendMessage.setReplyMarkup(ReplyButtons.regionsButtons());
                }
                sendMsg(sendMessage);
            } else if (currentUser.getStatus().equals(GeneralStatus.NEW_USER)) {
                /** begin user registring */
                if (currentUser.getName() == null) {
                    sendMsg(authService.setName(message.getText(), currentUser.getTgId())); // ism set qilish
                } else if (currentUser.getAge() == null) {
                    sendMsg(authService.setAge(message.getText(), currentUser.getTgId())); // age set qilish
                }
            } else if (message.getText().equals("Vakansiya joylashㅤ")) {
                sendMsg(vacancyService.create(currentUser.getTgId()));
            } else if (currentUser.getStep().equals(UserStep.ADD_VACANCY)) {
                /**  get current vacancy */
                VacancyDTO vacancyDTO = MapRepository.currentVacancy.get(currentUser.getTgId());
                if (vacancyDTO.getEmployerName() == null) {
                    /** set employerName and show regions */
                    sendMsg(vacancyService.setEmployerName(message));
                } else if (vacancyDTO.getPosition() == null) {
                    /**  set position and show "enter workTime" msg */
                    sendMsg(vacancyService.setPosition(message));
                } else if (vacancyDTO.getWorkTime() == null) {
                    /**  set setWorkTime and enter salary msg */
                    sendMsg(vacancyService.setWorkTime(message));
                } else if (vacancyDTO.getSalary() == null) {
                    /** set salary and show call link msg */
                    sendMsg(vacancyService.setSalary(message));
                } else if (vacancyDTO.getConnectAddress() == null) {
                    /** set call link and show extra info msg */
                    sendMsg(vacancyService.setConnectAddress(message));
                } else if (vacancyDTO.getExtraInfo() == null) {
                    /** set extra info to vacancy ent accepting set vacancy to DataBase */
                    sendMsg(vacancyService.acceptingVacancy(message));
                }
            } else if (message.getText().equals("Rezyume joylashㅤ")) {
                sendMsg(resumeService.create(currentUser.getTgId()));
            } else if (currentUser.getStep().equals(UserStep.ADD_RESUME)) {
                /**  get current resume */
                ResumeDTO resumeDTO = MapRepository.currentResume.get(currentUser.getTgId());
                if (resumeDTO.getEmployeeName() == null) {
                    /** set employeeName and show regions */
                    sendMsg(resumeService.setEmployeeName(message));
                } else if (resumeDTO.getTechnologies() == null) {
                    /**  set technologies and show "enter workTime" msg */
                    sendMsg(resumeService.setTechnologies(message));
                } else if (resumeDTO.getWorkTime() == null) {
                    /**  set setWorkTime and enter salary msg */
                    sendMsg(resumeService.setWorkTime(message));
                } else if (resumeDTO.getSalary() == null) {
                    /** set salary and show call link msg */
                    sendMsg(resumeService.setSalary(message));
                } else if (resumeDTO.getConnectAddress() == null) {
                    /** set call link and show extra info msg */
                    sendMsg(resumeService.setConnectAddress(message));
                } else if (resumeDTO.getExtraInfo() == null) {
                    /** set extra info to vacancy ent accepting set vacancy to DataBase */
                    sendMsg(resumeService.acceptingResume(message));
                }
            } else if (message.getText().equals("Mening vakansiyalarimㅤ")) {
                List<SendMessage> vacancyMsgList = vacancyService.getMyVacancies(currentUser.getTgId());
                for (int i = 0; i < vacancyMsgList.size(); i++) {
                    sendMsg(vacancyMsgList.get(i));
                }
            } else if (message.getText().equals("Mening rezyumelarimㅤ")) {
                List<SendMessage> resumeMsgList = resumeService.getMyResumes(currentUser.getTgId());
                for (int i = 0; i < resumeMsgList.size(); i++) {
                    sendMsg(resumeMsgList.get(i));
                }
            } else if (message.getText().equals("Vakansiya izlashㅤ")) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(currentUser.getTgId());
                sendMessage.setText("\uD83D\uDD0D Vakansiya izlash");
                sendMessage.setReplyMarkup(ReplyButtons.cancelButton());
                sendMsg(sendMessage);
                sendMsg(vacancyService.createSearchMethod(currentUser.getTgId()));
            } else if (message.getText().equals("Rezyume izlashㅤ")) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(currentUser.getTgId());
                sendMessage.setText("\uD83D\uDD0D Rezyume izlash");
                sendMessage.setReplyMarkup(ReplyButtons.cancelButton());
                sendMsg(sendMessage);
                sendMsg(resumeService.createSearchMethod(currentUser.getTgId()));
            } else if (message.getText().equals("Adminㅤ")) {
                sendMsg(userService.botSupport(message));
            } else if (message.getText().equals("Sozlamalarㅤ")) {
                sendMsg(userService.settings(message));
            } else if (message.getText().equals("\uD83D\uDC64 Profileㅤ")) {
                sendMsgDTO(userService.getProfile(message));
            } else if (message.getText().equals("\uD83D\uDCCA Statisticsㅤ")) {
                sendMsgDTO(userService.statistics(message));
            } else if (message.getText().equals("\uD83D\uDCB3 Donateㅤ")) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(currentUser.getTgId());
                sendMessage.setText("Telegram bot faoliyati sizga ma'qul kelayotgan bo'lsa xursandmiz\uD83D\uDE0A\n" + "Bot faoliyati davomli va yanada sifatli bo'lishi uchun o'z hissangizni qo'shing\uD83D\uDE09\n\n" + "\uD83D\uDCB3  5614680004553372  \uD83D\uDCB3");
                sendMsg(sendMessage);

            } else if (message.getText().equals("⬅\uFE0F Ortgaㅤ")) {
                if (currentUser.getStep().equals(UserStep.SETTINGS)) {
                    userRepository.changeUserStep(message.getChatId(), UserStep.END);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(currentUser.getTgId());
                    sendMessage.setText("Bosh menyu");
                    sendMessage.setReplyMarkup(ReplyButtons.mainMenuButtons());
                    sendMsg(sendMessage);
                } else if (currentUser.getStep().equals(UserStep.PROFILE)) {
                    sendMsg(userService.settings(message));
                } else if (currentUser.getStep().equals(UserStep.EDIT_PROFILE) || currentUser.getStep().equals(UserStep.DELETING_PROFILE)) {
                    userRepository.changeUserStep(message.getChatId(), UserStep.PROFILE);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(currentUser.getTgId());
                    sendMessage.setText("\uD83D\uDC64 Profileㅤ");
                    sendMessage.setReplyMarkup(ReplyButtons.profileButtons());
                    sendMsg(sendMessage);
                }
            } else if (message.getText().equals("Profilni tahrirlashㅤ")) {
                sendMsgDTO(userService.editProfile(message));
            } else if (message.getText().equals("Profilni o'chirishㅤ")) {
                userRepository.changeUserStep(message.getChatId(), UserStep.DELETING_PROFILE);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(message.getChatId());
                sendMessage.setText("Profilingizni o'chirsangiz sizdagi mavjud barcha vakansiya va rezyumelar ham o'chiriladi !!");
                sendMessage.setReplyMarkup(ReplyButtons.acceptingDeletingButtons());
                sendMsg(sendMessage);
            } else if (currentUser.getStep().equals(UserStep.DELETING_PROFILE)) {
                if (message.getText().equals("✅ Tasdiqlashㅤ")) {
                    sendMsgDTO(userService.deleteProfile(message));
                }
            } else if (currentUser.getStep().equals(UserStep.EDIT_PROFILE)) {
                if (message.getText().equals("Ismㅤ")) {
                    userRepository.changeUserStep(message.getChatId(), UserStep.EDIT_NAME);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("✍\uD83C\uDFFB Ismingiz . . .");
                    sendMessage.setChatId(message.getChatId());
                    sendMessage.setReplyMarkup(ReplyButtons.removeButton());
                    sendMsg(sendMessage);
                } else if (message.getText().equals("Yoshㅤ")) {
                    userRepository.changeUserStep(message.getChatId(), UserStep.EDIT_AGE);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("✍\uD83C\uDFFB Yoshingiz . . .");
                    sendMessage.setChatId(message.getChatId());
                    sendMessage.setReplyMarkup(ReplyButtons.removeButton());
                    sendMsg(sendMessage);
                } else if (message.getText().equals("Manzilㅤ")) {
                    userRepository.changeUserStep(message.getChatId(), UserStep.EDIT_ADDRESS);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("\uD83D\uDDFA Yashash manzilingiz . . .");
                    sendMessage.setChatId(message.getChatId());
                    sendMessage.setReplyMarkup(ReplyButtons.regionsButtons());
                    sendMsg(sendMessage);
                }
            } else if (currentUser.getStep().equals(UserStep.EDIT_NAME) && message.hasText()) {
                sendMsg(userService.setName(message.getText(), message.getChatId()));
            } else if (currentUser.getStep().equals(UserStep.EDIT_AGE) && message.hasText()) {
                sendMsg(userService.setAge(message.getText(), message.getChatId()));
            } else if (currentUser.getStep().equals(UserStep.EDIT_ADDRESS) && message.hasText()) {
                userRepository.changeUserStep(message.getChatId(), UserStep.EDIT_PROFILE);
                userRepository.changeUserAddress(message.getChatId(), message.getText());
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("✅ Manzil o'zgartirildi.");
                sendMessage.setChatId(message.getChatId());
                sendMessage.setReplyMarkup(ReplyButtons.editProfileButtons());
                sendMsg(sendMessage);
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            User user = callbackQuery.getFrom();
            UserDTO currentUser = authService.getById(user.getId());
            if (currentUser == null || currentUser.getStatus().equals(GeneralStatus.DELETED)) {
                if (callbackQuery.getData().equals("signUp")) {
                    if (currentUser != null) {
                        currentUser.setStatus(GeneralStatus.NEW_USER);
                        currentUser.setStep(UserStep.CREATING);
                        userService.update(currentUser);
                    }
                    /**  begin signing up */
                    EditMessageText signingUpEditMsg = new EditMessageText();
                    signingUpEditMsg.setText("\uD83D\uDD30 Ro'yxatdan o'tish \uD83D\uDD30");
                    signingUpEditMsg.setMessageId(callbackQuery.getMessage().getMessageId());
                    signingUpEditMsg.setChatId(callbackQuery.getFrom().getId());
                    sendEditMsg(signingUpEditMsg);
                    /**   set default fields */
                    authService.create(user.getId());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("✍\uD83C\uDFFB Ismingiz . .");
                    sendMessage.setChatId(user.getId());
                    sendMessage.setReplyMarkup(ReplyButtons.cancelButton());
                    sendMsg(sendMessage);
                } else if (callbackQuery.getData().equals("aboutBot")) {
                    /** show aboutBot and make signUp button */
                    sendEditMsg(authService.sendAboutBotMsg(callbackQuery));
                }
            } else if (currentUser.getAddress() == null) {
                /** set user's region and show total fields and accepting buttons */
                sendEditMsg(authService.setAddress(callbackQuery));
            } else if (callbackQuery.getData().startsWith("get")) {
                /**  my posts bo'limi */
                if (callbackQuery.getData().startsWith("getLessVacancy")) {
                    sendEditMsg(vacancyService.getById(callbackQuery));
                } else if (callbackQuery.getData().startsWith("getMoreVacancy")) {
                    sendEditMsg(vacancyService.getById(callbackQuery));
                } else if (callbackQuery.getData().startsWith("getDeletingVacancy")) {
                    sendEditMsg(vacancyService.delete(callbackQuery));
                } else if (callbackQuery.getData().startsWith("getDeleteVacancy")) {
                    sendEditMsg(vacancyService.delete(callbackQuery));
                } else if (callbackQuery.getData().startsWith("getLessResume")) {
                    sendEditMsg(resumeService.getById(callbackQuery));
                } else if (callbackQuery.getData().startsWith("getMoreResume")) {
                    sendEditMsg(resumeService.getById(callbackQuery));
                } else if (callbackQuery.getData().startsWith("getDeletingResume")) {
                    sendEditMsg(resumeService.delete(callbackQuery));
                } else if (callbackQuery.getData().startsWith("getDeleteResume")) {
                    sendEditMsg(resumeService.delete(callbackQuery));
                } else if (callbackQuery.getData().startsWith("getMoreNewVacancy")) {
                    sendEditMsg(vacancyService.getById(callbackQuery));
                } else if (callbackQuery.getData().startsWith("getLessNewVacancy")) {
                    sendEditMsg(vacancyService.getById(callbackQuery));
                } else if (callbackQuery.getData().startsWith("getMoreNewResume")) {
                    sendEditMsg(resumeService.getById(callbackQuery));
                } else if (callbackQuery.getData().startsWith("getLessNewResume")) {
                    sendEditMsg(resumeService.getById(callbackQuery));
                } else if (callbackQuery.getData().startsWith("getPage")) {
                    if (MapRepository.currentSearcherMap.get(currentUser.getTgId()).getSearchPostType().equals(SearchPostType.VACANCY)) {
                        sendEditMsg(vacancyService.searchVacancyTotalMsg(callbackQuery));
                    } else if (MapRepository.currentSearcherMap.get(currentUser.getTgId()).getSearchPostType().equals(SearchPostType.RESUME)) {
                        sendEditMsg(resumeService.searchResumeTotalMsg(callbackQuery));
                    }
                } else if (callbackQuery.getData().startsWith("getLessSearchVacancy")) {
                    sendMsg(vacancyService.getSearchResultVacancy(callbackQuery));
                } else if (callbackQuery.getData().startsWith("getLessSearchResume")) {
                    sendMsg(resumeService.getSearchResultResume(callbackQuery));
                }
            } else if (callbackQuery.getData().startsWith("notDelete")) {
                sendEditMsg(userService.checkingPost(callbackQuery));
            } else if (currentUser.getStep().equals(UserStep.SEARCH_VACANCY)) {
                sendEditMsg(vacancyService.search(callbackQuery));
                if (userService.getById(currentUser.getTgId()).getStep().equals(UserStep.END)) {
                    SendMessage send = new SendMessage();
                    send.setChatId(callbackQuery.getFrom().getId());
                    send.setText("Bosh menyu");
                    send.setReplyMarkup(ReplyButtons.mainMenuButtons());
                    sendMsg(send);
                }
            } else if (currentUser.getStep().equals(UserStep.SEARCH_RESUME)) {
                sendEditMsg(resumeService.search(callbackQuery));
                if (userService.getById(currentUser.getTgId()).getStep().equals(UserStep.END)) {
                    SendMessage send = new SendMessage();
                    send.setChatId(callbackQuery.getFrom().getId());
                    send.setText("Bosh menyu");
                    send.setReplyMarkup(ReplyButtons.mainMenuButtons());
                    sendMsg(send);
                }
            } else if (currentUser.getStep().equals(UserStep.ACCEPTING_NEW_USER)) {
                /** new user fields accepting or noAccepting */
                sendEditMsg(authService.acceptNewUser(callbackQuery));
                /** accepted qilgan bo'lsa */
                /**/
                if (authService.getById(currentUser.getTgId()).getStatus().equals(GeneralStatus.ACTIVE)) {
                    SendMessage msg = new SendMessage();
                    msg.setText("\uD83D\uDCF1 Endi bot imkoniyatlaridan to'liq foydalanishingiz mumkin.");
                    msg.setChatId(currentUser.getTgId());
                    msg.setReplyMarkup(ReplyButtons.mainMenuButtons());
                    sendMsg(msg);
                }
            } else if (currentUser.getStep().equals(UserStep.ADD_VACANCY)) {
                if (MapRepository.currentVacancy.get(currentUser.getTgId()).getWorkRegion() == null) {
                    /** set vacancy region and show districts buttons */
                    sendEditMsg(vacancyService.setVacancyRegion(update.getCallbackQuery()));
                } else if (MapRepository.currentVacancy.get(currentUser.getTgId()).getWorkDistinct() == null) {
                    /** set distinct and show specialty1 buttons */
                    sendEditMsg(vacancyService.setVacancyDistinct(update.getCallbackQuery()));
                } else if (MapRepository.currentVacancy.get(currentUser.getTgId()).getSpecialty1() == null) {
                    /**  set specialty1 and show specialty2 buttons */
                    sendEditMsg(vacancyService.setSpecialty1(update.getCallbackQuery()));
                } else if (MapRepository.currentVacancy.get(currentUser.getTgId()).getSpecialty2() == null) {
                    /**  set specialty2 and "enter position msg" */
                    sendEditMsg(vacancyService.setSpecialty2(update.getCallbackQuery()));
                }
            } else if (currentUser.getStep().equals(UserStep.ACCEPTING_VACANCY)) {
                if (callbackQuery.getData().equals("accept")) {
                    VacancyDTO dto = vacancyService.save(callbackQuery);
                    /** send total Vacancy Msg  */
                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setChatId(callbackQuery.getFrom().getId());
                    editMessageText.setText("#" + dto.getId() + "  \uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n\uD83C\uDFE2 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + dto.getPosition() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n《《   @IshVakansiyaBot   》》");
                    editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
                    sendEditMsg(editMessageText);
                    //.............................................//
                    List<SendMessage> newVacaciesList = vacancyService.sendingVacancyToEmployees(dto);
                    for (int i = 0; i < newVacaciesList.size(); i++) {
                        sendMsg(newVacaciesList.get(i));
                    }
                    SendMessage msg = new SendMessage();
                    msg.setText("✅ Vakansiya tizimga yuklandi va kiritilgan yo'nalish bo'yicha ish izlayotganlarga uzatildi.");
                    msg.setChatId(currentUser.getTgId());
                    msg.setReplyMarkup(ReplyButtons.mainMenuButtons());
                    sendMsg(msg);
                } else if (callbackQuery.getData().equals("edit")) {
                    sendEditMsg(vacancyService.editVacancyButtons(callbackQuery));
                } else if (callbackQuery.getData().equals("cancel")) {
                    sendEditMsg(vacancyService.cancelVacancy(callbackQuery.getMessage()));
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(currentUser.getTgId());
                    sendMessage.setText("Bosh menyu \uD83D\uDC47\uD83C\uDFFB");
                    sendMessage.setReplyMarkup(ReplyButtons.mainMenuButtons());
                    sendMsg(sendMessage);
                }
            } else if (currentUser.getStep().equals(UserStep.EDIT_VACANCY)) {
                sendEditMsg(vacancyService.editVacancy(callbackQuery));
            } else if (currentUser.getStep().equals(UserStep.ADD_RESUME)) {
                if (MapRepository.currentResume.get(currentUser.getTgId()).getWorkRegion() == null) {
                    /** set resume region and show districts buttons */
                    sendEditMsg(resumeService.setResumeRegion(update.getCallbackQuery()));
                } else if (MapRepository.currentResume.get(currentUser.getTgId()).getWorkDistinct() == null) {
                    /** set distinct and show specialty1 buttons */
                    sendEditMsg(resumeService.setResumeDistinct(update.getCallbackQuery()));
                } else if (MapRepository.currentResume.get(currentUser.getTgId()).getSpecialty1() == null) {
                    /**  set specialty1 and show specialty2 buttons */
                    sendEditMsg(resumeService.setSpecialty1(update.getCallbackQuery()));
                } else if (MapRepository.currentResume.get(currentUser.getTgId()).getSpecialty2() == null) {
                    /**  set specialty2 and "enter position msg" */
                    sendEditMsg(resumeService.setSpecialty2(update.getCallbackQuery()));
                }
            } else if (currentUser.getStep().equals(UserStep.ACCEPTING_RESUME)) {
                if (callbackQuery.getData().equals("accept")) {
                    ResumeDTO dto = resumeService.save(callbackQuery);
                    /** send total Vacancy Msg  */
                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setChatId(callbackQuery.getFrom().getId());
                    editMessageText.setText("#" + dto.getId() + "  \uD83D\uDD30 Rezyume \uD83D\uDD30\n\n\uD83D\uDC64 Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n《《   @IshVakansiyaBot   》》");
                    editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
                    sendEditMsg(editMessageText);
                    //.................................................//
                    List<SendMessage> newResumesList = resumeService.sendingResumeToEmployers(dto);
                    for (int i = 0; i < newResumesList.size(); i++) {
                        sendMsg(newResumesList.get(i));
                    }
                    SendMessage msg = new SendMessage();
                    msg.setText("✅ Rezyume tizimga yuklandi va kiritilgan yo'nalish bo'yicha barcha ish beruvchilarga uzatildi.");
                    msg.setChatId(currentUser.getTgId());
                    msg.setReplyMarkup(ReplyButtons.mainMenuButtons());
                    sendMsg(msg);
                } else if (callbackQuery.getData().equals("edit")) {
                    sendEditMsg(resumeService.editResumeButtons(callbackQuery));
                } else if (callbackQuery.getData().equals("cancel")) {
                    sendEditMsg(resumeService.cancelResume(callbackQuery.getMessage()));
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(currentUser.getTgId());
                    sendMessage.setText("Bosh menyu \uD83D\uDC47\uD83C\uDFFB");
                    sendMessage.setReplyMarkup(ReplyButtons.mainMenuButtons());
                    sendMsg(sendMessage);
                }
            } else if (currentUser.getStep().equals(UserStep.EDIT_RESUME)) {
                sendEditMsg(resumeService.editResume(callbackQuery));
            }

        }
    }

    public void sendMsgDTO(SendMsgDTO sendMsgDTO) {
        try {
            if (sendMsgDTO.getText() != null) {
                execute(sendMsgDTO.getText());
            } else if (sendMsgDTO.getEditText() != null) {
                execute(sendMsgDTO.getEditText());
            } else if (sendMsgDTO.getPhoto() != null) {
                execute(sendMsgDTO.getPhoto());
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMsg(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendEditMsg(EditMessageText sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}
