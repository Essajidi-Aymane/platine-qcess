package univ.lille.module_notification.application.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import univ.lille.module_notification.domain.port.out.NotificationRepositoryPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationCleanupScheduler {

    private static final int RETENTION_DAYS = 90;
    private static final int MAX_PER_USER = 1000;

    private final NotificationRepositoryPort notificationRepository;

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupOldNotifications() {
        int deleted = notificationRepository.deleteReadOlderThanDays(RETENTION_DAYS);
        log.info("Retention cleanup deleted {} read notifications older than {} days", deleted, RETENTION_DAYS);

        List<Long> userIds = notificationRepository.findDistinctUserIds();
        for (Long userId : userIds) {
            var allAsc = notificationRepository.findOldestByUserId(userId);
            if (allAsc.size() > MAX_PER_USER) {
                int toDeleteCount = allAsc.size() - MAX_PER_USER;
                List<Long> idsToDelete = new ArrayList<>();
                for (int i = 0; i < toDeleteCount; i++) {
                    idsToDelete.add(allAsc.get(i).getId());
                }
                notificationRepository.deleteByIds(idsToDelete);
                log.info("User {} capped at {} notifications; deleted {} oldest", userId, MAX_PER_USER, toDeleteCount);
            }
        }
    }
}
