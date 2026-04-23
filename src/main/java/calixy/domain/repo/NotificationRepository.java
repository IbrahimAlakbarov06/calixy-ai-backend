package calixy.domain.repo;

import calixy.domain.entity.Notification;
import calixy.model.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, NotificationStatus status);

    Long countByUserIdAndStatus(Long userId, NotificationStatus status);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}