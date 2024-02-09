package uz.code.ishvakansiyabot.repository;

import org.springframework.data.repository.CrudRepository;
import uz.code.ishvakansiyabot.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findByTgId(Long id);
}
