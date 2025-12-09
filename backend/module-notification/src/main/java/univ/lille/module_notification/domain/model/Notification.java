package univ.lille.module_notification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    private Long id;
    private Long userId;
    private String title;
    private String body;
    private String type;
    private boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
