package univ.lille.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DeviceTokenJpaRepository extends JpaRepository<DeviceTokenEntity, Integer> {
    
    List<DeviceTokenEntity> findByUserId(Integer userId);
    
    List<DeviceTokenEntity> findByOrganizationId(Integer organizationId);
    
    Optional<DeviceTokenEntity> findByFcmToken(String fcmToken);
    
    void deleteByFcmToken(String fcmToken);
    
    void deleteByUserId(Integer userId);
}
