package univ.lille.infrastructure.adapter.persistence.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name="access_logs") 
@Getter 
@Setter
public class AccessLogEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ; 
    private Long userId; 
    private Long zoneId; 
    private String userName;
    private String zoneName; 
    private Long organizationId; 
    private LocalDateTime timestamp; 
    private boolean accessGranted ; 
    private String reason; 
    
}
