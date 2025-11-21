package univ.lille.infrastructure.adapter.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import univ.lille.enums.UserRole;
import univ.lille.infrastructure.adapter.persistence.entity.OrganizationEntity;
import univ.lille.infrastructure.adapter.persistence.entity.UserEntity;
import univ.lille.infrastructure.adapter.persistence.repository.UserJpaRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private UserJpaRepository userJpaRepository;
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        userJpaRepository = mock(UserJpaRepository.class);
        customUserDetailsService = new CustomUserDetailsService(userJpaRepository);
    }

    // -----------------------------------------
    // Case: user FOUND
    // -----------------------------------------
    @Test
    void loadUserByUsername_should_return_QcessUserPrincipal_when_user_exists() {
        // GIVEN
        OrganizationEntity org = new OrganizationEntity();
        org.setId(10L);

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("test@domain.com");
        user.setPassword("hashed");
        user.setRole(UserRole.ADMIN);
        user.setOrganization(org);

        when(userJpaRepository.findByEmailWithOrganization("test@domain.com"))
                .thenReturn(Optional.of(user));

        // WHEN
        var result = customUserDetailsService.loadUserByUsername("test@domain.com");

        // THEN
        assertThat(result).isInstanceOf(QcessUserPrincipal.class);
        QcessUserPrincipal principal = (QcessUserPrincipal) result;

        assertThat(principal.getId()).isEqualTo(1L);
        assertThat(principal.getOrganizationId()).isEqualTo(10L);
        assertThat(principal.getUsername()).isEqualTo("test@domain.com");
        assertThat(principal.getPassword()).isEqualTo("hashed");

        assertThat(principal.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");

        verify(userJpaRepository).findByEmailWithOrganization("test@domain.com");
    }

    // -----------------------------------------
    // Case: user NOT found
    // -----------------------------------------
    @Test
    void loadUserByUsername_should_throw_exception_when_user_not_found() {
        when(userJpaRepository.findByEmailWithOrganization("missing@domain.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                customUserDetailsService.loadUserByUsername("missing@domain.com")
        )
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email");

        verify(userJpaRepository).findByEmailWithOrganization("missing@domain.com");
    }

    // -----------------------------------------
    // Case: null password → empty password
    // -----------------------------------------
    @Test
    void loadUserByUsername_should_set_empty_password_when_null() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("test@domain.com");
        user.setPassword(null);
        user.setRole(UserRole.USER);

        when(userJpaRepository.findByEmailWithOrganization("test@domain.com"))
                .thenReturn(Optional.of(user));

        var details = customUserDetailsService.loadUserByUsername("test@domain.com");

        assertThat(details.getPassword()).isEqualTo("");
    }

    // -----------------------------------------
    // Case: no organization → orgId = null
    // -----------------------------------------
    @Test
    void loadUserByUsername_should_set_orgId_null_when_no_organization() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("test@domain.com");
        user.setPassword("pass");
        user.setRole(UserRole.USER);
        user.setOrganization(null);

        when(userJpaRepository.findByEmailWithOrganization("test@domain.com"))
                .thenReturn(Optional.of(user));

        QcessUserPrincipal result =
                (QcessUserPrincipal) customUserDetailsService.loadUserByUsername("test@domain.com");

        assertThat(result.getOrganizationId()).isNull();
    }
}
