package univ.lille.infrastructure.adapter.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter; 

import lombok.RequiredArgsConstructor;
import univ.lille.domain.model.ZoneQrCode;
import univ.lille.domain.port.in.AccessControlPort;
import univ.lille.domain.port.in.ZoneQrCodePort;
import univ.lille.dto.access.AccessLogResponseDTO;
import univ.lille.dto.access.AccessRequestDTO;
import org.springframework.http.MediaType;
import univ.lille.dto.access.AccessResponseDTO;
import univ.lille.infrastructure.adapter.notification.SseNotificationAdapter;
import univ.lille.infrastructure.adapter.security.QcessUserPrincipal;
import java.util.List;

@RestController
@RequestMapping("/api/access")
@RequiredArgsConstructor
public class AccessController {

    private final AccessControlPort accessControlPort; 
    private final ZoneQrCodePort zoneQrCodePort ; 
    private final SseNotificationAdapter sseNotificationAdapter; 


    // Endpoint pour le mobile : Scan QR

    @PostMapping("/scan") 
    public ResponseEntity<AccessResponseDTO> scanQrCode(
        @RequestBody AccessRequestDTO request, 
        @AuthenticationPrincipal QcessUserPrincipal principal
    ) { 
        AccessResponseDTO response = accessControlPort.validateAccess(principal.getId(), request.getZoneId()); 
          
        if (response.isGranted()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(403).body(response);
        }
    }
    // Endpoint pour l'admin : Récupérer le QR d'une zone (image)

    @GetMapping(value = "/zones/{zoneId}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<byte[]> getZoneQrCode(@PathVariable("zoneId") Long zoneId, @AuthenticationPrincipal QcessUserPrincipal principal) { 
        ZoneQrCode qr = zoneQrCodePort.getByZoneId(zoneId, principal.getOrganizationId()); 
        return ResponseEntity.ok(qr.getImage()); 
    }
    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccessLogResponseDTO>> getAccessHistory(@AuthenticationPrincipal QcessUserPrincipal principal) {
        List<AccessLogResponseDTO> logs = accessControlPort.getAccessLogs(principal.getOrganizationId());
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/stream-logs") 
    @PreAuthorize("hasRole('ADMIN')")
    public SseEmitter streamAccessLogs(@AuthenticationPrincipal QcessUserPrincipal principal) { 
        return sseNotificationAdapter.subscribe(principal.getOrganizationId()); 
    }
}
