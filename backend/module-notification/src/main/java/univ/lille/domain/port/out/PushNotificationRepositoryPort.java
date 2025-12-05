package univ.lille.domain.port.out;

public interface PushNotificationRepositoryPort {
    void sendPushToToken(String fcmToken, String title, String body);
}