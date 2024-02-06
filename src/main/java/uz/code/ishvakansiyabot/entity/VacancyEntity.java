package uz.code.ishvakansiyabot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.code.ishvakansiyabot.enums.GeneralStatus;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vacancy")
public class VacancyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private Long employerId;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private GeneralStatus status = GeneralStatus.ACTIVE;
    private LocalDateTime createdDate = LocalDateTime.now();
    @Column(columnDefinition = "text")
    private String employerName;
    private String specialty1;
    private String specialty2;
    @Column(columnDefinition = "text")
    private String position;
    private String salary;
    private String workRegion; // viloyat
    private String workDistinct; // tuman
    private String workTime;
    private String connectAddress;
    @Column(columnDefinition = "text")
    private String extraInfo;
}
