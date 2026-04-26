package calixy.controller;

import calixy.domain.entity.User;
import calixy.model.dto.request.FCMTokenRequest;
import calixy.model.dto.request.NotificationRequest;
import calixy.model.dto.response.MessageResponse;
import calixy.model.dto.response.NotificationResponse;
import calixy.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<NotificationResponse> create(
            @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.getMyNotifications(user.getId()));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnread(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(user.getId()));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(user.getId())));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(user.getId(), id));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<MessageResponse> markAllAsRead(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.markAllAsRead(user.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.deleteNotification(user.getId(), id));
    }

    @DeleteMapping
    public ResponseEntity<MessageResponse> deleteAll(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.deleteAllNotification(user.getId()));
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<MessageResponse> saveFcmToken(
            @AuthenticationPrincipal User user,
            @RequestBody FCMTokenRequest request) {
        return ResponseEntity.ok(notificationService.saveFCMToken(user.getId(), request.getFcmToken()));
    }

    @DeleteMapping("/fcm-token")
    public ResponseEntity<MessageResponse> removeFcmToken(
            @AuthenticationPrincipal User user,
            @RequestBody FCMTokenRequest request) {
        return ResponseEntity.ok(notificationService.removeFCMToken(user.getId(), request.getFcmToken()));
    }
}