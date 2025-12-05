package univ.lille.infrastructure.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import univ.lille.application.dto.SendNotificationRequest;
import univ.lille.application.dto.SendNotificationResponse;
import univ.lille.application.dto.SendToTokenRequest;
import univ.lille.domain.port.in.PushNotificationServicePort;


@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class PushNotificationController {

    private final PushNotificationServicePort pushNotificationService;

    @PostMapping("/send")
    public ResponseEntity<SendNotificationResponse> sendNotification(
            @Valid @RequestBody SendNotificationRequest request) {
        
        log.info("Sending push notification to user {}: {}", request.userId(), request.title());
        
        pushNotificationService.sendPushToUser(request.userId(), request.title(), request.body());
        
        return ResponseEntity.ok(SendNotificationResponse.success("Notification envoyée avec succès"));
    }

    @PostMapping("/send-to-token")
    public ResponseEntity<SendNotificationResponse> sendToToken(
            @Valid @RequestBody SendToTokenRequest request) {
        
        log.info("Sending push notification to token: {}", request.title());
        
        pushNotificationService.sendPushToToken(request.fcmToken(), request.title(), request.body());
        
        return ResponseEntity.ok(SendNotificationResponse.success("Notification envoyée avec succès"));
    }
}
