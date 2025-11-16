package univ.lille.infrastructure.adapter.persistence.mapper;

import org.springframework.stereotype.Component;
import univ.lille.domain.model.Organization;
import univ.lille.infrastructure.adapter.persistence.entity.OrganizationEntity;

@Component
public class OrganizationEntityMapper {
    public Organization toDomain(OrganizationEntity entity) {
        if (entity == null) return null;

        return Organization.builder()
                .id(entity.getId())
                .name(entity.getName())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }


    public OrganizationEntity toEntity(Organization domain) {
        if (domain == null ) return null;
        return  OrganizationEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .phone(domain.getPhone())
                .address(domain.getAddress())
                .description(domain.getDescription())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}