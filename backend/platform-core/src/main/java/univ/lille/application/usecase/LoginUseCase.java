package univ.lille.application.usecase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import univ.lille.domain.exception.InvalidCredentialsException;
import univ.lille.domain.exception.UserNotFoundException;
import univ.lille.domain.model.User;
import univ.lille.domain.port.in.LoginUserPort;
import univ.lille.domain.port.out.EmailPort;
import univ.lille.domain.port.out.UserRepository;
import univ.lille.dto.auth.AuthResponse;
import univ.lille.dto.auth.ForgotPasswordRequest;
import univ.lille.dto.auth.LoginRequest;
import univ.lille.dto.auth.ResetPasswordRequest;
import univ.lille.enums.UserRole;
import univ.lille.enums.UserStatus;
import univ.lille.infrastructure.adapter.security.JwtService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginUseCase implements LoginUserPort {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailPort emailPort;

    @Value("${app.fontend.base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    /**méthode de login
     * @param loginRequest
     * @return AuthResponse
     * */
    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Tentative de connexion pour email: {}", loginRequest.getEmail());
        log.info("RememberMe recu = {}", loginRequest.isRememberMe());
        User user = getUserByEmail(loginRequest.getEmail());

        loginByRole(user, loginRequest);

        user.updateLastLogin();
        userRepository.save(user);
        String token = jwtService.generateToken(user, loginRequest.isRememberMe());

        if (loginRequest.isRememberMe()) {
            log.info("Mode RememberMe activé → Token 7 jours pour utilisateur {}", user.getEmail());
        } else {
            log.info("Mode standard → Token 24h pour utilisateur {}", user.getEmail());
        }
        log.info("Login successful for user: {}", user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getRole(), user.getOrganization().getId(), user.getFullName());


}

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        log.info("Demande de réinitialisation de mot de passe pour email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        checkIfAdmin(user);
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);

        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(expiry);
        userRepository.save(user);

        String resetLink = frontendBaseUrl + "/auth/reset-password?token=" + token;
        emailPort.sendPasswordResetEmail(user.getEmail(), user.getFullName(), resetLink);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordToken(request.getToken())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid or expired password reset token."));

        if (user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw  new InvalidCredentialsException("Invalid or expired password reset token.");
        }
        checkIfAdmin(user);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);

    }

    /**méthode de login par rôle
 * @param user
 * @param loginRequest
 * */
private void loginByRole ( User user, LoginRequest loginRequest  ) {
    if (user.getRole()== UserRole.ADMIN ) {
       verifyAdminCredentials( loginRequest, user);

    } else {
        verifyUserCredentials(loginRequest,user);

        if (user.getUserStatus() == UserStatus.PENDING) {
            user.activate();
        }
    }}


    /** méthode de vérification des credentials admin
    * @param loginRequest
    * @param user
     * @exception InvalidCredentialsException si les credentials sont invalides
    */
    private void verifyAdminCredentials( LoginRequest loginRequest, User user) {
        if (loginRequest.getPassword() == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");

        }
    }
    /**méthode de vérification des credentials user
     * @param loginRequest
     * @param user
     * @exception InvalidCredentialsException si les credentials sont invalides
     * */
    private void verifyUserCredentials( LoginRequest loginRequest, User user) {
        if (loginRequest.getLoginCode() == null || !loginRequest.getLoginCode().equals(user.getLoginCode())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }
/**méthode de récupération de l'utilisateur par email
 * @param email
 * @return User
 * */
private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
    private void checkIfAdmin(User user) {
        if (user.getRole() != UserRole.ADMIN) {
            throw new InvalidCredentialsException("Only admin users can reset password.");
        }
    }
}

