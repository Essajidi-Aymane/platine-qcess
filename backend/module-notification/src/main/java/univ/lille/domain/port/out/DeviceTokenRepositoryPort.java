package univ.lille.domain.port.out;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepositoryPort {
    void save(int userId, Integer organizationId, String fcmToken);
    List<String> getTokensByUserId(int userId);
    Optional<Integer> getUserIdByToken(String fcmToken);
    List<String> getTokensByOrganizationId(int organizationId);
}