package univ.lille.infrastructure.adapter.persistence.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.domain.model.CustomRole;
import univ.lille.infrastructure.adapter.persistence.entity.CustomRoleEntity;
import univ.lille.infrastructure.adapter.persistence.entity.OrganizationEntity;
import univ.lille.infrastructure.adapter.persistence.repository.OrganizationJpaRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomRoleEntityMapperTest {

    @Mock
    private OrganizationJpaRepository organizationJpaRepository;

    @InjectMocks
    private CustomRoleEntityMapper mapper;

    // ========================================
    // TO DOMAIN TESTS
    // ========================================

    @Test
    void toDomain_should_map_all_fields_correctly() {
        LocalDateTime now = LocalDateTime.now();

        OrganizationEntity orgEntity = OrganizationEntity.builder()
                .id(5L)
                .name("Test Org")
                .build();

        CustomRoleEntity entity = new CustomRoleEntity();
        entity.setId(10L);
        entity.setName("Manager");
        entity.setDescription("Manager role with full access");
        entity.setOrganization(orgEntity);
        entity.setCreatedAt(now);

        CustomRole domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(10L);
        assertThat(domain.getName()).isEqualTo("Manager");
        assertThat(domain.getDescription()).isEqualTo("Manager role with full access");
        assertThat(domain.getOrgId()).isEqualTo(5L);
        assertThat(domain.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void toDomain_should_handle_null_organization() {
        CustomRoleEntity entity = new CustomRoleEntity();
        entity.setId(10L);
        entity.setName("Manager");
        entity.setDescription("Manager role");
        entity.setOrganization(null);

        CustomRole domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(10L);
        assertThat(domain.getName()).isEqualTo("Manager");
        assertThat(domain.getOrgId()).isNull();
    }

    @Test
    void toDomain_should_handle_null_optional_fields() {
        OrganizationEntity orgEntity = OrganizationEntity.builder()
                .id(5L)
                .build();

        CustomRoleEntity entity = new CustomRoleEntity();
        entity.setId(10L);
        entity.setName("Manager");
        entity.setOrganization(orgEntity);

        CustomRole domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(10L);
        assertThat(domain.getName()).isEqualTo("Manager");
        assertThat(domain.getDescription()).isNull();
        assertThat(domain.getOrgId()).isEqualTo(5L);
    }

    @Test
    void toDomain_should_return_null_when_entity_is_null() {
        CustomRole domain = mapper.toDomain(null);

        assertThat(domain).isNull();
    }

    // ========================================
    // TO ENTITY TESTS
    // ========================================

    @Test
    void toEntity_should_map_all_fields_correctly() {
        Long orgId = 5L;
        LocalDateTime now = LocalDateTime.now();

        CustomRole domain = CustomRole.builder()
                .id(10L)
                .name("Manager")
                .description("Manager role with full access")
                .orgId(orgId)
                .createdAt(now)
                .build();

        OrganizationEntity orgEntity = OrganizationEntity.builder()
                .id(orgId)
                .name("Test Org")
                .build();

        when(organizationJpaRepository.getReferenceById(orgId))
                .thenReturn(orgEntity);

        CustomRoleEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getName()).isEqualTo("Manager");
        assertThat(entity.getDescription()).isEqualTo("Manager role with full access");
        assertThat(entity.getOrganization()).isEqualTo(orgEntity);
        assertThat(entity.getCreatedAt()).isNotNull();

        verify(organizationJpaRepository).getReferenceById(orgId);
    }

    @Test
    void toEntity_should_handle_null_optional_fields() {
        Long orgId = 5L;

        CustomRole domain = CustomRole.builder()
                .id(10L)
                .name("Manager")
                .orgId(orgId)
                .build();

        OrganizationEntity orgEntity = new OrganizationEntity();

        when(organizationJpaRepository.getReferenceById(orgId))
                .thenReturn(orgEntity);

        CustomRoleEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getName()).isEqualTo("Manager");
        assertThat(entity.getDescription()).isNull();
        assertThat(entity.getOrganization()).isEqualTo(orgEntity);
    }

    @Test
    void toEntity_should_return_null_when_domain_is_null() {
        CustomRoleEntity entity = mapper.toEntity(null);

        assertThat(entity).isNull();
        verifyNoInteractions(organizationJpaRepository);
    }

    // ========================================
    // ROUND TRIP TEST
    // ========================================

    @Test
    void should_preserve_data_in_round_trip() {
        Long orgId = 5L;
        LocalDateTime now = LocalDateTime.now();

        CustomRole originalDomain = CustomRole.builder()
                .id(10L)
                .name("Manager")
                .description("Manager role with full access")
                .orgId(orgId)
                .createdAt(now)
                .build();

        OrganizationEntity orgEntity = OrganizationEntity.builder()
                .id(orgId)
                .name("Test Org")
                .build();

        when(organizationJpaRepository.getReferenceById(orgId))
                .thenReturn(orgEntity);

        CustomRoleEntity entity = mapper.toEntity(originalDomain);
        CustomRole resultDomain = mapper.toDomain(entity);

        assertThat(resultDomain.getId()).isEqualTo(originalDomain.getId());
        assertThat(resultDomain.getName()).isEqualTo(originalDomain.getName());
        assertThat(resultDomain.getDescription()).isEqualTo(originalDomain.getDescription());
        assertThat(resultDomain.getOrgId()).isEqualTo(originalDomain.getOrgId());
        // Note: createdAt may differ as toEntity sets LocalDateTime.now()
    }

    @Test
    void should_handle_minimal_data_in_round_trip() {
        Long orgId = 5L;

        CustomRole originalDomain = CustomRole.builder()
                .id(10L)
                .name("Manager")
                .orgId(orgId)
                .build();

        OrganizationEntity orgEntity = OrganizationEntity.builder()
                .id(orgId)
                .build();

        when(organizationJpaRepository.getReferenceById(orgId))
                .thenReturn(orgEntity);

        CustomRoleEntity entity = mapper.toEntity(originalDomain);
        CustomRole resultDomain = mapper.toDomain(entity);

        assertThat(resultDomain.getId()).isEqualTo(10L);
        assertThat(resultDomain.getName()).isEqualTo("Manager");
        assertThat(resultDomain.getOrgId()).isEqualTo(orgId);
        assertThat(resultDomain.getDescription()).isNull();
    }
}
