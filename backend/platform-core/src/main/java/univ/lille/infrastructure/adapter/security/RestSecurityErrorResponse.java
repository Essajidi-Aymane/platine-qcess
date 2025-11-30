package univ.lille.infrastructure.adapter.security;

import java.time.LocalDateTime;

public record RestSecurityErrorResponse(
        String errorCode,
        String message,
        LocalDateTime timestamp,
        String path
) {
}
