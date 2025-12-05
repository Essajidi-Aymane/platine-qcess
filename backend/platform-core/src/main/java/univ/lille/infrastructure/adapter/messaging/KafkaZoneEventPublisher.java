package univ.lille.infrastructure.adapter.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import univ.lille.domain.port.out.ZoneEventPublisher;
import univ.lille.events.ZoneCreatedEvent;


@Component
@RequiredArgsConstructor
public class KafkaZoneEventPublisher implements ZoneEventPublisher {
    private final KafkaTemplate<String, ZoneCreatedEvent> kafkaTemplate;

    /**
     * @param event
     */
    @Override
    public void publishZoneCreated(ZoneCreatedEvent event) {
        kafkaTemplate.send("zone-created", event.getZoneId().toString(), event);
    }
}
