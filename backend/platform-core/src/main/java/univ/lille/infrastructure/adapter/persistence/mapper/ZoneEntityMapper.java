package univ.lille.infrastructure.adapter.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import univ.lille.domain.model.Zone;
import univ.lille.infrastructure.adapter.persistence.entity.OrganizationEntity;
import univ.lille.infrastructure.adapter.persistence.entity.ZoneEntity;
import univ.lille.infrastructure.adapter.persistence.repository.OrganizationJpaRepository;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class ZoneEntityMapper {
    private final OrganizationJpaRepository organizationJpaRepository;

    public ZoneEntity toEntity(Zone z) {
        if (z == null) {
            return null;
        }

        OrganizationEntity organization =
                organizationJpaRepository.getReferenceById(z.getOrgId());

        ZoneEntity e = new ZoneEntity();
        e.setId(z.getId());
        e.setName(z.getName());
        e.setDescription(z.getDescription());
        e.setOrganization(organization);
        e.setStatus(z.getStatus());
        e.setCreatedAt(z.getCreatedAt());

        e.setAllowedRoleIds(
                z.getAllowedRoleIds() != null
                        ? new ArrayList<>(z.getAllowedRoleIds())
                        : new ArrayList<>()
        );

        return e;
    }

    public Zone toDomain(ZoneEntity e) {
        if (e == null) {
            return null;
        }

        return Zone.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .orgId(e.getOrganization().getId())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .allowedRoleIds(
                        e.getAllowedRoleIds() != null
                                ? new ArrayList<>(e.getAllowedRoleIds())
                                : new ArrayList<>()
                )
                .build();
    }
}
