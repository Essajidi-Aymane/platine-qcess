package univ.lille.events;

import lombok.Value;

@Value
public class ZoneCreatedEvent {

    Long zoneId ;
    Long organizationId;
    String name ;
   // String type ;


}
