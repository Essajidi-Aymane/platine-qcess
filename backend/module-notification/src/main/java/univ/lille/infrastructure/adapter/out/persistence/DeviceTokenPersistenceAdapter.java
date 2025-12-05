package univ.lille.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import univ.lille.domain.port.out.DeviceTokenRepositoryPort;

import java.util.List;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceTokenPersistenceAdapter implements DeviceTokenRepositoryPort {

    private final DeviceTokenJpaRepository jpaRepository;

    @Override
    public void save(int userId, Integer organizationId, String fcmToken) {
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
                    jpaRepository.save(DeviceTokenEntity.builder()
                        .userId(userId)
                        .organizationId(organizationId)
                        .fcmToken(fcmToken)
                        .build());
                    log.info("Registered new FCM token for user {}", userId);
                }
            );
    }

    @Override
    public List<String> getTokensByUserId(int userId) {
        return jpaRepository.findByUserId(userId).stream()
            .map(DeviceTokenEntity::getFcmToken)
            .toList();
    }

    @Override
    public List<String> getTokensByOrganizationId(int organizationId) {
        return jpaRepository.findByOrganizationId(organizationId).stream()
            .map(DeviceTokenEntity::getFcmToken)
            .toList();
    }

    @Override
    public Optional<Integer> getUserIdByToken(String fcmToken) {
        return jpaRepository.findByFcmToken(fcmToken)
            .map(DeviceTokenEntity::getUserId);
    }
}
