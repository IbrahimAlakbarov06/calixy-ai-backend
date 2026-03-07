package calixy.domain.repo;

import calixy.domain.entity.User;
import calixy.model.enums.UserRole;
import calixy.model.enums.UserStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);

    List<User> findByStatus(UserStatus status);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.email = :email")
    Optional<User> findByEmailWithProfile(@Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile LEFT JOIN FETCH u.goals WHERE u.id = :id")
    Optional<User> findByIdWithProfileAndGoals(@Param("id") Long id);

}
