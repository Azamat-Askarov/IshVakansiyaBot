package uz.code.ishvakansiyabot.dto;

import lombok.Data;
import uz.code.ishvakansiyabot.enums.GeneralStatus;

import java.time.LocalDateTime;

@Data
public class VacancyDTO {
    private Integer id;
    private Long employerId;
    private GeneralStatus status;
    private String createdDate;
    private String employerName;
    private String specialty1;
    private String specialty2;
    private String position;
    private String salary;
    private String workRegion; // viloyat
    private String workDistinct; // tuman
    private String connectAddress;
    private String workTime;
    private String extraInfo;
}
