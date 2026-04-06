package calixy.domain.repo;

import calixy.domain.entity.MealLog;
import calixy.model.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealLogRepository extends JpaRepository<MealLog, Long> {

    List<MealLog> findByUserIdAndDateOrderByLoggedAtDesc(Long userId, LocalDate date);

    List<MealLog> findByUserIdAndDateAndMealType(Long userId, LocalDate date, MealType mealType);

    List<MealLog> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate from, LocalDate to);

    @Query("SELECT COALESCE(SUM(m.calories), 0) FROM MealLog m WHERE m.user.id = :userId AND m.date = :date")
    Double sumCaloriesByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(m.protein), 0) FROM MealLog m WHERE m.user.id = :userId AND m.date = :date")
    Double sumProteinByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(m.carbs), 0) FROM MealLog m WHERE m.user.id = :userId AND m.date = :date")
    Double sumCarbsByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(m.fat), 0) FROM MealLog m WHERE m.user.id = :userId AND m.date = :date")
    Double sumFatByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}