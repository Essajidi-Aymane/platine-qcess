package univ.lille.domain.port.in;

import univ.lille.domain.model.AccessLog;
import univ.lille.dto.access.AccessLogResponseDTO;
import univ.lille.dto.access.AccessResponseDTO;
import java.util.List;

public interface AccessControlPort {

    AccessResponseDTO validateAccess(Long userId , Long zoneId) ; 

    List<AccessLogResponseDTO> getAccessLogs(Long organizationId); 

    List<AccessLog> findByUserIdOrderByTimestampDesc(Long userId, int limit);

    
}
