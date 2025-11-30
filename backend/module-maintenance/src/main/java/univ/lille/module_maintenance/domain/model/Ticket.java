package univ.lille.module_maintenance.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private Long id;
    
    private String title;
    private String description;
    private Priority priority;
    
    private Status status;
        
    private Long createdByUserId;
    private String createdByUserName;
    private Long organizationId;
    
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public boolean belongsTo(Long userId) {
        return this.createdByUserId != null && this.createdByUserId.equals(userId);
    }

    public boolean belongsToOrganization(Long orgId) {
        return this.organizationId != null && this.organizationId.equals(orgId);
    }

    public void updateStatus(Status newStatus) {
        if (this.status.canTransitionTo(newStatus)) {
            this.status = newStatus;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot transition from " + this.status + " to " + newStatus);
        }
    }
    
    public void addComment(Comment comment) {
        this.comments.add(comment);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isOpen() {
        return this.status == Status.OPEN;
    }
    
    public boolean isInProgress() {
        return this.status == Status.IN_PROGRESS;
    }
    
    public boolean isResolved() {
        return this.status == Status.RESOLVED;
    }

    public boolean isRejected() {
        return this.status == Status.REJECTED;
    }
}
