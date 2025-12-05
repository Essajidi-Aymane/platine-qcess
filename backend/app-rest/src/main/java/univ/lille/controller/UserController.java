package univ.lille.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import univ.lille.application.service.FileStorageService;
import univ.lille.domain.port.in.UserPort;
import univ.lille.dto.auth.user.CreateUserRequest;
import univ.lille.dto.auth.user.UpdateProfileRequest;
import univ.lille.dto.auth.user.UserDTO;
import univ.lille.dto.role.AssignRolesToUserRequest;
import univ.lille.dto.role.UnassignCustomRoleRequest;
import univ.lille.dto.users.UserResponse;
import univ.lille.infrastructure.adapter.security.QcessUserPrincipal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserPort userPort;
    private final FileStorageService fileStorageService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-user")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        UserDTO userDTO = userPort.createUser(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @PostMapping("/suspend-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> suspendUser(@PathVariable("userId") Long userId, @AuthenticationPrincipal QcessUserPrincipal principal) {
        Long orgId = principal.getOrganizationId();
        userPort.suspendUser(userId, orgId);
        return ResponseEntity.ok("User suspended successfully");
    }

    @PostMapping("/activate/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> activateUser(@PathVariable("userId") Long userId, @AuthenticationPrincipal QcessUserPrincipal principal) {
        Long orgId = principal.getOrganizationId();
        userPort.activateUser(userId, orgId);
        return ResponseEntity.ok("User activated successfully");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUsersOfOrganization(@AuthenticationPrincipal QcessUserPrincipal principal) {
        Long orgId = principal.getOrganizationId();
        List<UserDTO> users = userPort.getUsersByOrganizationId(orgId);
        Map<String, Object> meta = new HashMap<>();
        meta.put("size", users.size());
        meta.put("organizationId", orgId);

        return ResponseEntity.ok(UserResponse.builder()
                .data(users)
                .meta(meta)
                .build());
    }

    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignRoleToUsers(@Valid @RequestBody AssignRolesToUserRequest request, @AuthenticationPrincipal QcessUserPrincipal principal) {
        Long orgId = principal.getOrganizationId();
        userPort.assignCustomRoleToUsers(request.getRoleId(), request.getUserIds(), orgId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/unassign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unassignCustomRole(
            @Valid @RequestBody UnassignCustomRoleRequest request,
            @AuthenticationPrincipal QcessUserPrincipal principal
    ) {
        userPort.unassignCustomRoleFromUsers(
                request.getRoleId(),
                request.getUserIds(),
                principal.getOrganizationId(),
                principal.getId()
        );
        return ResponseEntity.ok().build();
    }


    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO userDTO = userPort.getCurrentUserProfile();
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(@Valid @RequestBody UpdateProfileRequest request) {
        UserDTO userDTO = userPort.updateProfile(request);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/me/profile-picture")
    public ResponseEntity<UserDTO> updateProfilePicture(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        long maxBytes = 10L * 1024L * 1024L;
        if (file.getSize() > maxBytes) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
        }

        try {
            String publicUrl = fileStorageService.saveUserAvatar(
                    userPort.getCurrentUserProfile().getId(), 
                    file
            );
            UserDTO userDTO = userPort.updateProfilePicture(publicUrl);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
