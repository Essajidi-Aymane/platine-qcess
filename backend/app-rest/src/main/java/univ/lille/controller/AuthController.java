package univ.lille.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import univ.lille.application.service.AuthenticationService;
import univ.lille.application.usecase.LoginUseCase;
import univ.lille.application.usecase.LogoutUseCase;
import univ.lille.application.usecase.RegisterAdminUseCase;
import univ.lille.domain.port.out.UserRepository;
import univ.lille.dto.auth.*;
import univ.lille.infrastructure.adapter.security.QcessUserPrincipal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterAdminUseCase registerAdminUseCase;
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {

        AuthResponse res = registerAdminUseCase.register(request);
        ResponseCookie cookie = ResponseCookie.from("qcess_token", res.getToken())
                .httpOnly(true)
                .secure(false) // true in production with HTTPS
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .sameSite("Lax")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body(res);
    }

    @PostMapping("/login/web")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse res = loginUseCase.login(loginRequest);
        ResponseCookie cookie = ResponseCookie.from("qcess_token", res.getToken())
                .httpOnly(true)
                .secure(false) // true in production with HTTPS
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .sameSite("Lax")
                .build();

        AuthResponse webResponse = AuthResponse.builder()
                .organisationId(res.getOrganisationId())
                .email(res.getEmail())
                .fullName(res.getFullName())
                .role(res.getRole())
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body(webResponse);


    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginMobile(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse res = loginUseCase.login(loginRequest);
        return ResponseEntity.ok(res);
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "qcess_token", required = false) String token
    ) {
        if (token != null && !token.isBlank()) {
            logoutUseCase.logout(token);
        }

        ResponseCookie deleteCookie = ResponseCookie.from("qcess_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        loginUseCase.forgotPassword(request);
        return ResponseEntity.ok("Un email de réinitialisation a été envoyé si le compte existe.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        loginUseCase.resetPassword(request);
        return ResponseEntity.ok("Mot de passe réinitialisé avec succès.");
    }


    /**
     * Test si le token est valide
     */
    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser() {
        var user = authenticationService.getCurrentUser();
        CurrentUserResponse response = CurrentUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
        return ResponseEntity.ok("Token is valid for user: " + response.getEmail());

    }


}
