package univ.lille.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import univ.lille.enums.ZoneStatus;

import java.time.LocalDateTime;

@Entity
@Table(name= "zones")
@Getter
@Setter
public class ZoneEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id ;

    private String name;

    private  String description ;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private OrganizationEntity organization;
    @Enumerated(EnumType.STRING)
        private ZoneStatus status ;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
}

