package uz.code.ishvakansiyabot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.code.ishvakansiyabot.enums.UserRole;
import uz.code.ishvakansiyabot.enums.GeneralStatus;
import uz.code.ishvakansiyabot.enums.UserStep;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "bot_user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer botId;
    @NotNull
    private Long tgId;
    private String createdDate;
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;
    @Enumerated(EnumType.STRING)
    private GeneralStatus status = GeneralStatus.NEW_USER;
    @Enumerated(EnumType.STRING)
    private UserStep step = UserStep.CREATING;
    private String name;
    private Byte age;
    private String address;
    private Double balance = 0.0;
}
