package univ.lille.domain.port.out;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Map;

public interface NotificationPort {
    
    SseEmitter subscribe(Long organizationId);

    void notifyResourceUpdate(Long organizationId, String resourceType, Long resourceId, Map<String, Object> payload);

    void notifyEvent(Long organizationId, String eventName, Object data);
}