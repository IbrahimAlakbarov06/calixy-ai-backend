package calixy.mapper;

import calixy.domain.entity.Notification;
import calixy.domain.entity.User;
import calixy.model.dto.request.NotificationRequest;
import calixy.model.dto.response.NotificationResponse;
import calixy.model.enums.NotificationStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationMapper {

    public Notification toEntity(NotificationRequest request, User user) {
        return Notification.builder()
                .user(user)
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .status(NotificationStatus.UNREAD)
                .build();
    }

    public NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .status(n.getStatus())
                .readAt(n.getReadAt())
                .createdAt(n.getCreatedAt())
                .build();
    }

    public List<NotificationResponse> toResponseList(List<Notification> list) {
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }
}