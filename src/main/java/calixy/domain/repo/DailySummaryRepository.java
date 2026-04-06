package calixy.domain.repo;

import calixy.domain.entity.DailySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailySummaryRepository extends JpaRepository<DailySummary, Long> {

    Optional<DailySummary> findByUserIdAndDate(Long userId, LocalDate date);

    List<DailySummary> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate from, LocalDate to);
}