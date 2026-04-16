package calixy.domain.repo;

import calixy.domain.entity.UserDietaryRule;
import calixy.model.enums.DietaryRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDietaryRuleRepository extends JpaRepository<UserDietaryRule, Long> {

    List<UserDietaryRule> findByUserId(Long userId);

    boolean existsByUserIdAndRule(Long userId, DietaryRule rule);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM UserDietaryRule udr WHERE udr.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}