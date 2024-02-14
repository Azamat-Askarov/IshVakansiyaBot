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

    static String[] toshkentArr = {"Toshkent.sh", "Toshkent.t", "Chirchiq.sh", "Quyi Chirchiq.t", "O'rta Chirchiq.t", "Yuqori Chirchiq.t", "Ohangaron.sh", "Ohangaron.t", "Bekobod.sh", "Bekobod.t", "Yangiyo'l.sh", "Yangiyo'l.t", "Angren.sh", "Bo'ka.t", "Bo'stonliq.t", "Olmaliq.sh", "Nurafshon.sh", "Qibray.t", "Oqqo'rg'on.t", "Parkent.t", "Piskent.t", "Zangiota.t", "Chinoz.t"};
    static String[] fargonaArr = {"Farg'ona.sh", "Farg'ona.t", "Qo'qon.sh", "Marg'ilon.sh", "Quvasoy.sh", "Uchko'prik.t", "Oltiariq.t", "Beshariq.t", "O'zbekiston.t", "Bog'dod.t", "Buvayda.t", "Dang'ara.t", "Furqat.t", "Qo'shtepa.t", "Quva.t", "Rishton.t", "So'x.t", "Toshloq.t", "Yozyovon.t"};
    static String[] andijonArr = {"Andijon.sh", "Andijon.t", "Xonobod.sh", "Asaka.t", "Baliqchi.", "Bo'z.t", "Buloqboshi.t", "Izboskan.t", "Jalolquduq.t", "Marhamat.t", "Oltinko'l.t", "Paxtaobod.t", "Qo'rg'ontepa.t", "Shahrixon.t", "Ulug'nor.t", "Xo'jaobod .t"};
    static String[] sirdaryoArr = {"Guliston.sh", "Guliston.t", "Yangiyer.sh", "Shirin.sh", "Sirdaryo.t", "Boyovut.t", "Oqoltin.t", "Sardoba.t", "Sayxunobod.t", "Xovos.t"};
    static String[] namanganArr = {"Namangan.sh", "Namangan.t", "Chortoq.t", "Chust.t", "Kosonsoy.t", "Mingbuloq.t", "Norin.t", "Pop.t", "To'raqo'rg'on.t", "Uchqo'rg'on.t", "Uychi.t", "Yangiqo'rgon.t"};
    static String[] jizzaxArr = {"Jizzax.sh", "Jizzax.t", "Arnasoy.", "Baxmal.t", "Do'stlik.t", "Forish.t", "G'allaorol.t", "Mirzacho'l.t", "Paxtakor.t", "Yangiobod.t", "Zafarobod.t", "Zarband.t", "Zomin.t"};
    static String[] samarqandArr = {"Samarqand.sh", "Samarqand.t", "Kattaqo'rg'on.sh", "Kattaqo'rg'on.t", "Bulung'ur.t", "Ishtixon.t", "Jomboy.t", "Narpay.t", "Nurobod.t", "Oqdaryo.t", "Past darg'om.t", "Paxtachi.t", "Poyariq.t", "Qo'shrabot.t", "Toyloq.t", "Urgut.t"};
    static String[] buxoroArr = {"Buxoro.sh", "Buxoro.t", "Kogon.sh", "Kogon.t", "G'ijduvon.t", "Jondor.t", "Olot.t", "Peshku.t", "Qorako'l.t", "Qorovulbozor.t", "Romitan.t", "Shofirkon.t", "Vobkent.t"};
    static String[] qashqadaryoArr = {"Qarshi.sh", "Qarshi.t", "Shahrisabz.sh", "Chiroqchi.t", "Dehqonobod.t", "G'uzor.t", "Kasbi.t", "Kitob.t", "Koson.t", "Mirishkor.t", "Muborak.t", "Nishon.t", "Qamashi.t", "Yakkabog.t"};
    static String[] surxondaryoArr = {"Termiz.sh", "Termiz.t", "Angor.t", "Bandixon.t", "Boysun.t", "Denov.t", "Jarqo'rg'on.t", "Muzrobot.t", "Oltinsoy.t", "Qiziriq.t", "Qumqo'rg'on.t", "Sariosiyo.t", "Sherobod.t", "Sho'rchi.t", "Uzun.t"};
    static String[] navoiyArr = {"Navoiy.sh", "Zarafshon.sh", "Karmana.t", "Konimex.t", "Navbahor.t", "Nurota.t", "Qiziltepa.t", "Tomdi.t", "Uchquduq.t", "Xatirchi.t"};
    static String[] xorazmArr = {"Urganch.t", "Urganch.t", "Xiva.t", "Bog'ot.t", "Gurlan.t", "Qo'shko'pir.t", "Shovot.t", "Xazorasp.t", "Xonqa.t", "Yangiariq.t", "Yangibozor.t"};
    static String[] qoraqalpogistonArr = {"No'kis.sh", "No'kis.t", "Amudaryo.t", "Bo'zataw.t", "Beruniy.t", "Shimbay.t", "Ellikqala.t", "Kegeyli.t", "Moynaq.t", "Qanliko'l.t", "Qaraózek.t", "Qońírat.t", "Shomanay.t", "Taqiyatas.sh", "Taxtakópir .t", "To'rtko'l.t", "Xojeli.t"};

    public static InlineKeyboardMarkup signUpOrAboutBotButton() {
        return InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDCF2 Ro'yxatdan o'tish", "signUp")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDCDC Bot haqida batafsil . .", "aboutBot"))));
    }

    public static InlineKeyboardMarkup signUpButton() {
        return InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDCF2 Ro'yxatdan o'tish", "signUp"))));
    }

    public static InlineKeyboardMarkup regionsButtons() {
        return InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Toshkent", "Toshkent")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Qoraqalpog'iston", "Qoraqalpog'iston")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Andijon", "Andijon"), InlineKeyboardButtonUtil.button("Buxoro", "Buxoro")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Farg'ona", "Farg'ona"), InlineKeyboardButtonUtil.button("Jizzax", "Jizzax")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Namangan", "Namangan"), InlineKeyboardButtonUtil.button("Navoiy", "Navoiy")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Qashqadaryo", "Qashqadaryo"), InlineKeyboardButtonUtil.button("Samarqand", "Samarqand")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Sirdaryo", "Sirdaryo"), InlineKeyboardButtonUtil.button("Surxondaryo", "Surxondaryo")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Xorazm", "Xorazm"))));
    }

    public static String[] chooseRegionFromCallBackQuery(CallbackQuery callbackQuery) {
        if (callbackQuery.getData().equals("Toshkent")) {
            return toshkentArr;
        } else if (callbackQuery.getData().equals("Buxoro")) {
            return buxoroArr;
        } else if (callbackQuery.getData().equals("Farg'ona")) {
            return fargonaArr;
        } else if (callbackQuery.getData().equals("Namangan")) {
            return namanganArr;
        } else if (callbackQuery.getData().equals("Andijon")) {
            return andijonArr;
        } else if (callbackQuery.getData().equals("Sirdaryo")) {
            return sirdaryoArr;
        } else if (callbackQuery.getData().equals("Jizzax")) {
            return jizzaxArr;
        } else if (callbackQuery.getData().equals("Samarqand")) {
            return samarqandArr;
        } else if (callbackQuery.getData().equals("Navoiy")) {
            return navoiyArr;
        } else if (callbackQuery.getData().equals("Qashqadaryo")) {
            return qashqadaryoArr;
        } else if (callbackQuery.getData().equals("Surxondaryo")) {
            return surxondaryoArr;
        } else if (callbackQuery.getData().equals("Xorazm")) {
            return xorazmArr;
        } else if (callbackQuery.getData().equals("Qoraqalpog'iston")) {
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
        return InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("✅ Tasdiqlash", "accept")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("⚠\uFE0F Tahrirlash", "edit")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("❌ Bekor qilish", "cancel"))));
    }

    public static InlineKeyboardMarkup specialtyButtons() {
        return InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Ta'lim", "Ta'lim")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Chet tili", "Chet tili")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("IT", "IT")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Management", "Management")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Media va dizayn", "Media va dizayn")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Iqtisodiyot", "Iqtisodiyot"))));
    }

    public static InlineKeyboardMarkup chooseSpecialty2FromCallBackQuery(CallbackQuery callbackQuery) {
        if (callbackQuery.getData().equals("Ta'lim")) {
            return educateButtons();
        } else if (callbackQuery.getData().equals("Chet tili")) {
            return foreignLanguageButtons();
        } else if (callbackQuery.getData().equals("IT")) {
            return ITButtons();
        } else if (callbackQuery.getData().equals("Management")) {
            return managementButtons();
        } else if (callbackQuery.getData().equals("Media va dizayn")) {
            return mediaAndDesignButtons();
        } else if (callbackQuery.getData().equals("Iqtisodiyot")) {
            return economicsButtons();
        }
        return null;
    }

    public static InlineKeyboardMarkup educateButtons() {
        return InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Matematika", "Matematika")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Biologiya", "Biologiya")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Kimyo", "Kimyo")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Fizika", "Fizika")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Huquq", "Huquq")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Tarix", "Tarix")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Ona-tili", "Ona-tili")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Mental Arifmetika", "Mental Arifmetika")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Maktabgacha tayyorlov", "Maktabgacha tayyorlov")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Logoped", "Logoped")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("IT trainer", "IT trainer"))));
    }

    public static InlineKeyboardMarkup foreignLanguageButtons() {
        return InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Ingliz tili", "Ingliz tili")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Arab tili", "Arab tili")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Koreys tili", "Koreys tili")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Rus tili", "Rus tili")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("IELTS", "IELTS")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("CEFR", "CEFR")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("GMAT", "GMAT")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("SAT", "SAT"))));
    }

    public static InlineKeyboardMarkup ITButtons() {
        return InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Full-Stack developer", "Full-Stack developer")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Frontend developer", "Frontend developer")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Backend developer", "Backend developer")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Mobile developer", "Mobile developer")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("(QA) Engineer", "(QA) Engineer")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("(AI) Engineer", "(AI) Engineer")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Data scientist", "Data scientist")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Data analyst", "Data analyst")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Security analyst", "Security analyst")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("System adminstrator", "System adminstrator")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Network adminstrator", "Network adminstrator")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("IT trainer", "IT trainer"))));
    }

    public static InlineKeyboardMarkup mediaAndDesignButtons() {
        return InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("SMM", "SMM")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Graphic designer", "Graphic designer")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("UI/UX designer", "UI/UX designer")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Content creator", "Content creator")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Mobilograph", "Mobilograph")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Videographer", "Videographer")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Video editor", "Video editor")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Copywriter", "Copywriter"))));
    }

    public static InlineKeyboardMarkup managementButtons() {
        return InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Project manager", "Project manager")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Sales manager", "Sales manager")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Marketolog", "Marketolog")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("HR manager", "HR manager"))));
    }

    public static InlineKeyboardMarkup economicsButtons() {
        return InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Bugalter", "Bugalter"))));
    }

    public static InlineKeyboardMarkup editingVacancyButtons() {
        return InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83C\uDFE2 Ish beruvchi", "employerName")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDDFA Manzil", "address")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDCCB Yo'nalish", "specialty")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBC Lavozim", "position")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDCB0 Maosh", "salary")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDD5E Haftalik ish soati", "workTime")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDCF1 Aloqa", "callAddress")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("‼\uFE0F Qo'shimcha", "extraInfo"))));

    }

    public static InlineKeyboardMarkup editingResumeButtons() {
        return InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDC64 Ism", "employeeName")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDDFA Manzil", "address")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDCCB Yo'nalish", "specialty")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("❇\uFE0F Texnologiya va kasbiy ko'nikmalar", "technologies")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDCB0 Maosh", "salary")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDD5E Haftalik ish soati", "workTime")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("\uD83D\uDCF1 Aloqa", "callAddress")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("‼\uFE0F Qo'shimcha", "extraInfo"))));
    }

    public static InlineKeyboardMarkup searchButtons() {
        return InlineKeyboardButtonUtil.keyboard(InlineKeyboardButtonUtil.collection(InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Yo'nalish", "search1")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Yo'nalish, Viloyat", "search2")), InlineKeyboardButtonUtil.row(InlineKeyboardButtonUtil.button("Yo'nalish, Viloyat, Tuman", "search3"))));
    }
}
