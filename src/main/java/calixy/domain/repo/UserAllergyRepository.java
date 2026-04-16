package calixy.domain.repo;

import calixy.domain.entity.UserAllergy;
import calixy.model.enums.AllergyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAllergyRepository extends JpaRepository<UserAllergy, Long> {

    List<UserAllergy> findByUserId(Long userId);

    boolean existsByUserIdAndAllergy(Long userId, AllergyType allergy);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM UserAllergy ua WHERE ua.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}