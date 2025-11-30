package univ.lille.dto.role;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CustomRoleDTO {
    private Long id ;
    private String name ;
    private String description ;
    private Long organizationId ;
    private LocalDateTime createdAt;

}