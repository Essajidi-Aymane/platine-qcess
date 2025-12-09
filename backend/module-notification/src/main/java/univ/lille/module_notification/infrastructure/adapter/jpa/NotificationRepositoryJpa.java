package univ.lille.module_notification.infrastructure.adapter.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import univ.lille.module_notification.infrastructure.dao.NotificationDao;

import java.util.List;
import java.time.LocalDateTime;

public interface NotificationRepositoryJpa extends JpaRepository<NotificationDao, Long> {
    
    List<NotificationDao> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<NotificationDao> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);
    
    long countByUserIdAndReadFalse(Long userId);
    
    @Modifying
    @Query("UPDATE NotificationDao n SET n.read = true, n.readAt = CURRENT_TIMESTAMP WHERE n.userId = :userId AND n.read = false")
    void markAllAsReadByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM NotificationDao n WHERE n.read = true AND n.createdAt < :threshold")
    int deleteReadOlderThan(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT DISTINCT n.userId FROM NotificationDao n")
    List<Long> findDistinctUserIds();

    List<NotificationDao> findByUserIdOrderByCreatedAtAsc(Long userId);

    @Modifying
    @Query("DELETE FROM NotificationDao n WHERE n.id IN :ids")
    void deleteByIdIn(@Param("ids") List<Long> ids);
}
