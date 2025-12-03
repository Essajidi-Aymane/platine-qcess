package univ.lille.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.application.service.AuthenticationService;
import univ.lille.application.utils.NameUtils;
import univ.lille.domain.exception.EmailAlreadyExistsException;
import univ.lille.domain.exception.OrganizationNotFoundException;
import univ.lille.domain.model.Organization;
import univ.lille.domain.model.User;
import univ.lille.domain.port.out.EmailPort;
import univ.lille.domain.port.out.OrganizationRepository;
import univ.lille.domain.port.out.UserRepository;
import univ.lille.dto.auth.user.CreateUserRequest;
import univ.lille.dto.auth.user.UserDTO;
import univ.lille.application.usecase.mapper.UserMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private EmailPort emailPort;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    @Test
    void createUser_shouldCreateUserAndSendEmail_whenDataIsValid() {
        // GIVEN
        Long orgId = 1L;

        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("user@test.com");
        request.setFirstName("John");
        request.setLastName("Doe");

        Organization org = Organization.builder()
                .id(orgId)
                .name("Org 1")
                .build();

        String fullName = NameUtils.buildFullName(
                request.getFirstName(),
                request.getLastName()
        );

        User savedUser = User.builder()
                .id(10L)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .fullName(fullName)
                .organization(org) // IMPORTANT pour éviter le NPE dans UserMapper.toDTO
                .build();

        UserDTO expectedDto = new UserDTO();
        expectedDto.setId(10L);
        expectedDto.setEmail(request.getEmail());
        expectedDto.setFirstName(request.getFirstName());
        expectedDto.setLastName(request.getLastName());
        expectedDto.setFullName(fullName);

        // Stubs
        when(authenticationService.getCurrentUserOrganizationId()).thenReturn(orgId);
        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(expectedDto);

        // WHEN
        UserDTO result = createUserUseCase.createUser(request);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("user@test.com");

        // Vérifier qu'on sauvegarde bien l'utilisateur
        verify(userRepository).save(any(User.class));

        // Vérifier qu'on envoie bien l'email de bienvenue
        verify(emailPort).sendWelcomeEmail(
                eq("user@test.com"),
                eq(fullName),
                anyString() // loginCode généré
        );
    }

    @Test
    void createUser_shouldThrow_whenOrganizationNotFound() {
        // GIVEN
        Long orgId = 999L;

        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("user@test.com");
        request.setFirstName("John");
        request.setLastName("Doe");

        when(authenticationService.getCurrentUserOrganizationId()).thenReturn(orgId);
        when(organizationRepository.findById(orgId)).thenReturn(Optional.empty());

        // WHEN
        Throwable thrown = catchThrowable(() -> createUserUseCase.createUser(request));

        // THEN
        assertThat(thrown)
                .isInstanceOf(OrganizationNotFoundException.class);

        verify(userRepository, never()).save(any());
        verify(emailPort, never()).sendWelcomeEmail(any(), any(), any());
    }

    @Test
    void createUser_shouldThrow_whenEmailAlreadyExists() {
        // GIVEN
        Long orgId = 1L;

        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("existing@test.com");
        request.setFirstName("John");
        request.setLastName("Doe");

        Organization org = Organization.builder()
                .id(orgId)
                .name("Org 1")
                .build();

        when(authenticationService.getCurrentUserOrganizationId()).thenReturn(orgId);
        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // WHEN
        Throwable thrown = catchThrowable(() -> createUserUseCase.createUser(request));

        // THEN
        assertThat(thrown)
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
        verify(emailPort, never()).sendWelcomeEmail(any(), any(), any());
    }
}
