package uz.code.ishvakansiyabot.util;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InlineKeyBoardUtil {
    public static InlineKeyboardButton button(String text, String callBack) {
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(callBack);
        return button;
    }

    static String[] toshkentArr = {"Toshkent.sh", "Toshkent.t", "Chirchiq.sh", "Quyi Chirchiq.t", "O'rta Chirchiq.t", "Yuqori Chirchiq.t", "Ohangaron.sh", "Ohangaron.t", "Bekobod.sh", "Bekobod.t", "Yangiyo'l.sh", "Yangiyo'l.t", "Angren.sh", "Bo'ka.t", "Bostonliq.t", "Olmaliq.sh", "Nurafshon.sh", "Qibray.t", "Oqqo'rg'on.t", "Parkent.t", "Piskent.t", "Zangiota.t", "Chinoz.t"};
    static String[] fargonaArr = {"Farg'ona.sh", "Farg'ona.t", "Qo'qon.sh","Marg'ilon.sh","Quvasoy.sh","Uchko'prik.t", "Oltiariq.t", "Beshariq.t", "O'zbekiston.t", "Bog'dod.t", "Buvayda.t", "Dang'ara.t", "Furqat.t", "Qo'shtepa.t", "Quva.t", "Rishton.t", "So'x.t", "Toshloq.t", "Yozyovon.t"};
    static String[] andijonArr = {"Toshkent.sh", "Toshkent.t", "Chirchiq.sh", "Quyi Chirchiq.t", "O'rta Chirchiq.t", "Yuqori Chirchiq.t", "Ohangaron.sh", "Ohangaron.t", "Bekobod.sh", "Bekobod.t", "Yangiyo'l.sh", "Yangiyo'l.t", "Angren.sh", "Bo'ka.t", "Bostonliq.t", "Olmaliq.sh", "Nurafshon.sh", "Qibray.t", "Oqqo'rg'on.t", "Parkent.t", "Piskent.t", "Zangiota.t", "Chinoz.t"};
    static String[] namanganArr = {"Toshkent.sh", "Toshkent.t", "Chirchiq.sh", "Quyi Chirchiq.t", "O'rta Chirchiq.t", "Yuqori Chirchiq.t", "Ohangaron.sh", "Ohangaron.t", "Bekobod.sh", "Bekobod.t", "Yangiyo'l.sh", "Yangiyo'l.t", "Angren.sh", "Bo'ka.t", "Bostonliq.t", "Olmaliq.sh", "Nurafshon.sh", "Qibray.t", "Oqqo'rg'on.t", "Parkent.t", "Piskent.t", "Zangiota.t", "Chinoz.t"};
    static String[] sirdaryoArr = {"Toshkent.sh", "Toshkent.t", "Chirchiq.sh", "Quyi Chirchiq.t", "O'rta Chirchiq.t", "Yuqori Chirchiq.t", "Ohangaron.sh", "Ohangaron.t", "Bekobod.sh", "Bekobod.t", "Yangiyo'l.sh", "Yangiyo'l.t", "Angren.sh", "Bo'ka.t", "Bostonliq.t", "Olmaliq.sh", "Nurafshon.sh", "Qibray.t", "Oqqo'rg'on.t", "Parkent.t", "Piskent.t", "Zangiota.t", "Chinoz.t"};
    static String[] jizzaxArr = {"Toshkent.sh", "Toshkent.t", "Chirchiq.sh", "Quyi Chirchiq.t", "O'rta Chirchiq.t", "Yuqori Chirchiq.t", "Ohangaron.sh", "Ohangaron.t", "Bekobod.sh", "Bekobod.t", "Yangiyo'l.sh", "Yangiyo'l.t", "Angren.sh", "Bo'ka.t", "Bostonliq.t", "Olmaliq.sh", "Nurafshon.sh", "Qibray.t", "Oqqo'rg'on.t", "Parkent.t", "Piskent.t", "Zangiota.t", "Chinoz.t"};
    static String[] samarqandArr = {"Toshkent.sh", "Toshkent.t", "Chirchiq.sh", "Quyi Chirchiq.t", "O'rta Chirchiq.t", "Yuqori Chirchiq.t", "Ohangaron.sh", "Ohangaron.t", "Bekobod.sh", "Bekobod.t", "Yangiyo'l.sh", "Yangiyo'l.t", "Angren.sh", "Bo'ka.t", "Bostonliq.t", "Olmaliq.sh", "Nurafshon.sh", "Qibray.t", "Oqqo'rg'on.t", "Parkent.t", "Piskent.t", "Zangiota.t", "Chinoz.t"};
    static String[] buxoroArr = {"Toshkent.sh", "Toshkent.t", "Chirchiq.sh", "Quyi Chirchiq.t", "O'rta Chirchiq.t", "Yuqori Chirchiq.t", "Ohangaron.sh", "Ohangaron.t", "Bekobod.sh", "Bekobod.t", "Yangiyo'l.sh", "Yangiyo'l.t", "Angren.sh", "Bo'ka.t", "Bostonliq.t", "Olmaliq.sh", "Nurafshon.sh", "Qibray.t", "Oqqo'rg'on.t", "Parkent.t", "Piskent.t", "Zangiota.t", "Chinoz.t"};
    static String[] qashqadaryoArr = {"Toshkent.sh", "Toshkent.t", "Chirchiq.sh", "Quyi Chirchiq.t", "O'rta Chirchiq.t", "Yuqori Chirchiq.t", "Ohangaron.sh", "Ohangaron.t", "Bekobod.sh", "Bekobod.t", "Yangiyo'l.sh", "Yangiyo'l.t", "Angren.sh", "Bo'ka.t", "Bostonliq.t", "Olmaliq.sh", "Nurafshon.sh", "Qibray.t", "Oqqo'rg'on.t", "Parkent.t", "Piskent.t", "Zangiota.t", "Chinoz.t"};
    static String[] surxondaryoArr = {"Toshkent.sh", "Toshkent.t", "Chirchiq.sh", "Quyi Chirchiq.t", "O'rta Chirchiq.t", "Yuqori Chirchiq.t", "Ohangaron.sh", "Ohangaron.t", "Bekobod.sh", "Bekobod.t", "Yangiyo'l.sh", "Yangiyo'l.t", "Angren.sh", "Bo'ka.t", "Bostonliq.t", "Olmaliq.sh", "Nurafshon.sh", "Qibray.t", "Oqqo'rg'on.t", "Parkent.t", "Piskent.t", "Zangiota.t", "Chinoz.t"};
    static String[] navoiyArr = {"Toshkent.sh", "Toshkent.t", "Chirchiq.sh", "Quyi Chirchiq.t", "O'rta Chirchiq.t", "Yuqori Chirchiq.t", "Ohangaron.sh", "Ohangaron.t", "Bekobod.sh", "Bekobod.t", "Yangiyo'l.sh", "Yangiyo'l.t", "Angren.sh", "Bo'ka.t", "Bostonliq.t", "Olmaliq.sh", "Nurafshon.sh", "Qibray.t", "Oqqo'rg'on.t", "Parkent.t", "Piskent.t", "Zangiota.t", "Chinoz.t"};
    static String[] xorazmArr = {"Toshkent.sh", "Toshkent.t", "Chirchiq.sh", "Quyi Chirchiq.t", "O'rta Chirchiq.t", "Yuqori Chirchiq.t", "Ohangaron.sh", "Ohangaron.t", "Bekobod.sh", "Bekobod.t", "Yangiyo'l.sh", "Yangiyo'l.t", "Angren.sh", "Bo'ka.t", "Bostonliq.t", "Olmaliq.sh", "Nurafshon.sh", "Qibray.t", "Oqqo'rg'on.t", "Parkent.t", "Piskent.t", "Zangiota.t", "Chinoz.t"};
    static String[] qoraqalpogistonArr = {"Toshkent.sh", "Toshkent.t", "Chirchiq.sh", "Quyi Chirchiq.t", "O'rta Chirchiq.t", "Yuqori Chirchiq.t", "Ohangaron.sh", "Ohangaron.t", "Bekobod.sh", "Bekobod.t", "Yangiyo'l.sh", "Yangiyo'l.t", "Angren.sh", "Bo'ka.t", "Bostonliq.t", "Olmaliq.sh", "Nurafshon.sh", "Qibray.t", "Oqqo'rg'on.t", "Parkent.t", "Piskent.t", "Zangiota.t", "Chinoz.t"};

    public static InlineKeyboardMarkup regionsButtons() {
        return InlineKeyboardButtonUtil.keyboard(
                InlineKeyboardButtonUtil.collection(
                        InlineKeyboardButtonUtil.row(
                                InlineKeyboardButtonUtil.button("Toshkent", "Toshkent")),
                        InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Qoraqalpog'iston",
                                "Qoraqalpog'iston")),
                        InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Andijon", "Andijon"),
                                InlineKeyboardButtonUtil.button("Buxoro", "Buxoro")),
                        InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Farg'ona", "Farg'ona"),
                                InlineKeyboardButtonUtil.button("Jizzax", "Jizzax")),
                        InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Namangan", "Namangan"),
                                InlineKeyboardButtonUtil.button("Navoiy", "Navoiy")),
                        InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Qashqadaryo", "Qashqadaryo"),
                                InlineKeyboardButtonUtil.button("Samarqand", "Samarqand")),
                        InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Sirdaryo", "Sirdaryo"),
                                InlineKeyboardButtonUtil.button("Surxondaryo", "Surxondaryo")),
                        InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Xorazm", "Xorazm")
                        )));
    }
    public static String[] chooseRegionFromCallBackQuery(CallbackQuery callbackQuery) {
        if (callbackQuery.getData().equals("Toshkent")) {
            return toshkentArr;
        } else if (callbackQuery.getData().equals("Buxoro")) {
            return buxoroArr;
        }else if (callbackQuery.getData().equals("Farg'ona")) {
            return fargonaArr;
        }else if (callbackQuery.getData().equals("Namangan")) {
            return namanganArr;
        }else if (callbackQuery.getData().equals("Andijon")) {
            return andijonArr;
        }else if (callbackQuery.getData().equals("Sirdaryo")) {
            return sirdaryoArr;
        }else if (callbackQuery.getData().equals("Jizzax")) {
            return jizzaxArr;
        }else if (callbackQuery.getData().equals("Samarqand")) {
            return samarqandArr;
        }else if (callbackQuery.getData().equals("Navoiy")) {
            return navoiyArr;
        }else if (callbackQuery.getData().equals("Qashqadaryo")) {
            return qashqadaryoArr;
        }else if (callbackQuery.getData().equals("Surxondaryo")) {
            return surxondaryoArr;
        }else if (callbackQuery.getData().equals("Xorazm")) {
            return xorazmArr;
        }else if (callbackQuery.getData().equals("Qoraqalpog'iston")) {
            return qoraqalpogistonArr;
        }
        return null;
    }

    public static InlineKeyboardMarkup districtButtons(CallbackQuery callbackQuery) {
        String[] arr = chooseRegionFromCallBackQuery(callbackQuery);
        int a = arr.length / 3;
        int qoldiq = arr.length % 3;
        int count = 0;
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        for (int i = 0; i < a; i++) {
            List<InlineKeyboardButton> row = new LinkedList<>();
            for (int j = 0; j < 3; j++) {
                InlineKeyboardButton button = InlineKeyBoardUtil.button(arr[count], arr[count]);
                row.add(button);
                count++;
            }
            rowList.add(row);
        }
        if (qoldiq != 0) {
            List<InlineKeyboardButton> row = new LinkedList<>();
            for (int j = 0; j < qoldiq; j++) {
                InlineKeyboardButton button = InlineKeyBoardUtil.button(arr[count], arr[count]);
                row.add(button);
                count++;
            }
            rowList.add(row);
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup acceptingButtons() {
        return InlineKeyboardButtonUtil.keyboard(
                InlineKeyboardButtonUtil.collection(
                        InlineKeyboardButtonUtil.row(
                                InlineKeyboardButtonUtil.button(
                                        "✅ Tasdiqlash", "tasdiqlash")),
                        InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("⚠\uFE0F Tahrirlash", "tahrirlash"))));
    }

    public static InlineKeyboardMarkup signUpOrAboutBotButton() {
        return InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDCF2 Ro'yxatdan o'tish", "signUp")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDCDC Bot haqida batafsil . .", "aboutBot"))));
    }

    public static InlineKeyboardMarkup signUpButton() {
        return InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDCF2 Ro'yxatdan o'tish", "signUp"))));
    }

    public static InlineKeyboardMarkup specialtyButtons(CallbackQuery callbackQuery) {
        return InlineKeyboardButtonUtil.keyboard(
                InlineKeyboardButtonUtil.collection(
                        InlineKeyboardButtonUtil.row(
                                InlineKeyboardButtonUtil.button("\uD83D\uDC69\uD83C\uDFFB\u200D\uD83C\uDFEB  Ta'lim", "Ta'lim")),
                        InlineKeyboardButtonUtil.row(
                                InlineKeyboardButtonUtil.button("\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB  Dasturlash", "Dasturlash")),
                        InlineKeyboardButtonUtil.row(
                                InlineKeyboardButtonUtil.button("\uD83D\uDDA5  Dizayn", "Dizayn")),
                        InlineKeyboardButtonUtil.row(
                                InlineKeyboardButtonUtil.button("\uD83D\uDCBC  Bugalteriya", "Bugalteriya")),
                        InlineKeyboardButtonUtil.row(
                                InlineKeyboardButtonUtil.button("\uD83D\uDCF1  Marketing", "Marketing"))
                        ));

    }

    public static InlineKeyboardMarkup talimButtons(CallbackQuery callbackQuery) {
        return InlineKeyboardButtonUtil.keyboard(
                InlineKeyboardButtonUtil.collection(
                        InlineKeyboardButtonUtil.row(
                                InlineKeyboardButtonUtil.button("Matematika", "Matematika")),
                        InlineKeyboardButtonUtil.row(
                                InlineKeyboardButtonUtil.button("Ingliz tili", "Ingliz tili")),
                        InlineKeyboardButtonUtil.row(
                                InlineKeyboardButtonUtil.button("Biologiya", "Biologiya")),
                        InlineKeyboardButtonUtil.row(
                                InlineKeyboardButtonUtil.button("Mental Arifmetika", "Mental Arifmetika")),
                        InlineKeyboardButtonUtil.row(
                                InlineKeyboardButtonUtil.button("Fizika", "Fizika"))
                ));
    }
}
