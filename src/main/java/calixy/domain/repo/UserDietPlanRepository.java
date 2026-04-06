package calixy.domain.repo;

import calixy.domain.entity.UserDietPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDietPlanRepository extends JpaRepository<UserDietPlan, Long> {

    List<UserDietPlan> findByUserIdAndIsActiveTrue(Long userId);

    boolean existsByUserIdAndDietPlanId(Long userId, Long dietPlanId);
}