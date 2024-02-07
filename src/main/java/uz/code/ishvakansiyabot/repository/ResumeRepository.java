package uz.code.ishvakansiyabot.repository;

import org.springframework.data.repository.CrudRepository;
import uz.code.ishvakansiyabot.entity.ResumeEntity;

import java.util.Optional;

public interface ResumeRepository extends CrudRepository<ResumeEntity, Integer> {
    Optional<ResumeEntity> findById(Integer id);
}
