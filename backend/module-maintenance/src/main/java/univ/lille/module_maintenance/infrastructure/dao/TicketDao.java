package univ.lille.module_maintenance.infrastructure.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import univ.lille.module_maintenance.domain.model.Priority;
import univ.lille.module_maintenance.domain.model.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "comments")
@Entity
@Table(name = "tickets")
public class TicketDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Long createdByUserId;
    
    @Column(nullable = false)
    private Long organizationId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;
        
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CommentDao> comments = new ArrayList<>();
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
