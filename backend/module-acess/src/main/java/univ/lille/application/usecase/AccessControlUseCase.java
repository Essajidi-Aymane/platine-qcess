package univ.lille.application.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import univ.lille.domain.exception.UserNotFoundException;
import univ.lille.domain.exception.ZoneNotFoundException;
import univ.lille.domain.model.AccessLog;
import univ.lille.domain.model.User;
import univ.lille.domain.model.Zone;
import univ.lille.domain.port.in.AccessControlPort;
import univ.lille.domain.port.out.AccessLogRepository;
import univ.lille.domain.port.out.UserRepository;
import univ.lille.domain.port.out.ZoneRepository;
import univ.lille.dto.access.AccessResponseDTO;
import univ.lille.enums.ZoneStatus;

@Service 
@RequiredArgsConstructor
public class AccessControlUseCase implements AccessControlPort {

    private final ZoneRepository zoneRepository ; 
    private final UserRepository userRepository ; 
    private final AccessLogRepository accessLogRepository ; 
    @Override
    @Transactional
    public AccessResponseDTO validateAccess(Long userId, Long zoneId) {

        User user = userRepository.findById(userId).orElseThrow(()-> 
        new UserNotFoundException("User Not found ")) ; 

        Zone zone = zoneRepository.findById(zoneId).orElseThrow(()-> 
        new ZoneNotFoundException("Zone not found")); 
        String reason = ""; 
        boolean granted = false; 
        if (zone.getStatus()!= ZoneStatus.ACTIVE) {
            reason = "ZONE_INACTIVE"; 
        } else if (zone.getAllowedRoleIds() == null || zone.getAllowedRoleIds().isEmpty()) { 
            granted=true; 
            reason = "PUBLIC_ZONE" ; 
        } else if ( zone.getAllowedRoleIds() != null && zone.getAllowedRoleIds().contains(user.getCustomRole().getId())) { 
            granted = true; 
            reason = "AUTHORIZED_ROLE" ; 

        } else { 
            reason = "ROLE_NOT_ALLOWED" ; 
        }

        AccessLog log = AccessLog.builder()
                .userId(userId)
                .zoneId(zoneId)
                .organizationId(zone.getOrgId())
                .timestamp(LocalDateTime.now())
                .accessGranted(granted)
                .reason(reason)
                .build(); 
        accessLogRepository.save(log);
        return new AccessResponseDTO(granted , reason , zone.getName()); 

    }
    
}
