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
import uz.code.ishvakansiyabot.util.ReplyButtons;

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

    public void delete(Long userId) {
        UserDTO dto = getById(userId);
        dto.setStatus(GeneralStatus.DELETED);
        dto.setName(null);
        dto.setAge(null);
        dto.setAddress(null);
        update(dto);
    }

    public void changeStep(Long userId, UserStep userStep) {
        userRepository.changeUserStep(userId, userStep);
    }

    public SendMessage cancelPosting(UserDTO currentUser) {
        changeStep(currentUser.getTgId(), UserStep.END);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(currentUser.getTgId());
        if (currentUser.getStep().equals(UserStep.ADD_RESUME) || currentUser.getStep().equals(UserStep.ACCEPTING_RESUME) || currentUser.getStep().equals(UserStep.EDIT_RESUME)) {
            sendMessage.setText("❌ Rezyume bekor qilindi.");
            /**   remove vacancy from vacancyMap */
            MapRepository.currentResume.remove(currentUser.getTgId());
        } else if (currentUser.getStep().equals(UserStep.ADD_VACANCY) || currentUser.getStep().equals(UserStep.ACCEPTING_VACANCY) || currentUser.getStep().equals(UserStep.EDIT_VACANCY)) {
            sendMessage.setText("❌ Vakansiya bekor qilindi.");
            /**   remove vacancy from vacancyMap */
            MapRepository.currentVacancy.remove(currentUser.getTgId());
        } else if (currentUser.getStep().equals(UserStep.SEARCH_VACANCY) || currentUser.getStep().equals(UserStep.SEARCH_RESUME)) {
            sendMessage.setText("❌ Qidiruv bekor qilindi.");
            /**   remove user from searcherMap */
            MapRepository.currentSearcherMap.remove(currentUser.getTgId());
        }
        sendMessage.setReplyMarkup(ReplyButtons.mainMenuButtons());
        //..................................................//
        if (currentUser.getStep().equals(UserStep.CREATING) || currentUser.getStep().equals(UserStep.ACCEPTING_NEW_USER)) {
            sendMessage.setText("❌ Ro'yxatdan o'tish bekor qilindi.");
            sendMessage.setReplyMarkup(ReplyButtons.startButton());
            /** delete user from DB */
            delete(currentUser.getTgId());
        }

        return sendMessage;
    }
}
