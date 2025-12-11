package univ.lille.domain.port.out;

import univ.lille.dto.access.AccessLogResponseDTO;

public interface AccessLogNotificationPort {
    void notifyAdmins(Long orgId , AccessLogResponseDTO logDto) ; 
    
}
