package univ.lille.infrastructure.adapter.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import univ.lille.domain.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.expiration-remember-me}")
    private Long expirationRememberMe;


    public String generateToken(User user ) {
        return generateToken(user, false);
    }


    public String generateToken(User user, boolean rememberMe) {
        long exp = rememberMe ? expirationRememberMe : expiration;
        return buildToken(user, exp);

    }

    private String buildToken(User user, long exp) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .claim("orgId", user.getOrganization().getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+ exp))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try{
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

}
