package univ.lille.module_maintenance.application.dto;

import jakarta.validation.constraints.NotNull;
import univ.lille.module_maintenance.domain.model.Status;

public record UpdateTicketStatusRequest(
    @NotNull(message = "Status is required")
    Status newStatus
) {
}
