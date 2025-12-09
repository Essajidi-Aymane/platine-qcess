package univ.lille.module_notification.infrastructure.adapter.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import univ.lille.module_notification.infrastructure.mapper.NotificationMapper;
import univ.lille.module_notification.domain.model.Notification;
import univ.lille.module_notification.domain.port.out.NotificationRepositoryPort;
import univ.lille.module_notification.infrastructure.dao.NotificationDao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final NotificationRepositoryJpa jpa;

    @Override
    public Notification save(Notification notification) {
        NotificationDao dao = NotificationMapper.toDao(notification);
        if (dao.getCreatedAt() == null) {
            dao.setCreatedAt(LocalDateTime.now());
        }
        NotificationDao saved = jpa.save(dao);
        log.debug("Notification saved with id: {}", saved.getId());
        return NotificationMapper.toDomain(saved);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return jpa.findById(id).map(NotificationMapper::toDomain);
    }

    @Override
    public List<Notification> findByUserId(Long userId) {
        return jpa.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationMapper::toDomain)
                .toList();
    }

    @Override
    public List<Notification> findUnreadByUserId(Long userId) {
        return jpa.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationMapper::toDomain)
                .toList();
    }

    @Override
    public long countUnreadByUserId(Long userId) {
        return jpa.countByUserIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public Notification markAsRead(Long id) {
        NotificationDao dao = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
        dao.setRead(true);
        dao.setReadAt(LocalDateTime.now());
        return NotificationMapper.toDomain(jpa.save(dao));
    }

    @Override
    @Transactional
    public void markAllAsReadByUserId(Long userId) {
        jpa.markAllAsReadByUserId(userId);
        log.debug("All notifications marked as read for user: {}", userId);
    }

    @Override
    @Transactional
    public int deleteReadOlderThanDays(int days) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        int deleted = jpa.deleteReadOlderThan(threshold);
        log.info("Deleted {} read notifications older than {} days", deleted, days);
        return deleted;
    }

    @Override
    public List<Long> findDistinctUserIds() {
        return jpa.findDistinctUserIds();
    }

    @Override
    public List<Notification> findOldestByUserId(Long userId) {
        return jpa.findByUserIdOrderByCreatedAtAsc(userId)
                .stream()
                .map(NotificationMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        jpa.deleteByIdIn(ids);
        log.info("Deleted {} old notifications", ids.size());
    }
}
