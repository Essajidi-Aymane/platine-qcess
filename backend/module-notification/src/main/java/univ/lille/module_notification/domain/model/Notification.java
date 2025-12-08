package univ.lille.module_notification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class Notification {
    private Long id;
    private int userId;
    private String title;
    private String body;
    private LocalDateTime createdAt;
}
