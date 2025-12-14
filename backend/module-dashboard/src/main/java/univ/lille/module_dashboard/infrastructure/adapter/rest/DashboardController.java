package univ.lille.module_dashboard.infrastructure.adapter.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import univ.lille.dto.DashboardStatsDTO;
import univ.lille.infrastructure.adapter.security.QcessUserPrincipal;
import univ.lille.module_dashboard.domain.port.DashboardPort;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardPort dashboardPort;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats(Authentication authentication) {
        QcessUserPrincipal principal = (QcessUserPrincipal) authentication.getPrincipal();
        Long organizationId = principal.getOrganizationId();
        
        DashboardStatsDTO stats = dashboardPort.getDashboardStats(organizationId);
        return ResponseEntity.ok(stats);
    }
}
