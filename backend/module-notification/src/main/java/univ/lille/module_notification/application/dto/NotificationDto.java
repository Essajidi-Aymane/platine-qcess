package univ.lille.module_notification.application.dto;

import java.time.LocalDateTime;
import java.util.Map;

import univ.lille.module_notification.domain.model.Notification;

public record NotificationDto(
    Long id,
    String title,
    String body,
    String type,
    boolean read,
    LocalDateTime createdAt,
    LocalDateTime readAt,
    Map<String, String> data

) {
    public static NotificationDto from(Notification notification) {
        Map<String, String> data = null;
        if (notification.getType() != null && notification.getType().toUpperCase().startsWith("TICKET")) {
            data = Map.of("feature", "maintenance");
        }
        return new NotificationDto(
            notification.getId(),
            notification.getTitle(),
            notification.getBody(),
            notification.getType(),
            notification.isRead(),
            notification.getCreatedAt(),
            notification.getReadAt(),
            data
        );
    }
}