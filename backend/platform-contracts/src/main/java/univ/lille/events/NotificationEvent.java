package univ.lille.events;

import java.util.Map;

import univ.lille.enums.NotificationType;

public record NotificationEvent(
    NotificationType type,
    Long targetUserId,
    Long organizationId,
    String title,
    String body,
    Map<String, String> data
) {
    public static NotificationEvent forUser(Long userId, NotificationType type, String title, String body) {
        return new NotificationEvent(type, userId, null, title, body, Map.of());
    }

    public static NotificationEvent forUser(Long userId, NotificationType type, String title, String body, Map<String, String> data) {
        return new NotificationEvent(type, userId, null, title, body, data);
    }

    public static NotificationEvent forOrganization(Long orgId, NotificationType type, String title, String body) {
        return new NotificationEvent(type, null, orgId, title, body, Map.of());
    }

    public static NotificationEvent forOrganization(Long orgId, NotificationType type, String title, String body, Map<String, String> data) {
        return new NotificationEvent(type, null, orgId, title, body, data);
    }
}
