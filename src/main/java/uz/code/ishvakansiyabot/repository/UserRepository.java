package uz.code.ishvakansiyabot.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import uz.code.ishvakansiyabot.entity.UserEntity;
import uz.code.ishvakansiyabot.enums.GeneralStatus;
import uz.code.ishvakansiyabot.enums.UserStep;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findByTgId(Long id);

    @Transactional
    @Modifying
    @Query("update UserEntity set step =?2 where tgId =?1")
    void changeUserStep(Long id, UserStep step);
}
