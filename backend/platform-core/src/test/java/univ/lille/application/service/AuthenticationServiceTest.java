package univ.lille.application.service;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import univ.lille.domain.exception.UserNotFoundException;
import univ.lille.domain.model.User;
import univ.lille.domain.port.out.UserRepository;
import univ.lille.infrastructure.adapter.security.QcessUserPrincipal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private UserRepository userRepository;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authenticationService = new AuthenticationService(userRepository);

        SecurityContextHolder.clearContext();
    }


    @Test
    void getCurrentUserId_should_return_id_when_authenticated() {
        QcessUserPrincipal principal =
                new QcessUserPrincipal(5L, 10L, "test@mail.com", "pwd", null);

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(principal);

        SecurityContextHolder.getContext().setAuthentication(auth);

        Long result = authenticationService.getCurrentUserId();

        assertThat(result).isEqualTo(5L);
    }

    @Test
    void getCurrentUserId_should_throw_when_not_authenticated() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> authenticationService.getCurrentUserId())
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Aucun utilisateur connecté");
    }


    @Test
    void getCurrentUserOrganizationId_should_return_orgId() {
        QcessUserPrincipal principal =
                new QcessUserPrincipal(5L, 99L, "test@mail.com", "pwd", null);

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(principal);

        SecurityContextHolder.getContext().setAuthentication(auth);

        Long result = authenticationService.getCurrentUserOrganizationId();

        assertThat(result).isEqualTo(99L);
    }

    @Test
    void getCurrentUserOrganizationId_should_throw_when_not_authenticated() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> authenticationService.getCurrentUserOrganizationId())
                .isInstanceOf(UserNotFoundException.class);
    }

    // ---------------------------------------------
    // getCurrentUserEmail()
    // ---------------------------------------------
    @Test
    void getCurrentUserEmail_should_return_email_when_authenticated() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("email@test.com");

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThat(authenticationService.getCurrentUserEmail())
                .isEqualTo("email@test.com");
    }

    @Test
    void getCurrentUserEmail_should_return_null_when_not_authenticated() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThat(authenticationService.getCurrentUserEmail())
                .isNull();
    }

    // ---------------------------------------------
    // getCurrentUser()
    // ---------------------------------------------
    @Test
    void getCurrentUser_should_return_user_from_repository() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("email@test.com");
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = User.builder().email("email@test.com").build();

        when(userRepository.findByEmail("email@test.com"))
                .thenReturn(java.util.Optional.of(user));

        User returnedUser = authenticationService.getCurrentUser();

        assertThat(returnedUser).isSameAs(user);
    }

    @Test
    void getCurrentUser_should_throw_when_email_null() {
        assertThatThrownBy(() -> authenticationService.getCurrentUser())
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getCurrentUser_should_throw_when_user_not_found() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("email@test.com");

        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findByEmail("email@test.com"))
                .thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> authenticationService.getCurrentUser())
                .isInstanceOf(UserNotFoundException.class);
    }
}
