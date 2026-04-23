package calixy.service;

import calixy.domain.entity.Notification;
import calixy.domain.entity.User;
import calixy.domain.repo.NotificationRepository;
import calixy.domain.repo.UserRepository;
import calixy.exception.NotFoundException;
import calixy.mapper.NotificationMapper;
import calixy.model.dto.request.NotificationRequest;
import calixy.model.dto.response.MessageResponse;
import calixy.model.dto.response.NotificationResponse;
import calixy.model.enums.NotificationStatus;
import calixy.model.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Transactional
    @CacheEvict(value = "notifications", key = "#request.userId")
    public NotificationResponse createNotification(NotificationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + request.getUserId()));

        Notification notification = notificationMapper.toEntity(request, user);
        return notificationMapper.toResponse(notificationRepository.save(notification));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "notifications", key = "#userId")
    public List<NotificationResponse> getMyNotifications(Long userId) {
        return notificationMapper.toResponseList(
                notificationRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "notifications", key = "'unread-' + #userId")
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        return notificationMapper.toResponseList(
                notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, NotificationStatus.UNREAD));
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.UNREAD);
    }

    @Transactional
    @CacheEvict(value = "notifications", key = "#userId")
    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        Notification notification = findByIdAndUserId(notificationId, userId);

        if (notification.getStatus() == NotificationStatus.UNREAD) {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }

        return notificationMapper.toResponse(notification);
    }

    @Transactional
    @CacheEvict(value = "notifications", key = "#userId")
    public MessageResponse markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(userId, NotificationStatus.UNREAD);

        LocalDateTime now = LocalDateTime.now();
        for (Notification n : unread) {
            n.setStatus(NotificationStatus.READ);
            n.setReadAt(now);
        }
        notificationRepository.saveAll(unread);

        return new MessageResponse("All notifications marked as read");
    }

    @Transactional
    @CacheEvict(value = "notifications", key = "#userId")
    public MessageResponse deleteNotification(Long userId, Long notificationId) {
        notificationRepository.delete(findByIdAndUserId(notificationId, userId));
        return new MessageResponse("Notification deleted successfully");
    }

    @Transactional
    @CacheEvict(value = "notifications", key = "#userId")
    public MessageResponse deleteAllNotification(Long userId) {
        notificationRepository.deleteByUserId(userId);
        return new MessageResponse("All notifications deleted successfully");
    }

    @Transactional
    public MessageResponse saveFCMToken(Long userId, String fcmToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        user.setFcmToken(fcmToken);
        userRepository.save(user);

        return new MessageResponse("FCM token registered successfully");
    }

    @Transactional
    public MessageResponse removeFCMToken(Long userId, String fcmToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        if (fcmToken.equals(user.getFcmToken())) {
            user.setFcmToken(null);
            userRepository.save(user);
        }

        return new MessageResponse("FCM token removed successfully");
    }

    @Transactional
    @CacheEvict(value = "notifications", key = "#user.id")
    public void send(User user, String title, String message, NotificationType type) {
        notificationRepository.save(Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .status(NotificationStatus.UNREAD)
                .build());
    }

    private Notification findByIdAndUserId(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new NotFoundException("Notification not found");
        }

        return notification;
    }
}