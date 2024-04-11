package uz.code.ishvakansiyabot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.code.ishvakansiyabot.dto.SearcherDTO;
import uz.code.ishvakansiyabot.dto.UserDTO;
import uz.code.ishvakansiyabot.dto.VacancyDTO;
import uz.code.ishvakansiyabot.entity.VacancyEntity;
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
public class VacancyService {
    @Autowired
    VacancyRepository vacancyRepository;
    @Autowired
    UserService userService;
    @Autowired
    ResumeRepository resumeRepository;

    public SendMessage createSearchMethod(Long userId) {
        SearcherDTO searcherDTO = MapRepository.currentSearcherMap.get(userId);
        if (searcherDTO != null) {
            MapRepository.currentSearcherMap.remove(userId);
        }
        searcherDTO = new SearcherDTO();
        searcherDTO.setUserId(userId);
        searcherDTO.setSearchPostType(SearchPostType.VACANCY);
        userService.changeStep(userId, UserStep.SEARCH_VACANCY);
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
                editMessageText = searchVacancyTotalMsg(callbackQuery);
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
                editMessageText = searchVacancyTotalMsg(callbackQuery);
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
                //editMessageText.setText(" .");
                editMessageText = searchVacancyTotalMsg(callbackQuery);
                userService.changeStep(userDTO.getId(), UserStep.END);
            }
        }
        return editMessageText;
    }

    public List<VacancyDTO> getResultBySearchMethods(SearcherDTO searcherDTO) {
        SearchMethodType searchMethodType = searcherDTO.getSearchMethodType();
        List<VacancyEntity> vacancyEntityList = new LinkedList<>();
        if (searchMethodType.equals(SearchMethodType.SEARCH1)) {
            vacancyEntityList = vacancyRepository.findBySpecialty2AndStatus(searcherDTO.getSpecialty2(), GeneralStatus.ACTIVE);
        } else if (searchMethodType.equals(SearchMethodType.SEARCH2)) {
            vacancyEntityList = vacancyRepository.findByWorkRegionAndSpecialty2AndStatus(searcherDTO.getRegion(), searcherDTO.getSpecialty2(), GeneralStatus.ACTIVE);
        } else if (searchMethodType.equals(SearchMethodType.SEARCH3)) {
            vacancyEntityList = vacancyRepository.findByWorkDistinctAndSpecialty2AndStatus(searcherDTO.getDistinct(), searcherDTO.getSpecialty2(), GeneralStatus.ACTIVE);
        }
        List<VacancyDTO> vacancyDTOList = new LinkedList<>();
        for (VacancyEntity entity : vacancyEntityList) {
            VacancyDTO dto = new VacancyDTO();
            dto.setId(entity.getId());
            dto.setEmployerId(entity.getEmployerId());
            dto.setStatus(entity.getStatus());
            dto.setCreatedDate(entity.getCreatedDate());
            dto.setEmployerName(entity.getEmployerName());
            dto.setSpecialty1(entity.getSpecialty1());
            dto.setSpecialty2(entity.getSpecialty2());
            dto.setPosition(entity.getPosition());
            dto.setSalary(entity.getSalary());
            dto.setWorkRegion(entity.getWorkRegion());
            dto.setWorkDistinct(entity.getWorkDistinct());
            dto.setConnectAddress(entity.getConnectAddress());
            dto.setWorkTime(entity.getWorkTime());
            dto.setExtraInfo(entity.getExtraInfo());
            vacancyDTOList.add(dto);
        }
        /** current result vacancy larni map ga saqlab qo'yish */
        MapRepository.searchVacancyResultMap.put(searcherDTO.getUserId(), vacancyDTOList);
        return vacancyDTOList;
    }

    public EditMessageText searchVacancyTotalMsg(CallbackQuery callbackQuery) {
        if (callbackQuery.getData().startsWith("getPageNext")) {
            int i = Integer.parseInt(callbackQuery.getData().substring(11));
            MapRepository.currentSearcherMap.get(callbackQuery.getFrom().getId()).setCurrentVacancyPageIndex(i + 1);
        } else if (callbackQuery.getData().startsWith("getPageBack")) {
            int i = Integer.parseInt(callbackQuery.getData().substring(11));
            MapRepository.currentSearcherMap.get(callbackQuery.getFrom().getId()).setCurrentVacancyPageIndex(i - 1);
        }
        SearcherDTO searcherDTO = MapRepository.currentSearcherMap.get(callbackQuery.getFrom().getId());
        int currentPageIndex = searcherDTO.getCurrentVacancyPageIndex();
        List<VacancyDTO> resultList = getResultBySearchMethods(searcherDTO);
        Long userId = searcherDTO.getUserId();
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(userId);
        /**   make vacancyListOfCurrentPage */
        List<VacancyDTO> currentPageList = new LinkedList<>();
        if (!resultList.isEmpty()) {
            for (int i = 0; i < 10; i++) {
                if (resultList.size() == (currentPageIndex * 10) + i) {
                    break;
                }
                currentPageList.add(resultList.get((currentPageIndex * 10) + i));
            }
        }
        int totalElementSize = MapRepository.searchVacancyResultMap.get(userId).size();
        String msg = "Natijalar :  " + (currentPageIndex * 10 + 1) + " - " + (currentPageIndex * 10 + currentPageList.size()) + " / " + totalElementSize + "\n\n";
        /**   make resultText */
        if (resultList.isEmpty()) {
            msg = "Bu filtr bo'yicha vakansiyalar topilmadi !";
        } else {
            for (int i = 0; i < 10; i++) {
                if (currentPageList.size() == i) {
                    break;
                }
                VacancyDTO dto = new VacancyDTO();
                dto = currentPageList.get(i);
                msg += (i + 1) + ". Ish beruvchi : " + dto.getEmployerName() + ",  Lavozim : " + dto.getPosition() + ",   Maosh : " + dto.getSalary() + "\n";
            }
        }
        /**   make searchResultButtons */
        editMessageText.setText(msg);
        InlineKeyboardMarkup inlineKeyboardMarkup = searchResultButtons(currentPageList, searcherDTO.getCurrentVacancyPageIndex(), resultList.size());
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        return editMessageText;
    }

    public InlineKeyboardMarkup searchResultButtons(List<VacancyDTO> currentPageList, int pageIndex, int sizeOfResultList) {
        int buttonRowSize = currentPageList.size() / 5;
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        for (int i = 0; i <= buttonRowSize; i++) {
            List<InlineKeyboardButton> row = new LinkedList<>();
            for (int j = 0; j < 5; j++) {
                if (currentPageList.size() == 5 * i + j) {
                    break;
                }
                InlineKeyboardButton button = InlineKeyboardButtonUtil.button(String.valueOf((5 * i) + j + 1), "getLessSearchVacancy" + currentPageList.get(5 * i + j).getId());
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

    public synchronized SendMessage create(Long userId) {
        /** add vacancy to HashMap */
        VacancyDTO vacancy = new VacancyDTO();
        vacancy.setEmployerId(userId);
        MapRepository.currentVacancy.put(userId, vacancy);
        /** change currentUser's step */
        UserDTO user = userService.getById(userId);
        user.setStep(UserStep.ADD_VACANCY);
        userService.update(user);
        /**  show "enter employerName" msg  */
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83C\uDFE2  Ish beruvchi nomi : . . .");
        sendMessage.setChatId(userId);
        /** Remove ReplyButtons */
        ReplyKeyboardRemove removeButton = new ReplyKeyboardRemove();
        removeButton.setSelective(true);
        removeButton.setRemoveKeyboard(true);
        sendMessage.setReplyMarkup(ReplyButtons.cancelButton());
        return sendMessage;
    }

    public SendMessage setEmployerName(Message message) {
        MapRepository.currentVacancy.get(message.getChatId()).setEmployerName(message.getText());
        /**   get currentVacancy */
        VacancyDTO dto = MapRepository.currentVacancy.get(message.getChatId());
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
        MapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setWorkRegion(callbackQuery.getData());
        /**  remove regions and show districts BUTTONS  */
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackQuery.getFrom().getId());
        editMessageText.setText("\uD83D\uDD30 Vakansiya joylash \uD83D\uDD30\n\n\uD83C\uDFE2 Ish beruvchi : " + MapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).getEmployerName() + "\n\uD83D\uDDFA Manzil : " + callbackQuery.getData() + ",  ...");
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        /**  . .make distinct buttons  */
        editMessageText.setReplyMarkup(InlineKeyBoardUtil.districtButtons(callbackQuery));
        return editMessageText;
    }

    public EditMessageText setVacancyDistinct(CallbackQuery callbackQuery) {
        /** set distinct to vacancy */
        MapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setWorkDistinct(callbackQuery.getData());
        /**  get currentVacancy */
        VacancyDTO dto = MapRepository.currentVacancy.get(callbackQuery.getFrom().getId());
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
        MapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setSpecialty1(callbackQuery.getData());
        /**  remove Sspecialty(1) button and show specialty(2) buttons  */
        VacancyDTO dto = MapRepository.currentVacancy.get(callbackQuery.getFrom().getId());
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
        MapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setSpecialty2(callbackQuery.getData());
        /**  get currentVacancy */
        VacancyDTO dto = MapRepository.currentVacancy.get(callbackQuery.getFrom().getId());
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
        MapRepository.currentVacancy.get(message.getChatId()).setPosition(message.getText());
        /**   get vcurrentVacancy */
        VacancyDTO dto = MapRepository.currentVacancy.get(message.getChatId());
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
        VacancyDTO dto = MapRepository.currentVacancy.get(message.getChatId());
        //.......................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        if (userService.isDigit(message.getText())) {
            if (Integer.parseInt(message.getText()) >= 7 && Integer.parseInt(message.getText()) <= 70) {
                /** set setWorkTime to vacancy */
                MapRepository.currentVacancy.get(message.getChatId()).setWorkTime(message.getText());
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
        VacancyDTO dto = MapRepository.currentVacancy.get(message.getChatId());
        //..............................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        /** set salary to vacancy */
        MapRepository.currentVacancy.get(message.getChatId()).setSalary(message.getText());
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
        VacancyDTO dto = MapRepository.currentVacancy.get(message.getChatId());
        //......................................//
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        /** set connect address to vacancy */
        MapRepository.currentVacancy.get(message.getChatId()).setConnectAddress(message.getText());
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
            MapRepository.currentVacancy.get(message.getChatId()).setExtraInfo(message.getText());
            VacancyDTO dto = MapRepository.currentVacancy.get(message.getChatId());
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
        VacancyDTO dto = MapRepository.currentVacancy.get(callbackQuery.getFrom().getId());
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
            MapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setEmployerName(null);
            editMessageText.setText("\uD83C\uDFE2  Ish beruvchi nomi . .");
        } else if (data.equals("address")) {
            MapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setWorkRegion(null);
            MapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setWorkDistinct(null);
            editMessageText.setText("\uD83D\uDDFA Manzil . .");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.regionsButtons());
        } else if (data.equals("specialty")) {
            MapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setSpecialty1(null);
            MapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setSpecialty2(null);
            editMessageText.setText("\uD83D\uDCCB Yo'nalish . .");
            editMessageText.setReplyMarkup(InlineKeyBoardUtil.specialtyButtons());
        } else if (data.equals("position")) {
            MapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setPosition(null);
            editMessageText.setText("\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim . .");
        } else if (data.equals("workTime")) {
            MapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setWorkTime(null);
            editMessageText.setText("\uD83D\uDD5E Haftalik ish soati . .");
        } else if (data.equals("salary")) {
            MapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setSalary(null);
            editMessageText.setText("\uD83D\uDCB0 Maosh . .");
        } else if (data.equals("callAddress")) {
            MapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setConnectAddress(null);
            editMessageText.setText("\uD83D\uDCE8  Aloqaga chiqish uchun link yoki tel raqam yozib qoldiring.");
        } else if (data.equals("extraInfo")) {
            MapRepository.currentVacancy.get(callbackQuery.getFrom().getId()).setExtraInfo(null);
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
        MapRepository.currentVacancy.remove(message.getChatId());
        return editMessageText;
    }

    public synchronized VacancyDTO save(CallbackQuery callbackQuery) {
        /** change user's step */
        UserDTO user = userService.getById(callbackQuery.getFrom().getId());
        user.setStep(UserStep.END);
        userService.update(user);
        /**  get currentVacancy to DTO */
        VacancyDTO dto = MapRepository.currentVacancy.get(callbackQuery.getFrom().getId());
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
        MapRepository.currentVacancy.remove(callbackQuery.getFrom().getId());
        return dto;
    }

    public synchronized EditMessageText delete(CallbackQuery callbackQuery) {
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
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n\uD83C\uDFE2 Ish beruvchi : " + entity.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + entity.getPosition() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + entity.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + entity.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + entity.getExtraInfo() + "\n\n《《   @JobZoneUzBot   》》");
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Vakansiyani o'chirish", "getDeletingVacancy" + entity.getId())), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Xabarni qisqartirish", "getLessVacancy" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("getLessVacancy")) {
            vacancyId = Integer.parseInt(callbackQuery.getData().substring(14));
            /**  get vacancyEntity from DB */
            Optional<VacancyEntity> optional = vacancyRepository.findById(vacancyId);
            VacancyEntity entity = optional.get();
            /**  get created date */
            String date = String.valueOf(entity.getCreatedDate());
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30  Vakansiya  \uD83D\uDD30" + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDDD3 Created Date : " + entity.getCreatedDate());
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Batafsil", "getMoreVacancy" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("getMoreNewVacancy")) {
            vacancyId = Integer.parseInt(callbackQuery.getData().substring(17));
            /**  get vacancyEntity from DB */
            Optional<VacancyEntity> optional = vacancyRepository.findById(vacancyId);
            VacancyEntity entity = optional.get();
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30 Vakansiya \uD83D\uDD30\n\n\uD83C\uDFE2 Ish beruvchi : " + entity.getEmployerName() + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim : " + entity.getPosition() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDD5E Haftalik ish soati : " + entity.getWorkTime() + "\n\uD83D\uDCF1 Aloqa : " + entity.getConnectAddress() + "\n\n‼\uFE0F Qo'shimcha : " + entity.getExtraInfo() + "\n\n《《   @JobZoneUzBot   》》");
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Xabarni qisqartirish", "getLessNewVacancy" + entity.getId())))));
        } else if (callbackQuery.getData().startsWith("getLessNewVacancy")) {
            vacancyId = Integer.parseInt(callbackQuery.getData().substring(17));
            /**  get vacancyEntity from DB */
            Optional<VacancyEntity> optional = vacancyRepository.findById(vacancyId);
            VacancyEntity entity = optional.get();
            /**  get created date */
            String date = String.valueOf(entity.getCreatedDate());
            editMessageText.setText("#" + entity.getId() + "  \uD83D\uDD30  Vakansiya  \uD83D\uDD30" + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDDD3 Created Date : " + entity.getCreatedDate());
            editMessageText.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Batafsil", "getMoreNewVacancy" + entity.getId())))));
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
            sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Batafsil", "getMoreVacancy" + entity.getId())))));
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
        employeeList.add(6793690581L);
        List<SendMessage> sendMessageList = new LinkedList<>();
        for (int i = 0; i < employeeList.size(); i++) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(employeeList.get(i));
            sendMessage.setText("#" + dto.getId() + "  \uD83D\uDD30  Vakansiya  \uD83D\uDD30" + "\n\uD83C\uDFE2 Ish beruvchi : " + dto.getEmployerName() + "\n\uD83D\uDCCB Yo'nalish : " + dto.getSpecialty1() + ", " + dto.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + dto.getSalary() + "\n\uD83D\uDDD3 Created date : " + dto.getCreatedDate());
            sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Batafsil", "getMoreNewVacancy" + dto.getId())))));
            sendMessageList.add(sendMessage);
        }
        return sendMessageList;
    }

    public SendMessage getSearchResultVacancy(CallbackQuery callbackQuery) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(callbackQuery.getFrom().getId());
        Integer vacancyId = Integer.parseInt(callbackQuery.getData().substring(20));
        /**  get vacancyEntity from DB */
        Optional<VacancyEntity> optional = vacancyRepository.findById(vacancyId);
        VacancyEntity entity = optional.get();
        /**  get created date */
        String date = String.valueOf(entity.getCreatedDate());
        sendMessage.setText("#" + entity.getId() + "  \uD83D\uDD30  Vakansiya  \uD83D\uDD30" + "\n\uD83D\uDDFA Manzil : " + entity.getWorkRegion() + ", " + entity.getWorkDistinct() + "\n\uD83D\uDCCB Yo'nalish : " + entity.getSpecialty1() + ", " + entity.getSpecialty2() + "\n\uD83D\uDCB0 Maosh : " + entity.getSalary() + "\n\uD83D\uDDD3 Created Date : " + entity.getCreatedDate());
        sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("To'liq ko'rsatish", "getMoreNewVacancy" + entity.getId())))));
        return sendMessage;
    }

    public List<VacancyEntity> checkingVacanciesDate() {
        return vacancyRepository.findAllByStatus(GeneralStatus.ACTIVE);
    }
}
