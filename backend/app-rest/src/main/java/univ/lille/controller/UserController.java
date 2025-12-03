package univ.lille.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import univ.lille.application.service.AuthenticationService;
import univ.lille.application.service.FileStorageService;
import univ.lille.application.usecase.CreateUserUseCase;
import univ.lille.application.usecase.UpdateProfileUseCase;
import univ.lille.application.usecase.mapper.UserMapper;
import univ.lille.domain.model.User;
import univ.lille.dto.auth.user.CreateUserRequest;
import univ.lille.dto.auth.user.UpdateProfileRequest;
import univ.lille.dto.auth.user.UserDTO;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;
    private final FileStorageService fileStorageService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-user")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        UserDTO userDTO = createUserUseCase.createUser(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        User user = authenticationService.getCurrentUser();
        UserDTO userDTO = userMapper.toDTO(user);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(@Valid @RequestBody UpdateProfileRequest request) {
        User updatedUser = updateProfileUseCase.updateProfile(request);
        UserDTO userDTO = userMapper.toDTO(updatedUser);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/me/profile-picture")
    public ResponseEntity<UserDTO> updateProfilePicture(@Valid @RequestPart("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            long maxBytes = 10L * 1024L * 1024L;
            if (file.getSize() > maxBytes) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
            }

            User currentUser = authenticationService.getCurrentUser();
            String publicUrl = fileStorageService.saveUserAvatar(currentUser.getId(), file);
            User updated = updateProfileUseCase.updateProfilePicture(publicUrl);
            UserDTO userDTO = userMapper.toDTO(updated);
            return ResponseEntity.ok(userDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
