package univ.lille.infrastructure.adapter.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import univ.lille.infrastructure.adapter.persistence.entity.AccessLogEntity;

import java.util.List;

public interface AccessLogJpaRepository extends JpaRepository<AccessLogEntity, Long >  {
        List<AccessLogEntity> findByOrganizationIdOrderByTimestampDesc(Long organizationId);
        List<AccessLogEntity> findByUserIdOrderByTimestampDesc(Long userId);


}
