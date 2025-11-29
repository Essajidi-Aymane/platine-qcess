package univ.lille.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import univ.lille.domain.port.in.CustomRolePort;
import univ.lille.domain.port.in.OrganizationManagementPort;
import univ.lille.dto.org.OrganizationUpdateRequest;
import univ.lille.dto.role.CreateCustomRoleRequest;
import univ.lille.dto.role.CustomRoleDTO;
import univ.lille.dto.role.UpdateCustomRoleRequest;
import univ.lille.infrastructure.adapter.security.QcessUserPrincipal;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationManagementPort organizationManagementPort;
    private final CustomRolePort customRolePort;

    @PatchMapping("/update-details")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateOrgDetails(@Valid @RequestBody OrganizationUpdateRequest request) {
        organizationManagementPort.updateOrganizationDetails(request);
        return ResponseEntity.ok("Organization details updated successfully.");
    }

    @PostMapping("/create-custom-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomRoleDTO> createCustomRole(@Valid @RequestBody CreateCustomRoleRequest request, @AuthenticationPrincipal QcessUserPrincipal principal) {
       Long adminId = principal.getId();
       Long orgId = principal.getOrganizationId();
        CustomRoleDTO createdRole = customRolePort.createCustomRole(request, orgId, adminId);
        return ResponseEntity.ok(createdRole);
    }

    @PatchMapping("/update-custom-role/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomRoleDTO> updateCustomRole(@PathVariable("roleId") Long roleId,
                                                          @Valid @RequestBody UpdateCustomRoleRequest request,
                                                          @AuthenticationPrincipal QcessUserPrincipal principal) {
        Long adminId = principal.getId();
        Long orgId = principal.getOrganizationId();
        CustomRoleDTO updatedRole = customRolePort.updateCustomRole(roleId, orgId, adminId, request);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/delete-custom-role/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCustomRole(@PathVariable("roleId") Long roleId, @AuthenticationPrincipal QcessUserPrincipal principal) {
        Long adminId = principal.getId();
        Long orgId = principal.getOrganizationId();
        customRolePort.deleteCustomRole(roleId, orgId, adminId);
        return ResponseEntity.ok("Custom role deleted successfully.");
    }


}
