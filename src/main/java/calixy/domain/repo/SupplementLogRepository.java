package calixy.domain.repo;

import calixy.domain.entity.SupplementLog;
import calixy.model.enums.SupplementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SupplementLogRepository extends JpaRepository<SupplementLog, Long> {

    List<SupplementLog> findByUserSupplementUserIdAndDate(Long userId, LocalDate date);

    List<SupplementLog> findByUserSupplementIdAndDateBetweenOrderByDateDesc(
            Long userSupplementId, LocalDate from, LocalDate to);

    boolean existsByUserSupplementIdAndDate(Long userSupplementId, LocalDate date);
}