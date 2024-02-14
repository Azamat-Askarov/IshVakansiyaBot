package uz.code.ishvakansiyabot.dto;

import lombok.Data;
import uz.code.ishvakansiyabot.enums.UserRole;
import uz.code.ishvakansiyabot.enums.GeneralStatus;
import uz.code.ishvakansiyabot.enums.UserStep;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Integer botId;
    private Long tgId;
    private String createdDate;
    private UserRole role;
    private GeneralStatus status;
    private UserStep step;
    private String name;
    private Byte age;
    private String address;
    private Double balance;
}
