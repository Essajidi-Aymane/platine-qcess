package univ.lille.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import univ.lille.application.usecase.LoginUseCase;
import univ.lille.application.usecase.LogoutUseCase;
import univ.lille.application.usecase.RegisterAdminUseCase;
import univ.lille.dto.auth.*;

import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthControllerUnitTest {

    private RegisterAdminUseCase registerAdminUseCase;
    private LoginUseCase loginUseCase;
    private LogoutUseCase logoutUseCase;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        registerAdminUseCase = mock(RegisterAdminUseCase.class);
        loginUseCase = mock(LoginUseCase.class);
        logoutUseCase = mock(LogoutUseCase.class);

        authController = new AuthController(registerAdminUseCase, loginUseCase, logoutUseCase);
    }

    // ----------------------- /register -----------------------

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

    // ----------------------- /login -----------------------

    @Test
    void login_should_return_ok_with_auth_response() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("pwd");

        AuthResponse authResponse = new AuthResponse("jwt", "user@test.com", null, 1L, "User");
        when(loginUseCase.login(request)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isSameAs(authResponse);
        verify(loginUseCase).login(request);
    }

    // ----------------------- /logout -----------------------

    @Test
    void logout_should_return_bad_request_when_header_invalid() {
        ResponseEntity<String> response1 = authController.logout(null);
        ResponseEntity<String> response2 = authController.logout("Bad token");

        assertThat(response1.getStatusCode().value()).isEqualTo(400);
        assertThat(response1.getBody()).isEqualTo("Invalid token");

        assertThat(response2.getStatusCode().value()).isEqualTo(400);
        assertThat(response2.getBody()).isEqualTo("Invalid token");

        verifyNoInteractions(logoutUseCase);
    }

    @Test
    void logout_should_extract_jwt_and_call_usecase_and_return_ok() {
        String header = "Bearer my-jwt-token";

        ResponseEntity<String> response = authController.logout(header);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Logged out successfully");

        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(logoutUseCase).logout(tokenCaptor.capture());
        assertThat(tokenCaptor.getValue()).isEqualTo("my-jwt-token");
    }

    // ----------------------- /forgot-password -----------------------

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

    // ----------------------- /reset-password -----------------------

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
        ResponseEntity<String> response = authController.getCurrentUser();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Authenticated");
    }
}
