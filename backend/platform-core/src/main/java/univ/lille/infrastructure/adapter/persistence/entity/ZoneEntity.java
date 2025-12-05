package univ.lille.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import univ.lille.enums.ZoneStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection
    @CollectionTable(
            name = "zone_allowed_roles",
            joinColumns = @JoinColumn(name = "zone_id")
    )

    private List<Long> allowedRoleIds = new ArrayList<>();
    @Enumerated(EnumType.STRING)
        private ZoneStatus status ;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
}

