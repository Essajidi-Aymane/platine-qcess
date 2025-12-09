package univ.lille.events;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data 
@NoArgsConstructor
@AllArgsConstructor
public class ZoneCreatedEvent implements Serializable {

    Long zoneId ;
    Long organizationId;
    String name ;
   // String type ;


}
