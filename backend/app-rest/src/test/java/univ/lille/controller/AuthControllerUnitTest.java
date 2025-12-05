package univ.lille.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import univ.lille.application.service.AuthenticationService;
import univ.lille.application.usecase.LoginUseCase;
import univ.lille.application.usecase.LogoutUseCase;
import univ.lille.application.usecase.RegisterAdminUseCase;
import univ.lille.domain.model.User;
import univ.lille.dto.auth.*;

import org.springframework.http.ResponseEntity;
import univ.lille.enums.UserRole;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AuthControllerUnitTest {

    private RegisterAdminUseCase registerAdminUseCase;
    private LoginUseCase loginUseCase;
    private LogoutUseCase logoutUseCase;
    private AuthenticationService authenticationService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        registerAdminUseCase = mock(RegisterAdminUseCase.class);
        loginUseCase = mock(LoginUseCase.class);
        logoutUseCase = mock(LogoutUseCase.class);
        authenticationService = mock(AuthenticationService.class);

        authController = new AuthController(registerAdminUseCase, loginUseCase, logoutUseCase, authenticationService);
    }


    @Test
    void register_should_return_ok_with_auth_response() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("admin@test.com");
        request.setPassword("123456");
        request.setFullName("Admin User");
        request.setOrganizationName("ORG");

        AuthResponse authResponse = new AuthResponse("token", "admin@test.com", null, 1L, "Admin User");
        when(registerAdminUseCase.register(request)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.register(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isSameAs(authResponse);
        verify(registerAdminUseCase).register(request);
    }


    @Test
    void login_should_return_ok_with_auth_response() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("pwd");

        AuthResponse authResponse = AuthResponse.builder()
                .email("user@test.com")
                .fullName("User")
                .organisationId(1L)
                .role(null)
                .build();

        when(loginUseCase.login(request)).thenReturn(
                new AuthResponse("jwt", "user@test.com", null, 1L, "User")
        );

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);

        AuthResponse responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getToken()).isNull();
        assertThat(responseBody.getEmail()).isEqualTo(authResponse.getEmail());
        assertThat(responseBody.getFullName()).isEqualTo(authResponse.getFullName());
        assertThat(responseBody.getOrganisationId()).isEqualTo(authResponse.getOrganisationId());
        assertThat(responseBody.getRole()).isEqualTo(authResponse.getRole());

        String setCookieHeader = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(setCookieHeader).isNotNull();
        assertThat(setCookieHeader).contains("qcess_token=jwt");
        assertThat(setCookieHeader).contains("HttpOnly");
    }
    @Test
    void loginMobile_should_return_ok_with_auth_response() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("mobileuser@test.com");
        request.setPassword("password");

        AuthResponse authResponse = AuthResponse.builder()
                .token("jwt-token")
                .email("mobileuser@test.com")
                .fullName("Mobile User")
                .organisationId(2L)
                .role(UserRole.USER)
                .build();

        when(loginUseCase.login(request)).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.loginMobile(request);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);

        AuthResponse responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getToken()).isEqualTo("jwt-token");
        assertThat(responseBody.getEmail()).isEqualTo("mobileuser@test.com");
        assertThat(responseBody.getFullName()).isEqualTo("Mobile User");
        assertThat(responseBody.getOrganisationId()).isEqualTo(2L);
        assertThat(responseBody.getRole()).isEqualTo(UserRole.USER);

        verify(loginUseCase).login(request);
    }

    @Test
    void logout_should_clear_cookie_and_blacklist_token_when_token_exists() {
        String token = "valid-jwt-token";

        ResponseEntity<Void> response = authController.logout(token);

        assertThat(response.getStatusCode().value()).isEqualTo(200);

        verify(logoutUseCase).logout(token);

        String setCookieHeader = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(setCookieHeader).isNotNull();
        assertThat(setCookieHeader).contains("qcess_token=");
        assertThat(setCookieHeader).contains("Max-Age=0");
        assertThat(setCookieHeader).contains("HttpOnly");
    }

    @Test
    void logout_should_clear_cookie_when_token_is_missing() {
        ResponseEntity<Void> response = authController.logout(null);

        assertThat(response.getStatusCode().value()).isEqualTo(200);

        verifyNoInteractions(logoutUseCase);

        String setCookieHeader = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(setCookieHeader).isNotNull();
        assertThat(setCookieHeader).contains("qcess_token=");
        assertThat(setCookieHeader).contains("Max-Age=0");
        assertThat(setCookieHeader).contains("HttpOnly");
    }








    @Test
    void forgotPassword_should_call_usecase_and_return_ok() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("user@test.com");

        ResponseEntity<String> response = authController.forgotPassword(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody())
                .isEqualTo("Un email de réinitialisation a été envoyé si le compte existe.");

        verify(loginUseCase).forgotPassword(request);
    }


    @Test
    void resetPassword_should_call_usecase_and_return_ok() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("abc");
        request.setNewPassword("new-pass");

        ResponseEntity<String> response = authController.resetPassword(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Mot de passe réinitialisé avec succès.");

        verify(loginUseCase).resetPassword(request);
    }


    @Test
    void getCurrentUser_should_return_authenticated() {
        User fakeUser = User.builder().id(1L).email("aymane@test.com").fullName("Aymane").build();

        Mockito.when(authenticationService.getCurrentUser())
                .thenReturn(fakeUser);

        var response = authController.getCurrentUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Token is valid for user: aymane@test.com", response.getBody());
    }
}
