package univ.lille.infrastructure.adapter.persistence.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.domain.model.Zone;
import univ.lille.enums.ZoneStatus;
import univ.lille.infrastructure.adapter.persistence.entity.OrganizationEntity;
import univ.lille.infrastructure.adapter.persistence.entity.ZoneEntity;
import univ.lille.infrastructure.adapter.persistence.repository.OrganizationJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZoneEntityMapperTest {

    @Mock
    private OrganizationJpaRepository organizationJpaRepository;

    @InjectMocks
    private ZoneEntityMapper mapper;

    // ========================================
    // TO ENTITY TESTS
    // ========================================

    @Test
    void toEntity_should_map_all_fields_correctly() {
        Long orgId = 5L;
        LocalDateTime now = LocalDateTime.now();

        Zone domain = Zone.builder()
                .id(10L)
                .name("Zone A")
                .description("Test zone")
                .orgId(orgId)
                .status(ZoneStatus.ACTIVE)
                .createdAt(now)
                .allowedRoleIds(new ArrayList<>(List.of(1L, 2L, 3L)))
                .build();

        OrganizationEntity orgEntity = OrganizationEntity.builder()
                .id(orgId)
                .name("Org 1")
                .build();

        when(organizationJpaRepository.getReferenceById(orgId))
                .thenReturn(orgEntity);

        ZoneEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getName()).isEqualTo("Zone A");
        assertThat(entity.getDescription()).isEqualTo("Test zone");
        assertThat(entity.getStatus()).isEqualTo(ZoneStatus.ACTIVE);
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getOrganization()).isEqualTo(orgEntity);
        assertThat(entity.getAllowedRoleIds()).containsExactlyInAnyOrder(1L, 2L, 3L);

        verify(organizationJpaRepository).getReferenceById(orgId);
    }

    @Test
    void toEntity_should_handle_empty_allowed_roles() {
        Long orgId = 5L;

        Zone domain = Zone.builder()
                .id(10L)
                .name("Zone A")
                .orgId(orgId)
                .status(ZoneStatus.ACTIVE)
                .allowedRoleIds(new ArrayList<>())
                .build();

        OrganizationEntity orgEntity = new OrganizationEntity();
        when(organizationJpaRepository.getReferenceById(orgId))
                .thenReturn(orgEntity);

        ZoneEntity entity = mapper.toEntity(domain);

        assertThat(entity.getAllowedRoleIds()).isEmpty();
    }

    @Test
    void toEntity_should_handle_null_allowed_roles() {
        Long orgId = 5L;

        Zone domain = Zone.builder()
                .id(10L)
                .name("Zone A")
                .orgId(orgId)
                .status(ZoneStatus.ACTIVE)
                .allowedRoleIds(null)
                .build();

        OrganizationEntity orgEntity = new OrganizationEntity();
        when(organizationJpaRepository.getReferenceById(orgId))
                .thenReturn(orgEntity);

        ZoneEntity entity = mapper.toEntity(domain);

        assertThat(entity.getAllowedRoleIds()).isEmpty();
    }

    @Test
    void toEntity_should_return_null_when_domain_is_null() {
        ZoneEntity entity = mapper.toEntity(null);

        assertThat(entity).isNull();
        verifyNoInteractions(organizationJpaRepository);
    }

    // ========================================
    // TO DOMAIN TESTS
    // ========================================

    @Test
    void toDomain_should_map_all_fields_correctly() {
        LocalDateTime now = LocalDateTime.now();

        OrganizationEntity orgEntity = OrganizationEntity.builder()
                .id(5L)
                .name("Org 1")
                .build();

        ZoneEntity entity = new ZoneEntity();
        entity.setId(10L);
        entity.setName("Zone A");
        entity.setDescription("Test zone");
        entity.setOrganization(orgEntity);
        entity.setStatus(ZoneStatus.ACTIVE);
        entity.setCreatedAt(now);
        entity.setAllowedRoleIds(new ArrayList<>(List.of(1L, 2L, 3L)));

        Zone domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(10L);
        assertThat(domain.getName()).isEqualTo("Zone A");
        assertThat(domain.getDescription()).isEqualTo("Test zone");
        assertThat(domain.getOrgId()).isEqualTo(5L);
        assertThat(domain.getStatus()).isEqualTo(ZoneStatus.ACTIVE);
        assertThat(domain.getCreatedAt()).isEqualTo(now);
        assertThat(domain.getAllowedRoleIds()).containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    void toDomain_should_handle_empty_allowed_roles() {
        OrganizationEntity orgEntity = OrganizationEntity.builder()
                .id(5L)
                .build();

        ZoneEntity entity = new ZoneEntity();
        entity.setId(10L);
        entity.setName("Zone A");
        entity.setOrganization(orgEntity);
        entity.setStatus(ZoneStatus.ACTIVE);
        entity.setAllowedRoleIds(new ArrayList<>());

        Zone domain = mapper.toDomain(entity);

        assertThat(domain.getAllowedRoleIds()).isEmpty();
    }

    @Test
    void toDomain_should_handle_null_allowed_roles() {
        OrganizationEntity orgEntity = OrganizationEntity.builder()
                .id(5L)
                .build();

        ZoneEntity entity = new ZoneEntity();
        entity.setId(10L);
        entity.setName("Zone A");
        entity.setOrganization(orgEntity);
        entity.setStatus(ZoneStatus.ACTIVE);
        entity.setAllowedRoleIds(null);

        Zone domain = mapper.toDomain(entity);

        assertThat(domain.getAllowedRoleIds()).isEmpty();
    }

    @Test
    void toDomain_should_return_null_when_entity_is_null() {
        Zone domain = mapper.toDomain(null);

        assertThat(domain).isNull();
    }

    // ========================================
    // ROUND TRIP TEST
    // ========================================

    @Test
    void should_preserve_data_in_round_trip() {
        Long orgId = 5L;
        LocalDateTime now = LocalDateTime.now();

        Zone originalDomain = Zone.builder()
                .id(10L)
                .name("Zone A")
                .description("Test zone")
                .orgId(orgId)
                .status(ZoneStatus.ACTIVE)
                .createdAt(now)
                .allowedRoleIds(new ArrayList<>(List.of(1L, 2L)))
                .build();

        OrganizationEntity orgEntity = OrganizationEntity.builder()
                .id(orgId)
                .name("Org 1")
                .build();

        when(organizationJpaRepository.getReferenceById(orgId))
                .thenReturn(orgEntity);

        ZoneEntity entity = mapper.toEntity(originalDomain);
        Zone resultDomain = mapper.toDomain(entity);

        assertThat(resultDomain.getId()).isEqualTo(originalDomain.getId());
        assertThat(resultDomain.getName()).isEqualTo(originalDomain.getName());
        assertThat(resultDomain.getDescription()).isEqualTo(originalDomain.getDescription());
        assertThat(resultDomain.getOrgId()).isEqualTo(originalDomain.getOrgId());
        assertThat(resultDomain.getStatus()).isEqualTo(originalDomain.getStatus());
        assertThat(resultDomain.getCreatedAt()).isEqualTo(originalDomain.getCreatedAt());
        assertThat(resultDomain.getAllowedRoleIds()).containsExactlyInAnyOrderElementsOf(originalDomain.getAllowedRoleIds());
    }
}
