package univ.lille.infrastructure.adapter.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import univ.lille.domain.port.in.ZoneQrCodePort;
import univ.lille.events.ZoneCreatedEvent;

@Component 
@RequiredArgsConstructor
@Slf4j
public class ZoneCreatedListener {
    private final ZoneQrCodePort zoneQrCodePort ; 
    
    @KafkaListener(topics = "zone-created", groupId = "access-service-group",
    containerFactory =  "kafkaListenerContainerFactory"
    )
    public void onZoneCreated(@Payload ZoneCreatedEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info(" Received ZoneCreatedEvent: zoneId={}, orgId={}, name={}", 
                 event.getZoneId(), event.getOrganizationId(), event.getName());   
         try {
            zoneQrCodePort.createForZone(event.getZoneId(), event.getOrganizationId(), event.getName());
            log.info("QR Code generated successfully for zone: {}", event.getZoneId());
        } catch (Exception e) {
            log.error("Error generating QR Code for zone: {}", event.getZoneId(), e);
        }
    }
}
