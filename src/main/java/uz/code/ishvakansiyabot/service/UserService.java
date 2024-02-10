package uz.code.ishvakansiyabot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.code.ishvakansiyabot.dto.UserDTO;
import uz.code.ishvakansiyabot.entity.UserEntity;
import uz.code.ishvakansiyabot.enums.GeneralStatus;
import uz.code.ishvakansiyabot.enums.UserStep;
import uz.code.ishvakansiyabot.repository.MapRepository;
import uz.code.ishvakansiyabot.repository.ResumeRepository;
import uz.code.ishvakansiyabot.repository.UserRepository;
import uz.code.ishvakansiyabot.repository.VacancyRepository;


import java.util.*;


@Service
public class UserService {
    @Autowired
    MapRepository mapRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    VacancyRepository vacancyRepository;
    @Autowired
    ResumeRepository resumeRepository;


    public Boolean isDigit(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    public UserDTO getById(Long id) {
        UserEntity entity = userRepository.findByTgId(id);
        if (entity == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setTgId(entity.getTgId());
        userDTO.setBotId(entity.getBotId());
        userDTO.setAge(entity.getAge());
        userDTO.setName(entity.getName());
        userDTO.setBalance(entity.getBalance());
        userDTO.setAddress(entity.getAddress());
        userDTO.setCreatedDate(entity.getCreatedDate());
        userDTO.setStatus(entity.getStatus());
        userDTO.setStep(entity.getStep());
        userDTO.setRole(entity.getRole());
        return userDTO;
    }

    public void update(UserDTO dto) {
        UserEntity entity = userRepository.findByTgId(dto.getTgId());
        entity.setName(dto.getName());
        entity.setAge(dto.getAge());
        entity.setAddress(dto.getAddress());
        entity.setBalance(dto.getBalance());
        entity.setRole(dto.getRole());
        entity.setStep(dto.getStep());
        entity.setStatus(dto.getStatus());
        entity.setCreatedDate(dto.getCreatedDate());
        userRepository.save(entity);
    }

    public void delete(Long userId) {
        UserDTO dto = getById(userId);
        dto.setStatus(GeneralStatus.DELETED);
        dto.setName(null);
        dto.setAge(null);
        dto.setAddress(null);
        update(dto);
    }

    public ReplyKeyboardMarkup mainMenuButtons() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Vakansiya joylashㅤ");
        row1.add("Rezyume joylashㅤ");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Vakansiya izlashㅤ");
        row2.add("Rezyume izlashㅤ");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Mening vakansiyalarimㅤ");
        row3.add("Mening rezyumelarimㅤ");

        KeyboardRow row4 = new KeyboardRow();
        row4.add("Sozlamalarㅤ");

        KeyboardRow row5 = new KeyboardRow();
        row5.add("Admin/Bot supportㅤ");

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    public ReplyKeyboardMarkup cancelButton() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Bekor qilishㅤ");
        keyboard.add(row1);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public ReplyKeyboardMarkup startButton() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("/start");
        keyboard.add(row1);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public ReplyKeyboardRemove removeButton() {
        ReplyKeyboardRemove removeButton = new ReplyKeyboardRemove();
        removeButton.setSelective(true);
        removeButton.setRemoveKeyboard(true);
        return removeButton;
    }

    public SendMessage cancelPosting(UserDTO currentUser) {
        userRepository.changeUserStep(currentUser.getTgId(), UserStep.END);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(currentUser.getTgId());
        if (currentUser.getStep().equals(UserStep.ADD_RESUME)) {
            sendMessage.setText("❌ Rezyume yuklash bekor qilindi.");
            /**   remove vacancy from vacancyMap */
            mapRepository.currentResume.remove(currentUser.getTgId());
        } else if (currentUser.getStep().equals(UserStep.ADD_VACANCY)) {
            sendMessage.setText("❌ Vakansiya yuklash bekor qilindi.");
            /**   remove vacancy from vacancyMap */
            mapRepository.currentVacancy.remove(currentUser.getTgId());
        } else if (currentUser.getStep().equals(UserStep.ACCEPTING_VACANCY)) {
            sendMessage.setText("❌ Vakansiya yuklash bekor qilindi.");
            /**   remove vacancy from vacancyMap */
            mapRepository.currentVacancy.remove(currentUser.getTgId());
        } else if (currentUser.getStep().equals(UserStep.ACCEPTING_RESUME)) {
            sendMessage.setText("❌ Rezyume yuklash bekor qilindi.");
            /**   remove vacancy from vacancyMap */
            mapRepository.currentResume.remove(currentUser.getTgId());
        } else if (currentUser.getStep().equals(UserStep.EDIT_VACANCY)) {
            sendMessage.setText("❌ Vakansiya yuklash bekor qilindi.");
            /**   remove vacancy from vacancyMap */
            mapRepository.currentVacancy.remove(currentUser.getTgId());
        } else if (currentUser.getStep().equals(UserStep.EDIT_RESUME)) {
            sendMessage.setText("❌ Rezyume yuklash bekor qilindi.");
            /**   remove vacancy from vacancyMap */
            mapRepository.currentResume.remove(currentUser.getTgId());
        }
        sendMessage.setReplyMarkup(mainMenuButtons());
        //..................................................//
        if (currentUser.getStep().equals(UserStep.CREATING) || currentUser.getStep().equals(UserStep.ACCEPTING_NEW_USER)) {
            sendMessage.setText("❌ Ro'yxatdan o'tish bekor qilindi.");
            sendMessage.setReplyMarkup(startButton());
            /** delete user from DB */
            delete(currentUser.getTgId());
        }

        return sendMessage;
    }
}
