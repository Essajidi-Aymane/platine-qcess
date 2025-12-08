package univ.lille.module_notification.infrastructure.adapter.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import univ.lille.module_notification.infrastructure.dao.DeviceTokenDao;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenJpaRepository extends JpaRepository<DeviceTokenDao, Long> {
    
    List<DeviceTokenDao> findByUserId(Long userId);
    
    List<DeviceTokenDao> findByOrganizationId(Long organizationId);
    
    Optional<DeviceTokenDao> findByFcmToken(String fcmToken);
    
    void deleteByFcmToken(String fcmToken);
    
    void deleteByUserId(Long userId);
}
