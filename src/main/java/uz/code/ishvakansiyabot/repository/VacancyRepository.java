package uz.code.ishvakansiyabot.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import uz.code.ishvakansiyabot.entity.VacancyEntity;
import uz.code.ishvakansiyabot.enums.GeneralStatus;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;

public interface VacancyRepository extends CrudRepository<VacancyEntity, Integer> {
    Optional<VacancyEntity> findById(Integer id);

    List<VacancyEntity> findByEmployerIdAndStatus(Long id, GeneralStatus generalStatus);

    @Transactional
    @Modifying
    @Query("update VacancyEntity set status =?2 where id =?1 ")
    int changeVacancyStatus(Integer id, GeneralStatus status);

    @Transactional
    @Modifying
    @Query("select employerId from VacancyEntity where workRegion =?1 and specialty2 =?2 and status =?3")
    List<Long> getEmployerIdByWorkRegionAndSpecialty2AndStatus(String region, String specialty2, GeneralStatus generalStatus);


}
