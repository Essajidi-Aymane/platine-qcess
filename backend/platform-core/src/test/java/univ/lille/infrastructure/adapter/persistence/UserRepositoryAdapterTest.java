package univ.lille.infrastructure.adapter.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.domain.model.User;
import univ.lille.infrastructure.adapter.persistence.entity.UserEntity;
import univ.lille.infrastructure.adapter.persistence.mapper.UserEntityMapper;
import univ.lille.infrastructure.adapter.persistence.repository.UserJpaRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserEntityMapper mapper;

    @InjectMocks
    private UserRepositoryAdapter adapter;

    private User domainUser;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        domainUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .build();

        userEntity = UserEntity.builder()
                .id(1L)
                .email("test@test.com")
                .build();
    }


    @Test
    void save_should_map_domain_to_entity_and_back() {
        when(mapper.toEntity(domainUser)).thenReturn(userEntity);
        when(userJpaRepository.save(userEntity)).thenReturn(userEntity);
        when(mapper.toDomain(userEntity)).thenReturn(domainUser);

        User result = adapter.save(domainUser);

        assertThat(result).isSameAs(domainUser);

        verify(mapper).toEntity(domainUser);
        verify(userJpaRepository).save(userEntity);
        verify(mapper).toDomain(userEntity);
    }


    @Test
    void findById_should_return_mapped_user_when_found() {
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(domainUser);

        Optional<User> result = adapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(domainUser);

        verify(userJpaRepository).findById(1L);
        verify(mapper).toDomain(userEntity);
    }

    @Test
    void findById_should_return_empty_when_not_found() {
        when(userJpaRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = adapter.findById(1L);

        assertThat(result).isEmpty();
        verify(mapper, never()).toDomain(any());
    }


    @Test
    void findByEmail_should_use_findByEmailWithOrganization_and_map_to_domain() {
        when(userJpaRepository.findByEmailWithOrganization("test@test.com"))
                .thenReturn(Optional.of(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(domainUser);

        Optional<User> result = adapter.findByEmail("test@test.com");

        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(domainUser);

        verify(userJpaRepository).findByEmailWithOrganization("test@test.com");
        verify(mapper).toDomain(userEntity);
    }

    @Test
    void findByEmail_should_return_empty_when_not_found() {
        when(userJpaRepository.findByEmailWithOrganization("test@test.com"))
                .thenReturn(Optional.empty());

        Optional<User> result = adapter.findByEmail("test@test.com");

        assertThat(result).isEmpty();
        verify(mapper, never()).toDomain(any());
    }


    @Test
    void existsByEmail_should_delegate_to_jpa_repository() {
        when(userJpaRepository.existsByEmail("test@test.com")).thenReturn(true);

        boolean exists = adapter.existsByEmail("test@test.com");

        assertThat(exists).isTrue();
        verify(userJpaRepository).existsByEmail("test@test.com");
    }

    // -------------------- findAll() --------------------

    @Test
    void findAll_should_map_all_entities_to_domain() {
        UserEntity userEntity2 = UserEntity.builder().id(2L).email("u2@test.com").build();
        User domainUser2 = User.builder().id(2L).email("u2@test.com").build();

        when(userJpaRepository.findAll()).thenReturn(List.of(userEntity, userEntity2));
        when(mapper.toDomain(userEntity)).thenReturn(domainUser);
        when(mapper.toDomain(userEntity2)).thenReturn(domainUser2);

        List<User> result = adapter.findAll();

        assertThat(result).containsExactly(domainUser, domainUser2);

        verify(userJpaRepository).findAll();
        verify(mapper).toDomain(userEntity);
        verify(mapper).toDomain(userEntity2);
    }


    @Test
    void findByOrganizationId_should_map_entities_to_domain() {
        UserEntity userEntity2 = UserEntity.builder().id(2L).email("u2@test.com").build();
        User domainUser2 = User.builder().id(2L).email("u2@test.com").build();

        when(userJpaRepository.findByOrganization_Id(10L))
                .thenReturn(List.of(userEntity, userEntity2));
        when(mapper.toDomain(userEntity)).thenReturn(domainUser);
        when(mapper.toDomain(userEntity2)).thenReturn(domainUser2);

        List<User> result = adapter.findByOrganizationId(10L);

        assertThat(result).containsExactly(domainUser, domainUser2);

        verify(userJpaRepository).findByOrganization_Id(10L);
        verify(mapper).toDomain(userEntity);
        verify(mapper).toDomain(userEntity2);
    }


    @Test
    void findByResetPasswordToken_should_return_mapped_user_when_found() {
        when(userJpaRepository.findByResetPasswordToken("token"))
                .thenReturn(Optional.of(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(domainUser);

        Optional<User> result = adapter.findByResetPasswordToken("token");

        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(domainUser);

        verify(userJpaRepository).findByResetPasswordToken("token");
        verify(mapper).toDomain(userEntity);
    }

    @Test
    void findByResetPasswordToken_should_return_empty_when_not_found() {
        when(userJpaRepository.findByResetPasswordToken("token"))
                .thenReturn(Optional.empty());

        Optional<User> result = adapter.findByResetPasswordToken("token");

        assertThat(result).isEmpty();
        verify(mapper, never()).toDomain(any());
    }


    @Test
    void deleteUser_should_map_domain_to_entity_and_call_delete() {
        when(mapper.toEntity(domainUser)).thenReturn(userEntity);

        adapter.deleteUser(domainUser);

        verify(mapper).toEntity(domainUser);
        verify(userJpaRepository).delete(userEntity);
    }
}
