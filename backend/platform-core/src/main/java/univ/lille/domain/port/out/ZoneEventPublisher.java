package univ.lille.domain.port.out;

import univ.lille.events.ZoneCreatedEvent;

public interface ZoneEventPublisher {
    void publishZoneCreated(ZoneCreatedEvent event);

}
