package uz.code.ishvakansiyabot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.code.ishvakansiyabot.dto.UserDTO;
import uz.code.ishvakansiyabot.entity.UserEntity;
import uz.code.ishvakansiyabot.enums.GeneralStatus;
import uz.code.ishvakansiyabot.enums.UserStep;
import uz.code.ishvakansiyabot.repository.UserRepository;
import uz.code.ishvakansiyabot.util.InlineKeyBoardUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public EditMessageText sendAboutBotMsg(CallbackQuery callbackQuery) {
        EditMessageText aboutBotEditMsg = new EditMessageText();
        aboutBotEditMsg.setText("\uD83D\uDCDC Bot haqida batafsil ma'lumot \uD83D\uDCDC\n" + "\n" + "✅ Botga vakansiya joylashingiz mumkin. Vakansiyada Ish beruvchi nomi, ish yo'nalishi, korxonaning manzili, ish haqi miqdori va yana qo'shimcha ma'lumotlar mavjud bo'ladi.\n" + "✅ Botga rezyume joylashingiz mumkin.\n" + "Rezyumeda xodimning ismi, xodim qaysi hududdan ish izlayapti, xodimning mutaxxasisligi, qancha maosh xohlashi va qo'shimcha ma'lumotlar mavjud bo'ladi.\n" + "✅Mening vakansiyalarim va Mening rezyumelarim qismlari mavjud.\n" + "Bu qismda siz rezyume yoki vakansiyangizni olib tashlashingiz mumkin.\n" + "✅ Vakansiya va rezyume izlash.\n" + "Bu bo'limda o'zingiz xohlagan filtr asosida botga yuklangan rezyume yoki vakansiyalarni izlab topishingiz mumkin. \n" + "Masalan : Ish joyi Toshkent shahri va yo'nalish Grafik dizayner bo'lgan barcha vakansiyalarni bot sizga uzatadi.\n" + "✅ va eng asosiy bo'limlardan biri..\n" + "Botga biror foydalanuvchi qaysidir yo'nalishda rezyume joylasa o'sha yo'nalish bo'yicha xodim izlayotgan barcha foydalanuvchilarga xodimning rezyumesi botga qo'shilgan zahoti uzatiladi. Buning natijasida siz o'zingizga kerakli bo'lgan xodimni tezroq topishingizga yordam beradi.\n" + "✅ va shuning teskarisi..\n" + "Biror ish beruvchi botga vakansiya joylashtirsa belgilangan yo'nalish bo'yicha rezyume joylashtirgan barcha foydalanuvchilarga vakansiya xabari yuboriladi.");
        aboutBotEditMsg.setMessageId(callbackQuery.getMessage().getMessageId());
        aboutBotEditMsg.setChatId(callbackQuery.getFrom().getId());
        aboutBotEditMsg.setReplyMarkup(InlineKeyBoardUtil.signUpButton());
        return aboutBotEditMsg;
    }

    public void create(Long id) {
        UserEntity userEntity = new UserEntity();
        userEntity.setTgId(id);
        userRepository.save(userEntity);
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

    public SendMessage setName(String text, Long tgId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgId);
        if (text.contains("0") || text.contains("1") || text.contains("2") || text.contains("3") || text.contains("4") || text.contains("5") || text.contains("6") || text.contains("7") || text.contains("8") || text.contains("9")) {
            sendMessage.setText("⚠\uFE0F Raqam ishlatmang.\n" + "✍\uD83C\uDFFB Ismingiz . .");
        } else if (text.length() < 3 || text.length() > 16) {
            sendMessage.setText("⚠\uFE0F Ism uzunligi [3 ; 16] oraliqda bo'lsin\n✍\uD83C\uDFFB Ismingiz . .");
        } else {
            UserEntity entity = userRepository.findByTgId(tgId);
            entity.setName(text);
            userRepository.save(entity);
            sendMessage.setText("✍\uD83C\uDFFB Yoshingiz . .");
        }
        return sendMessage;
    }

    public SendMessage setAge(String text, Long tgId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgId);
        if (isDigit(text) && Long.parseLong(text) >= 10 && Long.parseLong(text) <= 60) {
            UserEntity entity = userRepository.findByTgId(tgId);
            entity.setAge(Byte.valueOf(text));
            userRepository.save(entity);
            sendMessage.setText("\uD83D\uDDFA Yashash manzilingiz . .");
            sendMessage.setReplyMarkup(InlineKeyBoardUtil.regionsButtons());
        } else {
            sendMessage.setText("⚠\uFE0F Yoshingizni to'g'ri kiriting . .");
        }
        return sendMessage;
    }

    public EditMessageText setAddress(CallbackQuery callBackData) {
        /** button dan kelgan viloyatni set qilish va buttonni editMsg qilib tasdiqlashsni so'rash */
        UserEntity entity = userRepository.findByTgId(callBackData.getFrom().getId());
        entity.setAddress(callBackData.getData());
        entity.setStep(UserStep.ACCEPTING_NEW_USER);
        userRepository.save(entity);
        /** viloyatlar buttonni editMsg qilish */
        UserDTO dto = getById(callBackData.getFrom().getId()); // user ni get qilish
        EditMessageText editMessageText = new EditMessageText();// editMsg object olish
        editMessageText.setMessageId(callBackData.getMessage().getMessageId());
        editMessageText.setText("\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ma'lumotlaringiz.\n" + "\uD83D\uDD37 Ism : " + dto.getName() + "\n" + "\uD83D\uDD36 Yosh : " + dto.getAge() + "\n\uD83D\uDD37 Manzil : " + dto.getAddress() + "\n\uD83D\uDD36 Balans : " + dto.getBalance() + " so'm\n\uD83D\uDD37 Bot ID : " + dto.getBotId() + "\n\n\uD83D\uDCDC Ushbu ma'lumotlarni tasdiqlaysizmi ?");
        editMessageText.setChatId(dto.getTgId());
        editMessageText.setReplyMarkup(InlineKeyBoardUtil.acceptingButtons());//editMsg ga tasdiqlash button ni set qilish
        return editMessageText;
    }

    public EditMessageText acceptNewUser(CallbackQuery callbackQuery) {
        EditMessageText editMsg = new EditMessageText(); // make eccept (editMsg)button
        UserDTO currentUser = getById(callbackQuery.getFrom().getId()); // get currentUser

        if (callbackQuery.getData().equals("tasdiqlash")) {
            currentUser.setStep(UserStep.END);
            currentUser.setStatus(GeneralStatus.ACTIVE);
            currentUser.setCreatedDate(LocalDateTime.now());
            update(currentUser);
            editMsg.setChatId(currentUser.getTgId());
            editMsg.setMessageId(callbackQuery.getMessage().getMessageId());
            editMsg.setText("\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC Ma'lumotlaringiz.\n" + "\uD83D\uDD37 Ism : " + currentUser.getName() + "\n" + "\uD83D\uDD36 Yosh : " + currentUser.getAge() + "\n\uD83D\uDD37 Manzil : " + currentUser.getAddress() + "\n\uD83D\uDD36 Balans : " + currentUser.getBalance() + " so'm\n\uD83D\uDD37 Bot ID : " + currentUser.getBotId() + "\n\uD83D\uDD36 Ro'yxatdan o'tgan vaqt : " + currentUser.getCreatedDate() + "\n\n✅ Muvafaqqiyatli ro'yxatdan o'tdingiz.");
        } else if (callbackQuery.getData().equals("tahrirlash")) {
            currentUser.setName(null);
            currentUser.setAge(null);
            currentUser.setAddress(null);
            currentUser.setStep(UserStep.CREATING);
            update(currentUser);
            editMsg.setChatId(currentUser.getTgId());
            editMsg.setMessageId(callbackQuery.getMessage().getMessageId());
            editMsg.setText("\uD83D\uDD30 Ro'yxatdan o'tish \uD83D\uDD30\n✍\uD83C\uDFFB Ismingiz . .");
        }
        return editMsg;
    }

    public Boolean isDigit(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public SendMessage helloUser(User user) {
        SendMessage sendFirstMessage = new SendMessage();
        sendFirstMessage.setChatId(user.getId());
        sendFirstMessage.setReplyMarkup(InlineKeyBoardUtil.signUpOrAboutBotButton());
        sendFirstMessage.setText("\uD83D\uDC4B\uD83C\uDFFB Assalomu alaykum.\n" + "\uD83D\uDD0D Botimiz orqali o'zingizga mos ish topishingiz yoki ma'lum bir yo'nalishda" + " xodim kerak bo'lsa vakansiya joylashtirishingiz mumkin.\n" + "\uD83D\uDCF2 Botdan ro'yxatdan o'ting va bot imkoniyatlaridan to'liq foydalaning.");
        return sendFirstMessage;
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
