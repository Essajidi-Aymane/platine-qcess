package univ.lille.module_notification.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendNotificationRequest(
    @NotNull(message = "userId is required")
    Integer userId,
    
    @NotBlank(message = "title is required")
    String title,
    
    @NotBlank(message = "body is required")
    String body
) {}
