package calixy.domain.repo;

import calixy.domain.entity.DietPlan;
import calixy.model.enums.TargetGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {

    List<DietPlan> findByIsActiveTrue();

    List<DietPlan> findByTargetGoalAndIsActiveTrue(TargetGoal targetGoal);
}