package univ.lille.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import univ.lille.domain.port.in.ZoneManagementPort;
import univ.lille.dto.zone.CreateZoneRequest;
import univ.lille.dto.zone.UpdateZoneRequest;
import univ.lille.dto.zone.ZoneDTO;
import univ.lille.infrastructure.adapter.security.QcessUserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class ZoneController {
    private final ZoneManagementPort zonePort;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ZoneDTO> createZone(@Valid @RequestBody CreateZoneRequest request , @AuthenticationPrincipal QcessUserPrincipal principal) {

        ZoneDTO zoneDTO = zonePort.createZone(request,principal.getOrganizationId()) ;
        return  ResponseEntity.status(HttpStatus.CREATED).body(zoneDTO);
    }
     @DeleteMapping("/delete/{zoneId}")
     @PreAuthorize("hasRole('ADMIN')")
     public ResponseEntity<String> deleteZone(@PathVariable("zoneId") Long zoneId, @AuthenticationPrincipal QcessUserPrincipal principal) {
        Long adminId = principal.getId();
        Long orgId = principal.getOrganizationId();
        zonePort.deleteZone( zoneId ,orgId) ;
        return ResponseEntity.ok("Zone deleted successfully") ;
     }

    @GetMapping("/{zoneId}")
    public ResponseEntity<ZoneDTO> getZone(@PathVariable("zoneId") Long zoneId , @AuthenticationPrincipal QcessUserPrincipal principal) {

        ZoneDTO zoneDTO = zonePort.getZone(principal.getOrganizationId(),zoneId);
        return ResponseEntity.ok(zoneDTO);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ZoneDTO>> getZonesForOrg(@AuthenticationPrincipal QcessUserPrincipal principal) {
        Long orgId = principal.getOrganizationId();
        List<ZoneDTO> zones = zonePort.getZonesForOrg(orgId);
        return ResponseEntity.ok(zones);
    }

    @PatchMapping("/{zoneId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ZoneDTO> updateZone (@PathVariable("zoneId") Long zoneId , UpdateZoneRequest request, @AuthenticationPrincipal QcessUserPrincipal principal) {
        Long orgId = principal.getOrganizationId();
        ZoneDTO updated = zonePort.updateZone(zoneId,request,orgId ) ;
        return  ResponseEntity.ok(updated);
    }

}
