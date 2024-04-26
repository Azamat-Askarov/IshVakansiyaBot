package uz.code.ishvakansiyabot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.code.ishvakansiyabot.enums.GeneralStatus;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "resume")
public class ResumeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private Long employeeId;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private GeneralStatus status = GeneralStatus.ACTIVE;
    private String createdDate;
    @Column(columnDefinition = "text")
    private String employeeName;
    private String specialty1;
    private String specialty2;
    @Column(columnDefinition = "text")
    private String technologies;
    private String salary;
    private String workRegion; // viloyat
    private String workDistinct; // tuman
    private String connectAddress;
    @Column(columnDefinition = "text")
    private String extraInfo;
}
