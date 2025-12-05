package univ.lille.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import univ.lille.domain.port.in.DeviceTokenServicePort;
import univ.lille.domain.port.in.PushNotificationServicePort;
import univ.lille.domain.port.out.PushNotificationRepositoryPort;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService implements PushNotificationServicePort {

    private final PushNotificationRepositoryPort pushNotificationRepository;
    private final DeviceTokenServicePort deviceTokenService;

    @Override
    public void sendPushToToken(String fcmToken, String title, String body) {
        pushNotificationRepository.sendPushToToken(fcmToken, title, body);
    }

    @Override
    public void sendPushToUser(int userId, String title, String body) {
        List<String> tokens = deviceTokenService.getTokensByUserId(userId);
        if (tokens.isEmpty()) {
            log.warn("No FCM tokens found for user {}", userId);
            return;
        }
        for (String token : tokens) {
            sendPushToToken(token, title, body);
        }
        log.info("Push notification sent to {} device(s) for user {}", tokens.size(), userId);
    }

    @Override
    public void sendPushToOrganization(int organizationId, String title, String body) {
        List<String> tokens = deviceTokenService.getTokensByOrganizationId(organizationId);
        if(tokens.isEmpty()){
            log.warn("No FCM tokens found for organization {}", organizationId);
            return;
        }
        for (String token : tokens) {
            sendPushToToken(token, title, body);
        }
        log.info("Push notification sent to {} device(s) for organization {}", tokens.size(), organizationId);
    }
}