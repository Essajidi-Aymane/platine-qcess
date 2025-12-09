package univ.lille.module_notification.infrastructure.adapter.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import univ.lille.module_notification.infrastructure.dao.DeviceTokenDao;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenJpaRepository extends JpaRepository<DeviceTokenDao, Long> {
    
    List<DeviceTokenDao> findByUserId(Long userId);
    
    List<DeviceTokenDao> findByOrganizationId(Long organizationId);
    
    Optional<DeviceTokenDao> findByFcmToken(String fcmToken);

    @Query("SELECT DISTINCT d.userId FROM DeviceTokenDao d WHERE d.organizationId = :organizationId")
    List<Long> findDistinctUserIdsByOrganizationId(@Param("organizationId") Long organizationId);
    
    void deleteByFcmToken(String fcmToken);
    
    void deleteByUserId(Long userId);
}
