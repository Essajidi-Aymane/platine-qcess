package univ.lille.infrastructure.adapter.notification;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import univ.lille.domain.port.out.AccessLogNotificationPort;
import univ.lille.dto.access.AccessLogResponseDTO;

@Component
public class SseNotificationAdapter implements AccessLogNotificationPort {

    private final Map<Long, List<SseEmitter>> organizationEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long organizationId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        organizationEmitters.computeIfAbsent(organizationId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        Runnable removeEmitter = () -> {
            List<SseEmitter> emitters = organizationEmitters.get(organizationId);
            if (emitters != null) {
                emitters.remove(emitter);
                if (emitters.isEmpty()) {
                    organizationEmitters.remove(organizationId);
                }
            }
        };

        emitter.onCompletion(removeEmitter);
        emitter.onTimeout(removeEmitter);
        emitter.onError((e) -> removeEmitter.run());

        return emitter;
    }

   @Override
    public void notifyAdmins(Long organizationId, AccessLogResponseDTO logDto) {
        // On appelle votre méthode interne qui filtre par ID
        sendToOrganization(organizationId, logDto);
    }

    // Méthode interne (à garder dans la classe)
    private void sendToOrganization(Long organizationId, AccessLogResponseDTO logDto) {
        List<SseEmitter> emitters = organizationEmitters.get(organizationId);
        if (emitters != null) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().name("access-log").data(logDto));
                } catch (IOException e) {
                    emitters.remove(emitter);
                }
            }
        }
    }
 }
    
   

