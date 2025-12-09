package univ.lille.domain.model;

import lombok.Builder;
import lombok.Value; 
@Value
@Builder
public class ZoneQrCode {

    Long id ; 
    Long zoneId; 
    Long organizationId; 
    String content; 
    byte[] image; 
    String format; 

}
