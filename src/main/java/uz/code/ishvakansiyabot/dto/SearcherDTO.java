package uz.code.ishvakansiyabot.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import uz.code.ishvakansiyabot.enums.SearchMethodType;
import uz.code.ishvakansiyabot.enums.SearchPostType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SearcherDTO {
    private Long userId;
    private Integer currentVacancyPageIndex = 0;
    private Integer currentResumePageIndex = 0;
    private String specialty1;
    private String specialty2;
    private String region;
    private String distinct;
    private SearchPostType searchPostType;
    private SearchMethodType searchMethodType;
}