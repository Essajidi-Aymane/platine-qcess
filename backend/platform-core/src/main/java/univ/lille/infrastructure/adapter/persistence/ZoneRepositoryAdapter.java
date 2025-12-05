package univ.lille.infrastructure.adapter.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import univ.lille.domain.model.Zone;
import univ.lille.domain.port.out.ZoneRepository;
import univ.lille.enums.ZoneStatus;
import univ.lille.infrastructure.adapter.persistence.entity.ZoneEntity;
import univ.lille.infrastructure.adapter.persistence.mapper.ZoneEntityMapper;
import univ.lille.infrastructure.adapter.persistence.repository.ZoneJpaRepository;

import java.util.List;
import java.util.Optional;
@Component
@RequiredArgsConstructor
public class ZoneRepositoryAdapter implements ZoneRepository {
    private final ZoneJpaRepository zoneJpaRepository;
    private final ZoneEntityMapper mapper;

    /**
     * @param zone
     * @return
     */
    @Override
    public Zone save(Zone zone) {
        ZoneEntity entity = mapper.toEntity(zone);
        ZoneEntity saved = zoneJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    /**
     * @param zoneId
     * @param orgId
     * @return
     */
    @Override
    public Optional<Zone> findByIdAndOrganizationId(Long zoneId, Long orgId) {
        return zoneJpaRepository.findByIdAndOrganizationId(zoneId, orgId)
                .map(mapper::toDomain);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Optional<Zone> findById(Long id) {
        return zoneJpaRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * @param organizationId
     * @return
     */
    @Override
    public List<Zone> findByOrganizationId(Long organizationId) {
        return zoneJpaRepository.findByOrganizationId(organizationId).stream().map(mapper::toDomain).toList();
    }

    /**
     * @param name
     * @param organizationId
     * @return
     */
    @Override
    public boolean existsByNameAndOrganizationId(String name, Long orgId) {
        return  zoneJpaRepository.existsByNameAndOrganizationId(name,orgId) ;
    }

    /**
     * @param organizationId
     * @param status
     * @return
     */
    @Override
    public List<Zone> findByOrganizationIdAndStatus(Long organizationId, ZoneStatus status) {
            return  zoneJpaRepository.findByOrganization_IdAndStatus(organizationId,status).stream().map(mapper::toDomain).toList();

    }

    /**
     * @param zone
     */
    @Override
    public void delete(Zone zone) {
    zoneJpaRepository.delete(mapper.toEntity(zone));
    }
}
