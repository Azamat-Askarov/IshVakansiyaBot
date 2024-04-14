package uz.code.ishvakansiyabot.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import uz.code.ishvakansiyabot.entity.UserEntity;
import uz.code.ishvakansiyabot.enums.GeneralStatus;
import uz.code.ishvakansiyabot.enums.UserRole;
import uz.code.ishvakansiyabot.enums.UserStep;

public interface UserRepository extends CrudRepository<UserEntity, Long>  {
    UserEntity findByTgId(Long id);
    @Transactional
    @Modifying
    @Query("update UserEntity set status =?2 where tgId =?1 ")
    void changeUserStatus(Long id, GeneralStatus status);

    @Transactional
    @Modifying
    @Query("update UserEntity set step =?2 where tgId =?1")
    void changeUserStep(Long id, UserStep step);
    @Transactional
    @Modifying
    @Query("update UserEntity set name =?2 where tgId =?1")
    void changeUserName(Long id, String name);
    @Transactional
    @Modifying
    @Query("update UserEntity set age =?2 where tgId =?1")
    void changeUserAge(Long id, String age);
    @Transactional
    @Modifying
    @Query("update UserEntity set address =?2 where tgId =?1")
    void changeUserAddress(Long id, String address);
    @Query("select count(tg_id)from UserEntity tg_id")
    Integer countAllUsers();

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.status = 'ACTIVE' ")
    Integer countActiveUsers();

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.status != 'ACTIVE' ")
    Integer countDeletedUsers();

    @Query("SELECT COUNT(u) FROM VacancyEntity u WHERE u.status = 'ACTIVE' AND  u.employerId = :tg_id")
    Integer countAllVacanciesFromUser(Long tg_id);

    @Query("SELECT COUNT(u) FROM ResumeEntity u WHERE u.status = 'ACTIVE' AND  u.employeeId = :tg_id")
    Integer countAllResumesFromUser(Long tg_id);



}
