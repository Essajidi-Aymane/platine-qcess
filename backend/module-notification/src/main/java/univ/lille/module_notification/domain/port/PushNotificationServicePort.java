package univ.lille.module_notification.domain.port;

import java.util.Map;

public interface PushNotificationServicePort {

    void sendToToken(String fcmToken, String title, String body, Map<String, String> data);

    void sendPushToUser(Long userId, String title, String body, Map<String, String> data);
    
    void sendToOrganization(Long organizationId, String title, String body, Map<String, String> data);
}
