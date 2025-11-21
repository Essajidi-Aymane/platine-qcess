package univ.lille.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import univ.lille.domain.exception.InvalidCredentialsException;
import univ.lille.domain.exception.UserNotFoundException;
import univ.lille.domain.model.Organization;
import univ.lille.domain.model.User;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailPort emailPort;

    @InjectMocks
    private LoginUseCase loginUseCase;


    @Test
    void admin_login_should_succeed_with_valid_password() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@test.com");
        request.setPassword("password");
        request.setRememberMe(false);
        Organization org = Organization.builder()
                .id(10L)
                .name("Org 1")
                .build();
        User admin = User.builder()
                .id(1L)
                .email("admin@test.com")
                .role(UserRole.ADMIN)
                .password("encoded")
                .organization(org)
                .fullName("Admin Name")
                .userStatus(UserStatus.ACTIVE)
                .build();

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(jwtService.generateToken(admin, false)).thenReturn("jwt-token");

        AuthResponse response = loginUseCase.login(request);

        assertThat(response.getEmail()).isEqualTo("admin@test.com");
        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void admin_login_should_fail_with_wrong_password() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@test.com");
        request.setPassword("wrong");
        Organization org = Organization.builder()
                .id(10L)
                .name("Org 1")
                .build();
        User admin = User.builder()
                .id(1L)
                .email("admin@test.com")
                .role(UserRole.ADMIN)
                .password("encoded")
                .organization(org)
                .build();

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> loginUseCase.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void user_login_should_succeed_with_valid_login_code() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setLoginCode("123456");
        Organization org = Organization.builder()
                .id(20L)
                .name("Org 2")
                .build();
        User user = User.builder()
                .id(2L)
                .email("user@test.com")
                .role(UserRole.USER)
                .loginCode("123456")
                .organization(org)
                .userStatus(UserStatus.PENDING)
                .build();

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user, false)).thenReturn("jwt-user-token");

        AuthResponse response = loginUseCase.login(request);

        assertThat(response.getEmail()).isEqualTo("user@test.com");
        assertThat(response.getToken()).isEqualTo("jwt-user-token");
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE); // activation automatique
    }

    @Test
    void user_login_should_fail_with_wrong_code() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setLoginCode("wrong");
        Organization org = Organization.builder()
                .id(20L)
                .name("Org 2")
                .build();
        User user = User.builder()
                .email("user@test.com")
                .role(UserRole.USER)
                .loginCode("123456")
                .organization(org)
                .build();

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> loginUseCase.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_should_fail_when_user_not_found() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@test.com");

        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.login(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    // --------------------------------------------------------------------
    // FORGOT PASSWORD
    // --------------------------------------------------------------------

    @Test
    void forgotPassword_should_send_email_for_admin() {
        ForgotPasswordRequest req = new ForgotPasswordRequest();
        req.setEmail("admin@test.com");

        User admin = User.builder()
                .email("admin@test.com")
                .role(UserRole.ADMIN)
                .fullName("Admin Name")
                .build();

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));

        loginUseCase.forgotPassword(req);

        verify(emailPort).sendPasswordResetEmail(
                eq("admin@test.com"),
                eq("Admin Name"),
                contains("reset-password")
        );
    }

    @Test
    void forgotPassword_should_fail_if_not_admin() {
        ForgotPasswordRequest req = new ForgotPasswordRequest();
        req.setEmail("user@test.com");

        User user = User.builder()
                .email("user@test.com")
                .role(UserRole.USER)
                .fullName("John Doe")
                .build();

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> loginUseCase.forgotPassword(req))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    // --------------------------------------------------------------------
    // RESET PASSWORD
    // --------------------------------------------------------------------

    @Test
    void resetPassword_should_succeed() {
        ResetPasswordRequest req = new ResetPasswordRequest();
        req.setToken("abc");
        req.setNewPassword("newpass");

        User admin = User.builder()
                .role(UserRole.ADMIN)
                .passwordResetToken("abc")
                .passwordResetTokenExpiry(LocalDateTime.now().plusMinutes(10))
                .build();

        when(userRepository.findByResetPasswordToken("abc")).thenReturn(Optional.of(admin));
        when(passwordEncoder.encode("newpass")).thenReturn("encoded");

        loginUseCase.resetPassword(req);

        assertThat(admin.getPassword()).isEqualTo("encoded");
        assertThat(admin.getPasswordResetToken()).isNull();
    }

    @Test
    void resetPassword_should_fail_if_expired() {
        ResetPasswordRequest req = new ResetPasswordRequest();
        req.setToken("abc");

        User user = User.builder()
                .role(UserRole.ADMIN)
                .passwordResetToken("abc")
                .passwordResetTokenExpiry(LocalDateTime.now().minusMinutes(1))
                .build();

        when(userRepository.findByResetPasswordToken("abc")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> loginUseCase.resetPassword(req))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void resetPassword_should_fail_if_token_invalid() {
        ResetPasswordRequest req = new ResetPasswordRequest();
        req.setToken("wrong");

        when(userRepository.findByResetPasswordToken("wrong"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.resetPassword(req))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
