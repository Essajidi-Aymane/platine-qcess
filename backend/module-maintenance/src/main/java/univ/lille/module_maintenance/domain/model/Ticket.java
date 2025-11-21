package univ.lille.module_maintenance.domain.model;

@Builder
@Data
public class Ticket {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private Set<Comment> comments;
    private Date createdAt;
    private Date updatedAt;
}
