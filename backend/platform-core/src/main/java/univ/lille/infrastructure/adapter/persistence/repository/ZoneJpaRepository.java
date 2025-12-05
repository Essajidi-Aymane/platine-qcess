package univ.lille.infrastructure.adapter.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.lille.enums.ZoneStatus;
import univ.lille.infrastructure.adapter.persistence.entity.ZoneEntity;

import java.util.List;
import java.util.Optional;

public interface ZoneJpaRepository extends JpaRepository<ZoneEntity,Long> {
    Optional<ZoneEntity> findByIdAndOrganizationId(Long id, Long organizationId);
    List<ZoneEntity> findByOrganizationId(Long organizationId);
    boolean existsByNameAndOrganizationId(String name, Long orgId);
    List<ZoneEntity> findByOrganization_IdAndStatus(Long orgId, ZoneStatus status);




}
