package univ.lille.infrastructure.adapter.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import univ.lille.domain.model.Organization;
import univ.lille.domain.model.User;
import univ.lille.enums.UserRole;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // Fake secret (must be at least 32 bytes for HS256)
        jwtService.secret = "12345678901234567890123456789012";
        jwtService.expiration = 10000L; // 10s
        jwtService.expirationRememberMe = 50000L; // 50s
    }

    private User fakeUser() {
        Organization org = new Organization();
        org.setId(999L);

        User user = new User();
        user.setId(42L);
        user.setEmail("test@example.com");
        user.setRole(UserRole.ADMIN);
        user.setOrganization(org);
        return user;
    }

    // ---------------- generateToken() ----------------

    @Test
    void generateToken_should_create_valid_jwt_with_claims() {
        User user = fakeUser();

        String token = jwtService.generateToken(user);

        // parse token manually to check claims
        SecretKey key = Keys.hmacShaKeyFor(jwtService.secret.getBytes());
        var claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("test@example.com");
        assertThat(claims.get("userId", Integer.class)).isEqualTo(42);
        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
        assertThat(claims.get("orgId", Integer.class)).isEqualTo(999);

        // expiration > now
        assertThat(claims.getExpiration()).isAfter(new Date());
    }

    // -------------- extractEmail() ----------------

    @Test
    void extractEmail_should_return_email_from_jwt() {
        String token = jwtService.generateToken(fakeUser());

        String email = jwtService.extractEmail(token);

        assertThat(email).isEqualTo("test@example.com");
    }

    // ---------------- validateToken() ----------------

    @Test
    void validateToken_should_return_true_for_valid_token() {
        String token = jwtService.generateToken(fakeUser());

        boolean valid = jwtService.validateToken(token);

        assertThat(valid).isTrue();
    }

    @Test
    void validateToken_should_return_false_for_invalid_token() {
        String token = "invalid.jwt.token";

        boolean valid = jwtService.validateToken(token);

        assertThat(valid).isFalse();
    }


    @Test
    void generateToken_should_use_rememberMe_expiration_when_true() {
        User user = fakeUser();

        String token = jwtService.generateToken(user, true);

        SecretKey key = Keys.hmacShaKeyFor(jwtService.secret.getBytes());
        var claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload();

        long exp = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();

        assertThat(exp).isEqualTo(jwtService.expirationRememberMe);
    }
}
