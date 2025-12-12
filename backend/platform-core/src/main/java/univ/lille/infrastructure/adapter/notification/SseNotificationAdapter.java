package univ.lille.infrastructure.adapter.notification;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import univ.lille.domain.port.out.NotificationPort;
import univ.lille.dto.notification.ResourceEventDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseNotificationAdapter implements NotificationPort {

    private final Map<Long, List<SseEmitter>> organizationEmitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(Long organizationId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        organizationEmitters.computeIfAbsent(organizationId, k -> new ArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(organizationId, emitter));
        emitter.onTimeout(() -> removeEmitter(organizationId, emitter));
        emitter.onError((e) -> removeEmitter(organizationId, emitter));

        return emitter;
    }

    @Override
    public void notifyResourceUpdate(Long organizationId, String resourceType, Long resourceId, Map<String, Object> payload) {
        ResourceEventDTO event = ResourceEventDTO.builder()
                .resourceType(resourceType)
                .resourceId(resourceId)
                .payload(payload)
                .build();
        
        // On réutilise la méthode générique
        notifyEvent(organizationId, "resource-update", event);
    }

    @Override
    public void notifyEvent(Long organizationId, String eventName, Object data) {
        List<SseEmitter> emitters = organizationEmitters.get(organizationId);
        if (emitters != null) {
            new ArrayList<>(emitters).forEach(emitter -> {
                if (emitter != null) {
                    try {
                        emitter.send(SseEmitter.event().name(eventName).data(data));
                    } catch (IOException e) {
                        removeEmitter(organizationId, emitter);
                    }
                }
            });
        }
    }

    private void removeEmitter(Long organizationId, SseEmitter emitter) {
        List<SseEmitter> emitters = organizationEmitters.get(organizationId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                organizationEmitters.remove(organizationId);
            }
        }
    }
}



