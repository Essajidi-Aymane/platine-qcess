package univ.lille.dto.access;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessResponseDTO {
    private boolean granted ; 
    private String reason ; 
    private String zoneName; 
        
}
