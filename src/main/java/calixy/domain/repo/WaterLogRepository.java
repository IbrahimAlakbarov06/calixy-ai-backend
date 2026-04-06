package calixy.domain.repo;

import calixy.domain.entity.WaterLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WaterLogRepository extends JpaRepository<WaterLog, Long> {

    List<WaterLog> findByUserIdAndDateOrderByLoggedAtDesc(Long userId, LocalDate date);

    List<WaterLog> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate from, LocalDate to);

    @Query("SELECT COALESCE(SUM(w.amountMl), 0) FROM WaterLog w WHERE w.user.id = :userId AND w.date = :date")
    Integer sumAmountByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}