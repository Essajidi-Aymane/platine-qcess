package univ.lille.module_notification.domain.port.out;

import java.util.List;
import java.util.Optional;

import univ.lille.module_notification.domain.model.Notification;

public interface NotificationRepositoryPort {
    
    Notification save(Notification notification);
    
    Optional<Notification> findById(Long id);
    
    List<Notification> findByUserId(Long userId);
    
    List<Notification> findUnreadByUserId(Long userId);
    
    long countUnreadByUserId(Long userId);
    
    Notification markAsRead(Long id);
    
    void markAllAsReadByUserId(Long userId);

    int deleteReadOlderThanDays(int days);

    List<Long> findDistinctUserIds();

    List<Notification> findOldestByUserId(Long userId);

    void deleteByIds(List<Long> ids);
}
