package univ.lille.infrastructure.adapter.persistence.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.domain.model.Organization;
import univ.lille.infrastructure.adapter.persistence.entity.OrganizationEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrganizationEntityMapperTest {

    @InjectMocks
    private OrganizationEntityMapper mapper;

    // ========================================
    // TO DOMAIN TESTS
    // ========================================

    @Test
    void toDomain_should_map_all_fields_correctly() {
        LocalDateTime now = LocalDateTime.now();

        OrganizationEntity entity = OrganizationEntity.builder()
                .id(1L)
                .name("Test Organization")
                .phone("+33123456789")
                .address("123 Main St, Paris")
                .description("Test organization description")
                .createdAt(now)
                .build();

        Organization domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getName()).isEqualTo("Test Organization");
        assertThat(domain.getPhone()).isEqualTo("+33123456789");
        assertThat(domain.getAddress()).isEqualTo("123 Main St, Paris");
        assertThat(domain.getDescription()).isEqualTo("Test organization description");
        assertThat(domain.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void toDomain_should_handle_null_optional_fields() {
        OrganizationEntity entity = OrganizationEntity.builder()
                .id(1L)
                .name("Test Organization")
                .build();

        Organization domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getName()).isEqualTo("Test Organization");
        assertThat(domain.getPhone()).isNull();
        assertThat(domain.getAddress()).isNull();
        assertThat(domain.getDescription()).isNull();
        assertThat(domain.getCreatedAt()).isNull();
    }

    @Test
    void toDomain_should_return_null_when_entity_is_null() {
        Organization domain = mapper.toDomain(null);

        assertThat(domain).isNull();
    }

    // ========================================
    // TO ENTITY TESTS
    // ========================================

    @Test
    void toEntity_should_map_all_fields_correctly() {
        LocalDateTime now = LocalDateTime.now();

        Organization domain = Organization.builder()
                .id(1L)
                .name("Test Organization")
                .phone("+33123456789")
                .address("123 Main St, Paris")
                .description("Test organization description")
                .createdAt(now)
                .build();

        OrganizationEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("Test Organization");
        assertThat(entity.getPhone()).isEqualTo("+33123456789");
        assertThat(entity.getAddress()).isEqualTo("123 Main St, Paris");
        assertThat(entity.getDescription()).isEqualTo("Test organization description");
        assertThat(entity.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void toEntity_should_handle_null_optional_fields() {
        Organization domain = Organization.builder()
                .id(1L)
                .name("Test Organization")
                .build();

        OrganizationEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("Test Organization");
        assertThat(entity.getPhone()).isNull();
        assertThat(entity.getAddress()).isNull();
        assertThat(entity.getDescription()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
    }

    @Test
    void toEntity_should_return_null_when_domain_is_null() {
        OrganizationEntity entity = mapper.toEntity(null);

        assertThat(entity).isNull();
    }

    // ========================================
    // ROUND TRIP TEST
    // ========================================

    @Test
    void should_preserve_data_in_round_trip() {
        LocalDateTime now = LocalDateTime.now();

        Organization originalDomain = Organization.builder()
                .id(1L)
                .name("Test Organization")
                .phone("+33123456789")
                .address("123 Main St, Paris")
                .description("Test organization description")
                .createdAt(now)
                .build();

        OrganizationEntity entity = mapper.toEntity(originalDomain);
        Organization resultDomain = mapper.toDomain(entity);

        assertThat(resultDomain.getId()).isEqualTo(originalDomain.getId());
        assertThat(resultDomain.getName()).isEqualTo(originalDomain.getName());
        assertThat(resultDomain.getPhone()).isEqualTo(originalDomain.getPhone());
        assertThat(resultDomain.getAddress()).isEqualTo(originalDomain.getAddress());
        assertThat(resultDomain.getDescription()).isEqualTo(originalDomain.getDescription());
        assertThat(resultDomain.getCreatedAt()).isEqualTo(originalDomain.getCreatedAt());
    }

    @Test
    void should_handle_minimal_data_in_round_trip() {
        Organization originalDomain = Organization.builder()
                .id(2L)
                .name("Minimal Org")
                .build();

        OrganizationEntity entity = mapper.toEntity(originalDomain);
        Organization resultDomain = mapper.toDomain(entity);

        assertThat(resultDomain.getId()).isEqualTo(2L);
        assertThat(resultDomain.getName()).isEqualTo("Minimal Org");
        assertThat(resultDomain.getPhone()).isNull();
        assertThat(resultDomain.getAddress()).isNull();
        assertThat(resultDomain.getDescription()).isNull();
    }
}
