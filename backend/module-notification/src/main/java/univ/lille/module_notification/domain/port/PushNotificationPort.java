package univ.lille.module_notification.domain.port;

import java.util.Map;

public interface PushNotificationPort {
    void sendPushToToken(String fcmToken, String title, String body);
    
    void sendPushToToken(String fcmToken, String title, String body, Map<String, String> data);
}
