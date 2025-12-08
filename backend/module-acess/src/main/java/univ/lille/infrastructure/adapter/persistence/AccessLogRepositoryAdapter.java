package univ.lille.infrastructure.adapter.persistence;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import univ.lille.domain.model.AccessLog;
import univ.lille.domain.port.out.AccessLogRepository;
import univ.lille.infrastructure.adapter.persistence.entity.AccessLogEntity;
import univ.lille.infrastructure.adapter.persistence.repository.AccessLogJpaRepository;
@Component
@RequiredArgsConstructor
public class AccessLogRepositoryAdapter implements AccessLogRepository {

    private final AccessLogJpaRepository jpaRepository; 

    @Override
    public AccessLog save(AccessLog log) {
        AccessLogEntity entity = new AccessLogEntity(); 
        entity.setUserId(log.getUserId());
        entity.setOrganizationId(log.getOrganizationId());
        entity.setTimestamp(log.getTimestamp());
        entity.setAccessGranted(log.isAccessGranted());
        entity.setReason(log.getReason());

        AccessLogEntity saved = jpaRepository.save(entity); 
        return mapToDomain(saved);
    }

    @Override
    public List<AccessLog> findByOrganizationId(Long orgId) {
        return jpaRepository.findByOrganizationIdOrderByTimestampDesc(orgId)
        .stream()
        .map(this::mapToDomain)
        .collect(Collectors.toList()); 
    }

    
    private AccessLog mapToDomain(AccessLogEntity entity) {
        return AccessLog.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .zoneId(entity.getZoneId())
                .organizationId(entity.getOrganizationId())
                .timestamp(entity.getTimestamp())
                .accessGranted(entity.isAccessGranted())
                .reason(entity.getReason())
                .build();
    }
    
}
