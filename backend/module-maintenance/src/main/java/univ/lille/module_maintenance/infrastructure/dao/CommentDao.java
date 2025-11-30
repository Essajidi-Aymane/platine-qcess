package univ.lille.module_maintenance.infrastructure.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import univ.lille.module_maintenance.domain.model.CommentType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "ticket")
@Entity
@Table(name = "comments")
public class CommentDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false)
    private Long authorUserId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentType type;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketDao ticket;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}