package univ.lille.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import univ.lille.domain.exception.InvalidCredentialsException;
import univ.lille.domain.exception.UserNotFoundException;
import univ.lille.domain.model.Organization;
import univ.lille.domain.model.User;
import univ.lille.domain.port.out.EmailPort;
import univ.lille.domain.port.out.NotificationPort;
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
    @Mock 
    private NotificationPort notificationPort; 

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
        assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(response.getOrganisationId()).isEqualTo(10L);
        assertThat(response.getFullName()).isEqualTo("Admin Name");
        verify(userRepository).save(admin);
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
        request.setRememberMe(false);
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
                .fullName("User Name")
                .userStatus(UserStatus.PENDING)
                .build();

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user, false)).thenReturn("jwt-user-token");

        AuthResponse response = loginUseCase.login(request);

        assertThat(response.getEmail()).isEqualTo("user@test.com");
        assertThat(response.getToken()).isEqualTo("jwt-user-token");
        assertThat(response.getRole()).isEqualTo(UserRole.USER);
        assertThat(response.getOrganisationId()).isEqualTo(20L);
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE); // activation automatique
        verify(userRepository).save(user);
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

    @Test
    void login_should_fail_when_user_is_suspended() {
        LoginRequest request = new LoginRequest();
        request.setEmail("suspended@test.com");
        request.setPassword("password");

        Organization org = Organization.builder()
                .id(5L)
                .name("Org Test")
                .build();

        User suspendedUser = User.builder()
                .id(3L)
                .email("suspended@test.com")
                .role(UserRole.ADMIN)
                .password("encoded")
                .organization(org)
                .userStatus(UserStatus.SUSPENDED)
                .build();

        when(userRepository.findByEmail("suspended@test.com")).thenReturn(Optional.of(suspendedUser));

        assertThatThrownBy(() -> loginUseCase.login(request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("User Suspended");
    }

    @Test
    void login_should_fail_when_user_is_deleted() {
        LoginRequest request = new LoginRequest();
        request.setEmail("deleted@test.com");
        request.setLoginCode("123456");

        Organization org = Organization.builder()
                .id(5L)
                .name("Org Test")
                .build();

        User deletedUser = User.builder()
                .id(4L)
                .email("deleted@test.com")
                .role(UserRole.USER)
                .loginCode("123456")
                .organization(org)
                .userStatus(UserStatus.DELETED)
                .build();

        when(userRepository.findByEmail("deleted@test.com")).thenReturn(Optional.of(deletedUser));

        assertThatThrownBy(() -> loginUseCase.login(request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("User Suspended");
    }

    @Test
    void admin_login_with_remember_me_should_generate_long_token() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@test.com");
        request.setPassword("password");
        request.setRememberMe(true);

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
        when(jwtService.generateToken(admin, true)).thenReturn("jwt-long-token");

        AuthResponse response = loginUseCase.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-long-token");
        verify(jwtService).generateToken(admin, true);
    }

    @Test
    void user_login_should_succeed_when_already_active() {
        LoginRequest request = new LoginRequest();
        request.setEmail("active.user@test.com");
        request.setLoginCode("654321");
        request.setRememberMe(false);

        Organization org = Organization.builder()
                .id(20L)
                .name("Org 2")
                .build();

        User user = User.builder()
                .id(5L)
                .email("active.user@test.com")
                .role(UserRole.USER)
                .loginCode("654321")
                .organization(org)
                .fullName("Active User")
                .userStatus(UserStatus.ACTIVE)
                .build();

        when(userRepository.findByEmail("active.user@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user, false)).thenReturn("jwt-token");

        AuthResponse response = loginUseCase.login(request);

        assertThat(response.getEmail()).isEqualTo("active.user@test.com");
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        verify(userRepository).save(user);
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
