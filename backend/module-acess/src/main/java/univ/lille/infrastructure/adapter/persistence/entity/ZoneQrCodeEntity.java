package univ.lille.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zone_qr_codes")
@Getter 
@Setter
public class ZoneQrCodeEntity {
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY) 
    private Long id ; 

    @Column(unique = true)
    private Long zoneId; 

    private Long organizationId; 
    private String content; 
    @Lob
    private byte[] image ; 

    private String format; 

    
}
