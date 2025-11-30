package univ.lille.module_maintenance.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AddCommentRequest(
    @NotBlank(message = "Comment content is required")
    String content
) {
}
