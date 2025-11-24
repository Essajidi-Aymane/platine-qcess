package univ.lille.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import univ.lille.application.usecase.OrganizationUseCase;
import univ.lille.dto.org.OrganizationUpdateRequest;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationUseCase organizationUseCase ;

    @PatchMapping("/update-details")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateOrgDetails(@Valid @RequestBody OrganizationUpdateRequest request) {
        organizationUseCase.updateOrganizationDetails(request);
        return ResponseEntity.ok("Organization details updated successfully.");
    }

}
