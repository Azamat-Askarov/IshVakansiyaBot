package uz.code.ishvakansiyabot.repository;

import uz.code.ishvakansiyabot.dto.ResumeDTO;
import uz.code.ishvakansiyabot.dto.SearcherDTO;
import uz.code.ishvakansiyabot.dto.SendMsgDTO;
import uz.code.ishvakansiyabot.dto.VacancyDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapRepository {
    public static Map<Long, VacancyDTO> currentVacancy = new HashMap<>();
    public static Map<Long, ResumeDTO> currentResume = new HashMap<>();
    public static Map<Long, SearcherDTO> currentSearcherMap = new HashMap<>();
    public static Map<Long, List<VacancyDTO>> searchVacancyResultMap = new HashMap<>();
    public static Map<Long, List<ResumeDTO>> searchResumeResultMap = new HashMap<>();
    public static Map<Long, SendMsgDTO> currentFeedbackMap = new HashMap<>();

}
