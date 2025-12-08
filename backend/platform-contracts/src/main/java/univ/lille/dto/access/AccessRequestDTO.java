package univ.lille.dto.access;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessRequestDTO {
    private Long userId ; 
    private Long zoneId; 
}
