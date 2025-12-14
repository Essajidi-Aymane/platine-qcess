package univ.lille.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map; 

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResourceEventDTO {

    String resourceType; 
    Long resourceId; 
    Map<String , Object> payload; 
    
}
