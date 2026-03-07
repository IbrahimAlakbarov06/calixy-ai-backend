package calixy.domain.repo;

import calixy.domain.entity.UserGoal;
import calixy.model.enums.Goal;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {
    List<UserGoal> findByUserId(Long userId);

    boolean existsByUserIdAndGoal(Long userId, Goal goal);

    @Modifying
    @Query("DELETE FROM UserGoal ug WHERE ug.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
