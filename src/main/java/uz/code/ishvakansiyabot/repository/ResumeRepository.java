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

    List<ResumeEntity> findBySpecialty2AndStatus(String specialty2, GeneralStatus generalStatus);

    List<ResumeEntity> findByWorkRegionAndSpecialty2AndStatus(String workRegion, String specialty2, GeneralStatus generalStatus);

    List<ResumeEntity> findByWorkDistinctAndSpecialty2AndStatus(String workDistinct, String specialty2, GeneralStatus generalStatus);

    @Transactional
    @Modifying
    @Query("update ResumeEntity set status =?2 where id =?1 ")
    void changeStatus(Integer id, GeneralStatus status);

    @Transactional
    @Modifying
    @Query("select employeeId from ResumeEntity where workRegion =?1 and specialty2 =?2 and status =?3")
    List<Long> getEmployeeIdByWorkRegionAndSpecialty2AndStatus(String region, String specialty2, GeneralStatus generalStatus);
}
