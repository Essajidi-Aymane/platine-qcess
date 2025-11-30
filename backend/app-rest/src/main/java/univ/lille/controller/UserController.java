package univ.lille.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import univ.lille.application.usecase.UserUseCase;
import univ.lille.domain.port.in.UserPort;
import univ.lille.dto.auth.user.CreateUserRequest;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-user")
    public ResponseEntity<UserDTO> createUser (@Valid @RequestBody CreateUserRequest createUserRequest) {


        UserDTO userDTO = userPort.createUser(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUsersOfOrganization(@AuthenticationPrincipal QcessUserPrincipal principal) {

    Long orgId = principal.getOrganizationId();
    List<UserDTO> users = userPort.getUsersByOrganizationId(orgId);
        Map<String, Object> meta = new HashMap<>() ;
        meta.put("size", users.size());
        meta.put("organizationId", orgId);

    return ResponseEntity.ok(UserResponse.builder()
            .data(users)
            .meta(meta)
            .build());
    }

    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignRoleToUsers(@Valid @RequestBody AssignRolesToUserRequest request , @AuthenticationPrincipal QcessUserPrincipal principal) {
            Long adminId = principal.getId();
            Long orgId = principal.getOrganizationId();
            userPort.assignCustomRoleToUsers(request.getRoleIds(),request.getUserIds(), adminId, orgId);


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


}
