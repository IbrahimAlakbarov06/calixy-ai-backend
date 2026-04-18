package calixy.domain.repo;

import calixy.domain.entity.Supplement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplementRepository extends JpaRepository<Supplement, Long> {

    List<Supplement> findByIsActiveTrue();

    List<Supplement> findByIsCustomFalseAndIsActiveTrue();
}