package univ.lille.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccessLog {
    Long id; 
    Long userId; 
    Long zoneId ; 
    Long organizationId; 
    LocalDateTime timestamp; 
    boolean accessGranted ; 
    String reason; // "AUTHORIZED", "ROLE_NOT_ALLOWED", "ZONE_INACTIVE", "PUBLIC_ZONE"
}
