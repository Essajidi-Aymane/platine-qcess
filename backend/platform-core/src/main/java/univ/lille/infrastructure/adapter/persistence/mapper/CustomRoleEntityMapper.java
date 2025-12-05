package univ.lille.infrastructure.adapter.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import univ.lille.domain.model.CustomRole;
import univ.lille.infrastructure.adapter.persistence.entity.CustomRoleEntity;
import univ.lille.infrastructure.adapter.persistence.entity.OrganizationEntity;
import univ.lille.infrastructure.adapter.persistence.repository.OrganizationJpaRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomRoleEntityMapper {



    private final OrganizationJpaRepository organizationJpaRepository;

    // Entity -> Domain
    public CustomRole toDomain(CustomRoleEntity entity) {
        if (entity == null) return null;

        Long orgId = null;
        if (entity.getOrganization() != null) {
            orgId = entity.getOrganization().getId();
        }

        return CustomRole.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .orgId(orgId)
                .build();
    }

    // Domain -> Entity

    public CustomRoleEntity toEntity(CustomRole domain) {
        if (domain == null) return null;

        CustomRoleEntity e = new CustomRoleEntity();
        e.setId(domain.getId());
        e.setName(domain.getName());
        e.setCreatedAt(LocalDateTime.now());
        e.setDescription(domain.getDescription());

        OrganizationEntity orgRef =
                organizationJpaRepository.getReferenceById(domain.getOrgId());
        e.setOrganization(orgRef);

        return e;
    }
}
