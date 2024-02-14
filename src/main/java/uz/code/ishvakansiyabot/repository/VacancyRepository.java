package uz.code.ishvakansiyabot.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.code.ishvakansiyabot.entity.VacancyEntity;
import uz.code.ishvakansiyabot.enums.GeneralStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacancyRepository extends CrudRepository<VacancyEntity, Integer> {
    Optional<VacancyEntity> findById(Integer id);

    List<VacancyEntity> findByEmployerIdAndStatus(Long id, GeneralStatus generalStatus);

    List<VacancyEntity> findBySpecialty2AndStatus(String specialty2, GeneralStatus generalStatus);


    List<VacancyEntity> findByWorkRegionAndSpecialty2AndStatus(String workRegion, String specialty2, GeneralStatus generalStatus);

    List<VacancyEntity> findByWorkDistinctAndSpecialty2AndStatus(String workDistinct, String specialty2, GeneralStatus generalStatus);

    @Transactional
    @Modifying
    @Query("update VacancyEntity set status =?2 where id =?1 ")
    void changeVacancyStatus(Integer id, GeneralStatus status);

    @Transactional
    @Modifying
    @Query("select employerId from VacancyEntity where workRegion =?1 and specialty2 =?2 and status =?3")
    List<Long> getEmployerIdByWorkRegionAndSpecialty2AndStatus(String region, String specialty2, GeneralStatus generalStatus);
}
