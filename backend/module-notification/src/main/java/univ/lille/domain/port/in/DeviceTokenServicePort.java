package univ.lille.domain.port.in;

import java.util.List;

public interface DeviceTokenServicePort {
    void registerToken(int userId, Integer organizationId, String fcmToken);
    List<String> getTokensByUserId(int userId);
    List<String> getTokensByOrganizationId(int organizationId);
}