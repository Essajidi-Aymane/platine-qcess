package univ.lille.infrastructure.adapter.out.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import univ.lille.domain.port.out.PushNotificationRepositoryPort;


@Slf4j
@Component
public class FirebasePushNotificationAdapter implements PushNotificationRepositoryPort {

    @Override
    public void sendPushToToken(String fcmToken, String title, String body) {
        try {
            Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build()
                )
                .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Push notification sent successfully. Response: {}", response);
            
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send push notification to token: {}. Error: {}", 
                    fcmToken.substring(0, Math.min(20, fcmToken.length())) + "...", 
                    e.getMessage());
        } catch (IllegalStateException e) {
            log.error("Firebase is not initialized. Cannot send push notification. Error: {}", e.getMessage());
        }
    }
}
