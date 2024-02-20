package uz.code.ishvakansiyabot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class ReplyButtons {
    public static ReplyKeyboardMarkup mainMenuButtons() {
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
        row5.add("Adminㅤ");

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup cancelButton() {
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

    public static ReplyKeyboardMarkup settingsButtons() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("\uD83D\uDC64 Profileㅤ");
        row1.add("\uD83D\uDCCA Statisticsㅤ");
        row1.add("\uD83D\uDCB3 Donateㅤ");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("⬅\uFE0F Ortgaㅤ");
        keyboard.add(row1);
        keyboard.add(row2);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup profileButtons() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Mening vakansiyalarimㅤ");
        row1.add("Mening rezyumelarimㅤ ");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Profilni tahrirlashㅤ ");
        row2.add("Profilni o'chirishㅤ ");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("⬅\uFE0F Ortgaㅤ");
        row3.add("Adminㅤ");
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup editProfileButtons() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Ismㅤ");
        row1.add("Yoshㅤ");
        row1.add("Manzilㅤ");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("⬅\uFE0F Ortgaㅤ");

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup startButton() {
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

    public static ReplyKeyboardRemove removeButton() {
        ReplyKeyboardRemove removeButton = new ReplyKeyboardRemove();
        removeButton.setSelective(true);
        removeButton.setRemoveKeyboard(true);
        return removeButton;
    }

    public static ReplyKeyboard acceptingButtons() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("✅ Tasdiqlashㅤ");
        row1.add("⚠\uFE0F Tahrirlashㅤ");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Bekor qilishㅤ");

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static ReplyKeyboard acceptingDeletingButtons() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("✅ Tasdiqlashㅤ");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("⬅\uFE0F Ortgaㅤ");

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static ReplyKeyboard regionsButtons() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Toshkent");
        row1.add("Buxoro");
        row1.add("Samarqand");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Jizzax");
        row2.add("Navoiy");
        row2.add("Qashqadaryo");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Farg'ona");
        row3.add("Namangan");
        row3.add("Andijon");

        KeyboardRow row4 = new KeyboardRow();
        row4.add("Surxondaryo");
        row4.add("Xorazm");
        row4.add("Sirdaryo");

        KeyboardRow row5 = new KeyboardRow();
        row5.add("Qoraqalpog'iston");


        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
