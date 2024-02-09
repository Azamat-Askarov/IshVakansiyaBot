package uz.code.ishvakansiyabot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.code.ishvakansiyabot.dto.UserDTO;
import uz.code.ishvakansiyabot.entity.ResumeEntity;
import uz.code.ishvakansiyabot.entity.UserEntity;
import uz.code.ishvakansiyabot.entity.VacancyEntity;
import uz.code.ishvakansiyabot.enums.UserStep;
import uz.code.ishvakansiyabot.repository.ResumeRepository;
import uz.code.ishvakansiyabot.repository.UserRepository;
import uz.code.ishvakansiyabot.repository.VacancyRepository;
import uz.code.ishvakansiyabot.util.InlineKeyboardButtonUtil;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {
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
}
