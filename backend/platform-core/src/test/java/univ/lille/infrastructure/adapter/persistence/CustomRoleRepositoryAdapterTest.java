package univ.lille.infrastructure.adapter.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.domain.model.CustomRole;
import univ.lille.infrastructure.adapter.persistence.entity.CustomRoleEntity;
import univ.lille.infrastructure.adapter.persistence.mapper.CustomRoleEntityMapper;
import univ.lille.infrastructure.adapter.persistence.repository.CustomRoleJpaRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomRoleRepositoryAdapterTest {

    @Mock
    private CustomRoleEntityMapper mapper;

    @Mock
    private CustomRoleJpaRepository customRoleJpaRepository;

    @InjectMocks
    private CustomRoleRepositoryAdapter adapter;

    private CustomRole domainRole;
    private CustomRoleEntity entity;

    @BeforeEach
    void setup() {
        domainRole = CustomRole.builder()
                .id(1L)
                .name("MANAGER")
                .build();

        entity = CustomRoleEntity.builder()
                .id(1L)
                .name("MANAGER")
                .build();
    }


    @Test
    void save_should_map_domain_to_entity_and_back() {
        when(mapper.toEntity(domainRole)).thenReturn(entity);
        when(customRoleJpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domainRole);

        CustomRole result = adapter.save(domainRole);

        assertThat(result).isSameAs(domainRole);
        verify(mapper).toEntity(domainRole);
        verify(customRoleJpaRepository).save(entity);
        verify(mapper).toDomain(entity);
    }


    @Test
    void findById_should_return_domain_when_found() {
        when(customRoleJpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domainRole);

        Optional<CustomRole> result = adapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(domainRole);

        verify(customRoleJpaRepository).findById(1L);
        verify(mapper).toDomain(entity);
    }

    @Test
    void findById_should_return_empty_when_not_found() {
        when(customRoleJpaRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<CustomRole> result = adapter.findById(1L);

        assertThat(result).isEmpty();
        verify(mapper, never()).toDomain(any());
    }


    @Test
    void getCustomRolesByOrganizationId_should_map_entities_to_domain() {
        CustomRoleEntity e2 = CustomRoleEntity.builder().id(2L).name("SECURITY").build();
        CustomRole r2 = CustomRole.builder().id(2L).name("SECURITY").build();

        when(customRoleJpaRepository.findByOrganization_Id(10L))
                .thenReturn(List.of(entity, e2));
        when(mapper.toDomain(entity)).thenReturn(domainRole);
        when(mapper.toDomain(e2)).thenReturn(r2);

        List<CustomRole> result = adapter.getCustomRolesByOrganizationId(10L);

        assertThat(result).containsExactly(domainRole, r2);

        verify(customRoleJpaRepository).findByOrganization_Id(10L);
        verify(mapper).toDomain(entity);
        verify(mapper).toDomain(e2);
    }


    @Test
    void findByIdInAndOrganizationId_should_map_entities_to_domain() {
        List<Long> ids = List.of(1L, 2L);

        CustomRoleEntity e2 = CustomRoleEntity.builder().id(2L).name("SECURITY").build();
        CustomRole r2 = CustomRole.builder().id(2L).name("SECURITY").build();

        when(customRoleJpaRepository.findByIdInAndOrganization_Id(ids, 10L))
                .thenReturn(List.of(entity, e2));
        when(mapper.toDomain(entity)).thenReturn(domainRole);
        when(mapper.toDomain(e2)).thenReturn(r2);

        List<CustomRole> result = adapter.findByIdInAndOrganizationId(ids, 10L);

        assertThat(result).containsExactly(domainRole, r2);

        verify(customRoleJpaRepository)
                .findByIdInAndOrganization_Id(ids, 10L);
    }


    @Test
    void existsByNameAndOrganizationId_should_return_true_when_present() {
        when(customRoleJpaRepository.findByNameAndOrganization_Id("MANAGER", 10L))
                .thenReturn(Optional.of(entity));

        boolean exists = adapter.existsByNameAndOrganizationId("MANAGER", 10L);

        assertThat(exists).isTrue();
        verify(customRoleJpaRepository).findByNameAndOrganization_Id("MANAGER", 10L);
    }

    @Test
    void existsByNameAndOrganizationId_should_return_false_when_absent() {
        when(customRoleJpaRepository.findByNameAndOrganization_Id("MANAGER", 10L))
                .thenReturn(Optional.empty());

        boolean exists = adapter.existsByNameAndOrganizationId("MANAGER", 10L);

        assertThat(exists).isFalse();
    }


    @Test
    void countUsersByRoleId_should_return_count_as_int() {
        when(customRoleJpaRepository.countUsersByRoleId(1L))
                .thenReturn(7L);

        int count = adapter.countUsersByRoleId(1L);

        assertThat(count).isEqualTo(7);
        verify(customRoleJpaRepository).countUsersByRoleId(1L);
    }


    @Test
    void delete_should_call_deleteById_directly() {
        Long roleId = 1L;

        adapter.deleteById(roleId);

        verify(customRoleJpaRepository).deleteById(roleId);
        verifyNoInteractions(mapper);
    }
}
