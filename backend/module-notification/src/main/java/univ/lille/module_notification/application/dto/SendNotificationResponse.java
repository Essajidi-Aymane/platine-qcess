package univ.lille.module_notification.application.dto;

public record SendNotificationResponse(
    String message,
    boolean success
) {
    public static SendNotificationResponse success(String message) {
        return new SendNotificationResponse(message, true);
    }

    public static SendNotificationResponse failure(String message) {
        return new SendNotificationResponse(message, false);
    }
}
