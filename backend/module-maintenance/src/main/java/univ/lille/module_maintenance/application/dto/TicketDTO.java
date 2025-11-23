package univ.lille.module_maintenance.application.dto;

import univ.lille.module_maintenance.domain.model.Comment;
import univ.lille.module_maintenance.domain.model.Priority;
import univ.lille.module_maintenance.domain.model.Status;
import univ.lille.module_maintenance.domain.model.Ticket;

import java.time.LocalDateTime;
import java.util.List;


public record TicketDTO(
    Long id,
    String title,
    String description,
    Priority priority,
    String priorityColor,
    Status status,
    List<Comment> comments,
    String createdByUserName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static TicketDTO from(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        return new TicketDTO(
            ticket.getId(),
            ticket.getTitle(),
            ticket.getDescription(),
            ticket.getPriority(),
            ticket.getPriority() != null ? ticket.getPriority().getDisplayColor() : null,
            ticket.getStatus(),
            ticket.getComments(),
            ticket.getCreatedByUserName(),
            ticket.getCreatedAt(),
            ticket.getUpdatedAt()
        );
    }
}
