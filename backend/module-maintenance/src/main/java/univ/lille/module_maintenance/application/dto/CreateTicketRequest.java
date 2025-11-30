package univ.lille.module_maintenance.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import univ.lille.module_maintenance.domain.model.Priority;
import univ.lille.module_maintenance.domain.model.Status;
import univ.lille.module_maintenance.domain.model.Ticket;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record CreateTicketRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    String title,
    
    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must be at most 5000 characters")
    String description,
    
    @NotNull(message = "Priority is required")
    Priority priority
) {
 
    @NonNull
    public Ticket toDomain(Long createdByUserId, Long organizationId, String createdByUserName) {
        LocalDateTime now = LocalDateTime.now();
        return Ticket.builder()
                .title(title.trim())
                .description(description == null || description.isBlank() ? null : description.trim())
                .priority(priority)
                .status(Status.OPEN)
                .createdByUserId(createdByUserId)
                .createdByUserName(createdByUserName)
                .organizationId(organizationId)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
