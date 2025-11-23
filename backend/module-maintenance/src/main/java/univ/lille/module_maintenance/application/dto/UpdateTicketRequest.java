package univ.lille.module_maintenance.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTicketRequest(
    @NotBlank(message = "Title is required")
    String title,
    
    @NotBlank(message = "Description is required")
    String description
) {
}