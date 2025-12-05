package univ.lille.infrastructure.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import univ.lille.application.dto.RegisterTokenRequest;
import univ.lille.application.service.AuthenticationService;
import univ.lille.domain.port.in.DeviceTokenServicePort;

@Slf4j
@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceTokenController {

    private final DeviceTokenServicePort deviceTokenService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register-token")
    public ResponseEntity<Void> registerToken(@Valid @RequestBody RegisterTokenRequest request) {
        Long userId = authenticationService.getCurrentUserId();
        Long organizationId = authenticationService.getCurrentUserOrganizationId();
        
        log.info("Registering FCM token for user {} (org: {})", userId, organizationId);
        
        deviceTokenService.registerToken(
            userId.intValue(), 
            organizationId != null ? organizationId.intValue() : null, 
            request.fcmToken()
        );
        
        log.debug("FCM token registered successfully for user {}", userId);
        return ResponseEntity.ok().build();
    }
}
