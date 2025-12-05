package univ.lille.application.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterTokenRequest(
    @NotBlank(message = "fcmToken is required")
    String fcmToken
) {}