package univ.lille.dto.access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessLogResponseDTO {
    private Long id; 
    
    private String userName;
    private String zoneName;
    private LocalDateTime timestamp;
    private boolean accessGranted;
    private String reason; 
}