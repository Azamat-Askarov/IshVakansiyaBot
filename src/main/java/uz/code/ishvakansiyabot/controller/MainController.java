package uz.code.ishvakansiyabot.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.code.ishvakansiyabot.config.BotConfig;
import uz.code.ishvakansiyabot.dto.UserDTO;
import uz.code.ishvakansiyabot.dto.VacancyDTO;
import uz.code.ishvakansiyabot.enums.GeneralStatus;
import uz.code.ishvakansiyabot.enums.UserStep;
import uz.code.ishvakansiyabot.service.UserService;
import uz.code.ishvakansiyabot.service.VacancyService;

@Component
public class MainController extends TelegramLongPollingBot {
    @Autowired
    BotConfig botConfig;
    @Autowired
    UserService userService;
    @Autowired
    VacancyService vacancyService;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            User user = message.getFrom();
            UserDTO currentUser = userService.getById(user.getId());
            if (currentUser == null || currentUser.getStatus().equals(GeneralStatus.DELETED)) {
                /** hello user...... */
                sendMsg(userService.helloUser(user));
            } else if (currentUser.getStatus().equals(GeneralStatus.NEW_USER)) {
                /** begin user registring */
                if (currentUser.getName() == null) {
                    sendMsg(userService.setName(message.getText(), currentUser.getTgId())); // ism set qilish
                } else if (currentUser.getAge() == null) {
                    sendMsg(userService.setAge(message.getText(), currentUser.getTgId())); // age set qilish
                }
            } else if (message.getText().equals("Vakansiya joylashㅤ")) {
                sendMsg(vacancyService.create(currentUser.getTgId()));
            } else if (currentUser.getStep().equals(UserStep.ADD_VACANCY)) {
                /**  get current vacancy */
                VacancyDTO vacancyDTO = vacancyService.currentVacancy.get(currentUser.getTgId());
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
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            User user = callbackQuery.getFrom();
            UserDTO currentUser = userService.getById(user.getId());
            /**  enter new user */
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
                    userService.create(user.getId());
                    sendText("✍\uD83C\uDFFB Ismingiz . .", user.getId()); // ism so'rash
                } else if (callbackQuery.getData().equals("aboutBot")) {
                    /** show aboutBot and make signUp button */
                    sendEditMsg(userService.sendAboutBotMsg(callbackQuery));
                }
            } else if (currentUser.getAddress() == null) {
                /** set user's region and show total fields and accepting buttons */
                sendEditMsg(userService.setAddress(callbackQuery)); // viloyat set qilish
            } else if (currentUser.getStep().equals(UserStep.ACCEPTING_NEW_USER)) {
                /** new user fields accepting or noAccepting */
                sendEditMsg(userService.acceptNewUser(callbackQuery));
                /** accepted qilgan bo'lsa */
                if (userService.getById(currentUser.getTgId()).getStatus().equals(GeneralStatus.ACTIVE)) {
                    SendMessage msg = new SendMessage();
                    msg.setText("\uD83D\uDCF1 Endi bot imkoniyatlaridan to'liq foydalanishingiz mumkin.");
                    msg.setChatId(currentUser.getTgId());
                    msg.setReplyMarkup(userService.mainMenuButtons());
                    sendMsg(msg);
                }
            } else if (currentUser.getStep().equals(UserStep.ADD_VACANCY)) {
                if (vacancyService.currentVacancy.get(currentUser.getTgId()).getWorkRegion() == null) {
                    /** set vacancy region and show districts buttons */
                    sendEditMsg(vacancyService.setVacancyRegion(update.getCallbackQuery()));
                } else if (vacancyService.currentVacancy.get(currentUser.getTgId()).getWorkDistinct() == null) {
                    /** set distinct and show specialty1 buttons */
                    sendEditMsg(vacancyService.setVacancyDistinct(update.getCallbackQuery()));
                } else if (vacancyService.currentVacancy.get(currentUser.getTgId()).getSpecialty1() == null) {
                    /**  set specialty1 and show specialty2 buttons */
                    sendEditMsg(vacancyService.setSpecialty1(update.getCallbackQuery()));
                } else if (vacancyService.currentVacancy.get(currentUser.getTgId()).getSpecialty2() == null) {
                    /**  set specialty2 and "enter position msg" */
                    sendEditMsg(vacancyService.setSpecialty2(update.getCallbackQuery()));
                }
            } else if (currentUser.getStep().equals(UserStep.ACCEPTING_VACANCY)) {
                if (callbackQuery.getData().equals("accept")) {
                    sendEditMsg(vacancyService.save(callbackQuery));
                } else if (callbackQuery.getData().equals("edit")) {
                    sendEditMsg(vacancyService.editVacancyButtons(callbackQuery));
                } else if (callbackQuery.getData().equals("cancel")) {
                    sendEditMsg(vacancyService.cancelVacancy(callbackQuery));
                }
            } else if (currentUser.getStep().equals(UserStep.EDIT_VACANCY)) {
                sendEditMsg(vacancyService.editVacancy(callbackQuery));
            }
        }
    }

    public void sendText(String text, Long id) {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(text);
            sendMessage.setChatId(id);
            execute(sendMessage);
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
