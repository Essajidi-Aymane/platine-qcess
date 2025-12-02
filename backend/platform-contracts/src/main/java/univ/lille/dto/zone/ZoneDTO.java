package univ.lille.dto.zone;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ZoneDTO {

    private Long id;
    private String name;
    private String description;

    // ex: ["Security", "Cleaner", "Admin"]
    private List<String> allowedRolesNames;

    private Long organizationId;

    private LocalDateTime createdAt;

    private String status; // ACTIVE / INACTIVE
}
