package calixy.domain.repo;

import calixy.domain.entity.FavoriteFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteFood, Long> {

    List<FavoriteFood> findByUserId(Long userId);

    Optional<FavoriteFood> findByUserIdAndFoodId(Long userId, Long foodId);

    Optional<FavoriteFood> findByUserIdAndDietPlanId(Long userId, Long dietPlanId);

    boolean existsByUserIdAndFoodId(Long userId, Long foodId);

    boolean existsByUserIdAndDietPlanId(Long userId, Long dietPlanId);
}