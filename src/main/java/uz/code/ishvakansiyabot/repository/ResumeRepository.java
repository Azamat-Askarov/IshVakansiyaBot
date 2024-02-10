package uz.code.ishvakansiyabot.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
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

    @Transactional
    @Modifying
    @Query("update ResumeEntity set status =?2 where id =?1 ")
    int changeResumeStatus(Integer id, GeneralStatus status);
}
