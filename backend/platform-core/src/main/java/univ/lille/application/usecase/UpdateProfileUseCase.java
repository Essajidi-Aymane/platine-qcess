package univ.lille.application.usecase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.lille.application.service.AuthenticationService;
import univ.lille.domain.exception.EmailAlreadyExistsException;
import univ.lille.domain.model.User;
import univ.lille.domain.port.out.UserRepository;
import univ.lille.dto.auth.user.UpdateProfileRequest;

@Service
@RequiredArgsConstructor
public class UpdateProfileUseCase {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    @Transactional
    public User updateProfile(UpdateProfileRequest request) {
        User currentUser = authenticationService.getCurrentUser();

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            currentUser.setFirstName(request.getFirstName().trim());
        }

        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            currentUser.setLastName(request.getLastName().trim());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim().toLowerCase();
            if (!newEmail.equals(currentUser.getEmail())) {
                if (userRepository.existsByEmail(newEmail)) {
                    throw new EmailAlreadyExistsException("Email already in use: " + newEmail);
                }
                currentUser.setEmail(newEmail);
            }
        }
        return userRepository.save(currentUser);
    }

    @Transactional
    public User updateProfilePicture(String pictureUrl) {
        User currentUser = authenticationService.getCurrentUser();
        currentUser.setProfilePictureUrl(pictureUrl);
        return userRepository.save(currentUser);
    }
}