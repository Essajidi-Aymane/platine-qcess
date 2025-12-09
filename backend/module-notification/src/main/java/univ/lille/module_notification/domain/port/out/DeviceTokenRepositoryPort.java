package univ.lille.module_notification.domain.port.out;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepositoryPort {
    void save(Long userId, Long organizationId, String fcmToken);
    List<String> getTokensByUserId(Long userId);
    List<String> getTokensByOrganizationId(Long organizationId);
    Optional<Long> getUserIdByToken(String fcmToken);
    List<Long> getUserIdsByOrganizationId(Long organizationId);
}