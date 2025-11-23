package univ.lille.module_maintenance.application.dto;

import univ.lille.module_maintenance.domain.model.CommentType;

import java.time.LocalDateTime;

public record CommentDTO(
    Long id,
    String content,
    Long authorUserId,
    String authorUserName,
    CommentType type,
    LocalDateTime createdAt
) {
}
