package univ.lille.module_notification.infrastructure.adapter.jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import univ.lille.module_notification.domain.port.out.DeviceTokenRepositoryPort;
import univ.lille.module_notification.infrastructure.dao.DeviceTokenDao;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceTokenRepositoryAdapter implements DeviceTokenRepositoryPort {

    private final DeviceTokenJpaRepository jpaRepository;

    @Override
    public void save(Long userId, Long organizationId, String fcmToken) {
        jpaRepository.findByFcmToken(fcmToken)
            .ifPresentOrElse(
                existing -> {
                    if (!existing.getUserId().equals(userId)) {
                        existing.setUserId(userId);
                        existing.setOrganizationId(organizationId);
                        jpaRepository.save(existing);
                        log.info("Updated FCM token for user {} (was user {})", userId, existing.getUserId());
                    }
                },
                () -> {
                    jpaRepository.save(DeviceTokenDao.builder()
                        .userId(userId)
                        .organizationId(organizationId)
                        .fcmToken(fcmToken)
                        .build());
                    log.info("Registered new FCM token for user {}", userId);
                }
            );
    }

    @Override
    public List<String> getTokensByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
            .map(DeviceTokenDao::getFcmToken)
            .toList();
    }

    @Override
    public List<String> getTokensByOrganizationId(Long organizationId) {
        return jpaRepository.findByOrganizationId(organizationId).stream()
            .map(DeviceTokenDao::getFcmToken)
            .toList();
    }

    @Override
    public List<Long> getUserIdsByOrganizationId(Long organizationId) {
        return jpaRepository.findDistinctUserIdsByOrganizationId(organizationId);
    }

    @Override
    public Optional<Long> getUserIdByToken(String fcmToken) {
        return jpaRepository.findByFcmToken(fcmToken)
            .map(DeviceTokenDao::getUserId);
    }
}
