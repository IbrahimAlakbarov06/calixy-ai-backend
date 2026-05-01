package calixy.domain.repo;

import calixy.domain.entity.UserSupplement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSupplementRepository extends JpaRepository<UserSupplement, Long> {

    List<UserSupplement> findByUserIdAndIsActiveTrue(Long userId);

    boolean existsByUserIdAndSupplementIdAndIsActiveTrue(Long userId, Long supplementId);
}