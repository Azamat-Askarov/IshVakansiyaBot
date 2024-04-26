package uz.code.ishvakansiyabot.dto;

import lombok.Data;
import uz.code.ishvakansiyabot.enums.GeneralStatus;

import java.time.LocalDateTime;

@Data
public class ResumeDTO {
    private Integer id;
    private Long employeeId;
    private GeneralStatus status;
    private String createdDate;
    private String employeeName;
    private String specialty1;
    private String specialty2;
    private String technologies;
    private String salary;
    private String workRegion; // viloyat
    private String workDistinct; // tuman
    private String connectAddress;
    private String extraInfo;
}
