package univ.lille.module_maintenance.application.dto;

import univ.lille.module_maintenance.domain.model.Priority;
import univ.lille.module_maintenance.domain.model.Status;


public record TicketFilterRequest(
    Status status,
    Priority priority,
    Long organizationId,
    Long userId
) {
}
