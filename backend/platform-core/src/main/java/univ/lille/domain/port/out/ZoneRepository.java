package univ.lille.domain.port.out;

import univ.lille.domain.model.Zone;
import univ.lille.enums.ZoneStatus;

import java.util.List;
import java.util.Optional;

public interface ZoneRepository {
    Zone save(Zone zone);
    Optional<Zone> findByIdAndOrganizationId(Long zoneId, Long orgId);
    Optional<Zone> findById(Long id);
    List<Zone> findByOrganizationId(Long organizationId);
    boolean existsByNameAndOrganizationId(String name, Long orgId);
    List<Zone> findByOrganizationIdAndStatus(Long organizationId, ZoneStatus status);

    void delete(Zone zone);


}
