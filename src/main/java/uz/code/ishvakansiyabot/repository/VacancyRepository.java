package uz.code.ishvakansiyabot.repository;

import org.springframework.data.repository.CrudRepository;
import uz.code.ishvakansiyabot.entity.VacancyEntity;

import java.util.Optional;

public interface VacancyRepository extends CrudRepository<VacancyEntity,Integer> {
    Optional<VacancyEntity> findById(Integer id);

}
