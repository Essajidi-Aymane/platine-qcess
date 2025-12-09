package univ.lille.module_notification.application.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterTokenRequest(
    @NotBlank(message = "fcmToken is required")
    String fcmToken
) {}
