package univ.lille.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import univ.lille.domain.exception.EmailAlreadyExistsException;
import univ.lille.domain.model.Organization;
import univ.lille.domain.model.User;
import univ.lille.domain.port.out.EmailPort;
import univ.lille.domain.port.out.OrganizationRepository;
import univ.lille.domain.port.out.UserRepository;
import univ.lille.dto.auth.AuthResponse;
import univ.lille.dto.auth.RegisterRequest;
import univ.lille.enums.UserRole;
import univ.lille.infrastructure.adapter.security.JwtService;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterAdminUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private EmailPort emailPort;

    @InjectMocks
    private RegisterAdminUseCase registerAdminUseCase;

    @Test
    void register_should_create_org_and_admin_and_return_auth_response() {
        // GIVEN
        RegisterRequest request = new RegisterRequest();
        request.setEmail("admin@test.com");
        request.setPassword("Secret123");
        request.setFullName("John Doe");
        request.setOrganizationName("My Organization");

        // l'email n'existe pas encore
        when(userRepository.existsByEmail("admin@test.com")).thenReturn(false);

        // organisation sauvegardée avec un id
        Organization savedOrg = Organization.builder()
                .id(10L)
                .name("My Organization")
                .build();
        when(organizationRepository.save(any(Organization.class))).thenReturn(savedOrg);

        // encodage mot de passe
        when(passwordEncoder.encode("Secret123")).thenReturn("encodedPassword");

        // user admin sauvegardé avec un id
        User savedAdmin = User.builder()
                .id(1L)
                .email("admin@test.com")
                .password("encodedPassword")
                .fullName("John Doe")
                .firstName("John")
                .lastName("Doe")
                .organization(savedOrg)
                .role(UserRole.ADMIN)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedAdmin);

        // token JWT généré
        when(jwtService.generateToken(savedAdmin)).thenReturn("jwt-token");

        // WHEN
        AuthResponse response = registerAdminUseCase.register(request);

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("admin@test.com");
        assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(response.getOrganisationId()).isEqualTo(10L);
        assertThat(response.getFullName()).isEqualTo("John Doe");

        ArgumentCaptor<Organization> orgCaptor = ArgumentCaptor.forClass(Organization.class);
        verify(organizationRepository).save(orgCaptor.capture());
        Organization orgToSave = orgCaptor.getValue();
        assertThat(orgToSave.getName()).isEqualTo("My Organization");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User adminToSave = userCaptor.getValue();
        assertThat(adminToSave.getEmail()).isEqualTo("admin@test.com");
        assertThat(adminToSave.getPassword()).isEqualTo("encodedPassword");
        assertThat(adminToSave.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(adminToSave.getOrganization()).isEqualTo(savedOrg);

        verify(emailPort).sendAdminWelcomeEmail(
                eq("admin@test.com"),
                eq("John Doe"),
                eq("My Organization")
        );
    }

    @Test
    void register_should_throw_when_email_already_exists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@test.com");
        request.setPassword("Secret123");
        request.setFullName("John Doe");
        request.setOrganizationName("My Organization");

        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        Throwable thrown = catchThrowable(() -> registerAdminUseCase.register(request));

        assertThat(thrown)
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("existing@test.com");

        verify(organizationRepository, never()).save(any());
        verify(userRepository, never()).save(any());
        verify(emailPort, never()).sendAdminWelcomeEmail(any(), any(), any());
        verify(jwtService, never()).generateToken(any());
    }
}
