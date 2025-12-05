package univ.lille.application.dto;

import jakarta.validation.constraints.NotBlank;

public record SendToTokenRequest(
    @NotBlank(message = "fcmToken is required")
    String fcmToken,
    
    @NotBlank(message = "title is required")
    String title,
    
    @NotBlank(message = "body is required")
    String body
) {}
