package uz.code.ishvakansiyabot.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import uz.code.ishvakansiyabot.entity.UserEntity;
import uz.code.ishvakansiyabot.entity.VacancyEntity;
import uz.code.ishvakansiyabot.enums.GeneralStatus;

import java.util.List;
import java.util.Optional;

public interface VacancyRepository extends CrudRepository<VacancyEntity, Integer> {
    Optional<VacancyEntity> findById(Integer id);
    List<VacancyEntity> findByEmployerIdAndStatus(Long id, GeneralStatus generalStatus);
}
