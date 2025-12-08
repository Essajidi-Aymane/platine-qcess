package univ.lille.domain.port.in;

import univ.lille.dto.access.AccessResponseDTO;

public interface AccessControlPort {

    AccessResponseDTO validateAccess(Long userId , Long zoneId) ; 
    
}
