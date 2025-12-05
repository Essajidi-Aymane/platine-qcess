package univ.lille.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.lille.domain.port.in.DeviceTokenServicePort;
import univ.lille.domain.port.out.DeviceTokenRepositoryPort;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceTokenService implements DeviceTokenServicePort {

    private final DeviceTokenRepositoryPort deviceTokenRepository;

    @Override
    public void registerToken(int userId, Integer organizationId, String fcmToken) {
        deviceTokenRepository.save(userId, organizationId, fcmToken);
    }

    @Override
    public List<String> getTokensByUserId(int userId) {
        return deviceTokenRepository.getTokensByUserId(userId);
    }

    @Override
    public List<String> getTokensByOrganizationId(int organizationId) {
        return deviceTokenRepository.getTokensByOrganizationId(organizationId);
    }
}