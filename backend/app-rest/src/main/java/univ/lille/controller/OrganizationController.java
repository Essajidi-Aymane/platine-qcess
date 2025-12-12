package univ.lille.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

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
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomRoleDTO>> getCustomRoles(@AuthenticationPrincipal QcessUserPrincipal principal) {
        Long orgId = principal.getOrganizationId();
        List<CustomRoleDTO> roles = customRolePort.getCustomRolesByOrganization(orgId);
        return ResponseEntity.ok(roles);
    }

    /**
     * methode donne le custom role d'un user
     * */
    @GetMapping("/me/custom-role")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<CustomRoleDTO> getMyCustomRole(
            @AuthenticationPrincipal QcessUserPrincipal principal
    ) {
        Long userId = principal.getId();
        Long orgId = principal.getOrganizationId();

        CustomRoleDTO role = customRolePort.getCustomRoleForUser(userId, orgId);

        if (role == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(role);
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
