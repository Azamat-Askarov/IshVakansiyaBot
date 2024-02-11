package uz.code.ishvakansiyabot.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.code.ishvakansiyabot.config.BotConfig;
import uz.code.ishvakansiyabot.dto.ResumeDTO;
import uz.code.ishvakansiyabot.dto.UserDTO;
import uz.code.ishvakansiyabot.dto.VacancyDTO;
import uz.code.ishvakansiyabot.enums.GeneralStatus;
import uz.code.ishvakansiyabot.enums.UserStep;
import uz.code.ishvakansiyabot.repository.MapRepository;
import uz.code.ishvakansiyabot.repository.VacancyRepository;
import uz.code.ishvakansiyabot.service.AuthService;
import uz.code.ishvakansiyabot.service.ResumeService;
import uz.code.ishvakansiyabot.service.UserService;
import uz.code.ishvakansiyabot.service.VacancyService;

import java.util.List;

@Component
public class MainController extends TelegramLongPollingBot {
    @Autowired
    MapRepository mapRepository;
    @Autowired
    VacancyRepository vacancyRepository;
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

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            User user = message.getFrom();
            UserDTO currentUser = authService.getById(user.getId());
            if (message.getText().equals("Bekor qilishㅤ")) {
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
                VacancyDTO vacancyDTO = mapRepository.currentVacancy.get(currentUser.getTgId());
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
                ResumeDTO resumeDTO = mapRepository.currentResume.get(currentUser.getTgId());
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
                    signingUpEditMsg.setText("\uD83D\uDC49\uD83C\uDFFB Quyidagi kiritiladigan ma'lumotlar bizga bot statistikasini to'g'ri tahlil qilish uchun zarur.\nBuni to'g'ri tushunasiz degan umiddamiz\uD83D\uDE0A\nIltimos ba'zi shaxsiy ma'lumotlaringizni kiritishingizni so'raymiz.\n\n\uD83D\uDD30 Ro'yxatdan o'tish \uD83D\uDD30");
                    signingUpEditMsg.setMessageId(callbackQuery.getMessage().getMessageId());
                    signingUpEditMsg.setChatId(callbackQuery.getFrom().getId());
                    sendEditMsg(signingUpEditMsg);
                    /** set default fields */
                    authService.create(user.getId());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("✍\uD83C\uDFFB Ismingiz . .");
                    sendMessage.setChatId(user.getId());
                    sendMessage.setReplyMarkup(userService.cancelButton());
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
                    msg.setReplyMarkup(userService.mainMenuButtons());
                    sendMsg(msg);
                }
            } else if (currentUser.getStep().equals(UserStep.ADD_VACANCY)) {
                if (mapRepository.currentVacancy.get(currentUser.getTgId()).getWorkRegion() == null) {
                    /** set vacancy region and show districts buttons */
                    sendEditMsg(vacancyService.setVacancyRegion(update.getCallbackQuery()));
                } else if (mapRepository.currentVacancy.get(currentUser.getTgId()).getWorkDistinct() == null) {
                    /** set distinct and show specialty1 buttons */
                    sendEditMsg(vacancyService.setVacancyDistinct(update.getCallbackQuery()));
                } else if (mapRepository.currentVacancy.get(currentUser.getTgId()).getSpecialty1() == null) {
                    /**  set specialty1 and show specialty2 buttons */
                    sendEditMsg(vacancyService.setSpecialty1(update.getCallbackQuery()));
                } else if (mapRepository.currentVacancy.get(currentUser.getTgId()).getSpecialty2() == null) {
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
                    msg.setReplyMarkup(userService.mainMenuButtons());
                    sendMsg(msg);
                } else if (callbackQuery.getData().equals("edit")) {
                    sendEditMsg(vacancyService.editVacancyButtons(callbackQuery));
                } else if (callbackQuery.getData().equals("cancel")) {
                    sendEditMsg(vacancyService.cancelVacancy(callbackQuery.getMessage()));
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(currentUser.getTgId());
                    sendMessage.setText("\uD83D\uDD30 . . . . . . . . . . . . . . . . . . . . \uD83D\uDD30");
                    sendMessage.setReplyMarkup(userService.mainMenuButtons());
                    sendMsg(sendMessage);
                }
            } else if (currentUser.getStep().equals(UserStep.EDIT_VACANCY)) {
                sendEditMsg(vacancyService.editVacancy(callbackQuery));
            } else if (currentUser.getStep().equals(UserStep.ADD_RESUME)) {
                if (mapRepository.currentResume.get(currentUser.getTgId()).getWorkRegion() == null) {
                    /** set resume region and show districts buttons */
                    sendEditMsg(resumeService.setResumeRegion(update.getCallbackQuery()));
                } else if (mapRepository.currentResume.get(currentUser.getTgId()).getWorkDistinct() == null) {
                    /** set distinct and show specialty1 buttons */
                    sendEditMsg(resumeService.setResumeDistinct(update.getCallbackQuery()));
                } else if (mapRepository.currentResume.get(currentUser.getTgId()).getSpecialty1() == null) {
                    /**  set specialty1 and show specialty2 buttons */
                    sendEditMsg(resumeService.setSpecialty1(update.getCallbackQuery()));
                } else if (mapRepository.currentResume.get(currentUser.getTgId()).getSpecialty2() == null) {
                    /**  set specialty2 and "enter position msg" */
                    sendEditMsg(resumeService.setSpecialty2(update.getCallbackQuery()));
                }
            } else if (currentUser.getStep().equals(UserStep.ACCEPTING_RESUME)) {
                if (callbackQuery.getData().equals("accept")) {
                    ResumeDTO dto = resumeService.save(callbackQuery);
                    /** send total Vacancy Msg  */
                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setChatId(callbackQuery.getFrom().getId());
                    editMessageText.setText("#" + dto.getId() + "  \uD83D\uDD30 Rezyume \uD83D\uDD30\n\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ism : " + dto.getEmployeeName() + "\n\uD83D\uDDFA Manzil : " + dto.getWorkRegion() + ", " + dto.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n❇\uFE0F Texnologiyalar : " + dto.getTechnologies() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + dto.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + dto.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + dto.getExtraInfo() + "\n\n《《   @IshVakansiyaBot   》》");
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
                    msg.setReplyMarkup(userService.mainMenuButtons());
                    sendMsg(msg);
                } else if (callbackQuery.getData().equals("edit")) {
                    sendEditMsg(resumeService.editResumeButtons(callbackQuery));
                } else if (callbackQuery.getData().equals("cancel")) {
                    sendEditMsg(resumeService.cancelResume(callbackQuery.getMessage()));
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(currentUser.getTgId());
                    sendMessage.setText("\uD83D\uDD30 . . . . . \uD83D\uDD30");
                    sendMessage.setReplyMarkup(userService.mainMenuButtons());
                    sendMsg(sendMessage);
                }
            } else if (currentUser.getStep().equals(UserStep.EDIT_RESUME)) {
                sendEditMsg(resumeService.editResume(callbackQuery));
            }
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
