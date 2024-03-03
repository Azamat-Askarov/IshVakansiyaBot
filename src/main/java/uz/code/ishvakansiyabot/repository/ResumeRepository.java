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
    List<ResumeEntity> findAllByStatus(GeneralStatus generalStatus);

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
    @Query("update ResumeEntity set status =?2 where employeeId =?1 ")
    void changeResumesStatusByEmployeeId(Long id, GeneralStatus status);

    @Transactional
    @Modifying
    @Query("update ResumeEntity set status =?2 where id =?1 ")
    void changeResumeStatus(Integer id, GeneralStatus status);

    @Transactional
    @Modifying
    @Query("select employeeId from ResumeEntity where workRegion =?1 and specialty2 =?2 and status =?3")
    List<Long> getEmployeeIdByWorkRegionAndSpecialty2AndStatus(String region, String specialty2, GeneralStatus generalStatus);

    @Query("select count(r)from ResumeEntity r")
    Integer countAllResumes();

    @Query("SELECT COUNT(r) FROM ResumeEntity r WHERE r.status = 'ACTIVE' ")
    Integer countActiveResumes();

    @Query("SELECT COUNT(r) FROM ResumeEntity r WHERE r.status = 'DELETED' ")
    Integer countDeletedResumes();

}
