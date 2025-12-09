package univ.lille.module_notification.infrastructure.adapter.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import univ.lille.events.NotificationEvent;
import univ.lille.module_notification.domain.port.in.PushNotificationServicePort;


@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    
    private final PushNotificationServicePort pushNotificationService;

    @Async
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("Received notification event: type={}, targetUser={}, targetOrg={}, title={}", 
            event.type(), event.targetUserId(), event.organizationId(), event.title());
        
        String type = event.type() != null ? event.type().name() : "UNKNOWN";
        
        if (event.targetUserId() != null) {
            pushNotificationService.sendPushToUser(event.targetUserId(), event.title(), event.body(), type, event.data());
        } else if (event.organizationId() != null) {
            pushNotificationService.sendToOrganization(event.organizationId(), event.title(), event.body(), type, event.data());
        } else {
            log.warn("Notification event has no target (user or organization), skipping");
        }
    }
}