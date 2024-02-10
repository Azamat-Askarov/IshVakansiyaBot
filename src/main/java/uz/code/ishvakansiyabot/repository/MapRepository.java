package uz.code.ishvakansiyabot.repository;

import org.springframework.stereotype.Component;
import uz.code.ishvakansiyabot.dto.ResumeDTO;
import uz.code.ishvakansiyabot.dto.VacancyDTO;

import java.util.HashMap;
import java.util.Map;

@Component
public class MapRepository {
    public Map<Long, VacancyDTO> currentVacancy = new HashMap<>();
    public Map<Long, ResumeDTO> currentResume = new HashMap<>();
}
