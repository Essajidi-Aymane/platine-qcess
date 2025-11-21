package univ.lille.infrastructure.adapter.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.domain.model.Organization;
import univ.lille.infrastructure.adapter.persistence.entity.OrganizationEntity;
import univ.lille.infrastructure.adapter.persistence.mapper.OrganizationEntityMapper;
import univ.lille.infrastructure.adapter.persistence.repository.OrganizationJpaRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationRepositoryAdapterTest {

    @Mock
    private OrganizationJpaRepository organizationJpaRepository;

    @Mock
    private OrganizationEntityMapper mapper;

    @InjectMocks
    private OrganizationRepositoryAdapter adapter;

    private Organization domainOrg;
    private OrganizationEntity orgEntity;

    @BeforeEach
    void setUp() {
        domainOrg = Organization.builder()
                .id(1L)
                .name("Acme Corp")
                .build();

        orgEntity = OrganizationEntity.builder()
                .id(1L)
                .name("Acme Corp")
                .build();
    }


    @Test
    void save_should_map_domain_to_entity_and_back() {
        when(mapper.toEntity(domainOrg)).thenReturn(orgEntity);
        when(organizationJpaRepository.save(orgEntity)).thenReturn(orgEntity);
        when(mapper.toDomain(orgEntity)).thenReturn(domainOrg);

        Organization result = adapter.save(domainOrg);

        assertThat(result).isSameAs(domainOrg);

        verify(mapper).toEntity(domainOrg);
        verify(organizationJpaRepository).save(orgEntity);
        verify(mapper).toDomain(orgEntity);
    }


    @Test
    void findById_should_return_mapped_organization_when_found() {
        when(organizationJpaRepository.findById(1L))
                .thenReturn(Optional.of(orgEntity));
        when(mapper.toDomain(orgEntity)).thenReturn(domainOrg);

        Optional<Organization> result = adapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(domainOrg);

        verify(organizationJpaRepository).findById(1L);
        verify(mapper).toDomain(orgEntity);
    }

    @Test
    void findById_should_return_empty_when_not_found() {
        when(organizationJpaRepository.findById(1L))
                .thenReturn(Optional.empty());

        Optional<Organization> result = adapter.findById(1L);

        assertThat(result).isEmpty();
        verify(mapper, never()).toDomain(any());
    }


    @Test
    void findByName_should_return_mapped_organization_when_found() {
        when(organizationJpaRepository.findByName("Acme Corp"))
                .thenReturn(Optional.of(orgEntity));
        when(mapper.toDomain(orgEntity)).thenReturn(domainOrg);

        Optional<Organization> result = adapter.findByName("Acme Corp");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domainOrg);

        verify(organizationJpaRepository).findByName("Acme Corp");
        verify(mapper).toDomain(orgEntity);
    }

    @Test
    void findByName_should_return_empty_when_not_found() {
        when(organizationJpaRepository.findByName("Acme Corp"))
                .thenReturn(Optional.empty());

        Optional<Organization> result = adapter.findByName("Acme Corp");

        assertThat(result).isEmpty();
        verify(mapper, never()).toDomain(any());
    }


    @Test
    void delete_should_map_domain_to_entity_and_call_delete() {
        when(mapper.toEntity(domainOrg)).thenReturn(orgEntity);

        adapter.delete(domainOrg);

        verify(mapper).toEntity(domainOrg);
        verify(organizationJpaRepository).delete(orgEntity);
    }
}
