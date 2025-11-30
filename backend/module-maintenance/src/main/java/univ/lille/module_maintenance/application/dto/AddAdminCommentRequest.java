package univ.lille.module_maintenance.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AddAdminCommentRequest(
    @NotBlank(message = "Comment cannot be empty")
    String comment
) {
}
