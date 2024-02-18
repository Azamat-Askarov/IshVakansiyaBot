package uz.code.ishvakansiyabot.dto;

import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Data
public class SendMsgDTO {
    private EditMessageText editText;
    private SendMessage text;
    private SendPhoto photo;
    private SendVideo video;
    private SendAudio audio;
}
