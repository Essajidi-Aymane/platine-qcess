package univ.lille.module_notification.domain.port;

import java.util.List;

public interface DeviceTokenServicePort {
    void registerToken(Long userId, Long organizationId, String fcmToken);
    List<String> getTokensByUserId(Long userId);
    List<String> getTokensByOrganizationId(Long organizationId);
}
