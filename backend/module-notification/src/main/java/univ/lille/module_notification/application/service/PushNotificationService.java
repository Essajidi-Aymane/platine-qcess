package univ.lille.module_notification.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import univ.lille.module_notification.domain.port.DeviceTokenServicePort;
import univ.lille.module_notification.domain.port.PushNotificationPort;
import univ.lille.module_notification.domain.port.PushNotificationServicePort;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService implements PushNotificationServicePort {

    private final PushNotificationPort pushNotificationPort;
    private final DeviceTokenServicePort deviceTokenService;

    @Override
    public void sendToToken(String fcmToken, String title, String body, Map<String, String> data) {
        pushNotificationPort.sendPushToToken(fcmToken, title, body, data);
    }

    @Override
    public void sendPushToUser(Long userId, String title, String body, Map<String, String> data) {
        List<String> tokens = deviceTokenService.getTokensByUserId(userId);
        if (tokens.isEmpty()) {
            log.warn("No FCM tokens found for user {}", userId);
            return;
        }
        for (String token : tokens) {
            pushNotificationPort.sendPushToToken(token, title, body, data);
        }
        log.info("Push notification sent to {} device(s) for user {}", tokens.size(), userId);
    }

    @Override
    public void sendToOrganization(Long organizationId, String title, String body, Map<String, String> data) {
        List<String> tokens = deviceTokenService.getTokensByOrganizationId(organizationId);
        if (tokens.isEmpty()) {
            log.warn("No FCM tokens found for organization {}", organizationId);
            return;
        }
        for (String token : tokens) {
            pushNotificationPort.sendPushToToken(token, title, body, data);
        }
        log.info("Broadcast notification sent to {} device(s) for organization {}", tokens.size(), organizationId);
    }
}
