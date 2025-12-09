package univ.lille.module_notification.infrastructure.mapper;

import univ.lille.module_notification.domain.model.Notification;
import univ.lille.module_notification.infrastructure.dao.NotificationDao;

public final class NotificationMapper {

    private NotificationMapper() {
    }

    public static Notification toDomain(NotificationDao dao) {
        if (dao == null) return null;
        return Notification.builder()
                .id(dao.getId())
                .userId(dao.getUserId())
                .title(dao.getTitle())
                .body(dao.getBody())
                .type(dao.getType())
                .read(dao.isRead())
                .createdAt(dao.getCreatedAt())
                .readAt(dao.getReadAt())
                .build();
    }

    public static NotificationDao toDao(Notification notification) {
        if (notification == null) return null;
        return NotificationDao.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .body(notification.getBody())
                .type(notification.getType())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}
