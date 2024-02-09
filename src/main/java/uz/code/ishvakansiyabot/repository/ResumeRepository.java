package uz.code.ishvakansiyabot.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import uz.code.ishvakansiyabot.entity.ResumeEntity;
import uz.code.ishvakansiyabot.entity.VacancyEntity;
import uz.code.ishvakansiyabot.enums.GeneralStatus;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends CrudRepository<ResumeEntity, Integer> {
    Optional<ResumeEntity> findById(Integer id);

    List<ResumeEntity> findByEmployeeIdAndStatus(Long id, GeneralStatus generalStatus);
}
