package univ.lille.module_maintenance.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTicketRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    String title,
    
    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must be at most 5000 characters")
    String description
) {
}