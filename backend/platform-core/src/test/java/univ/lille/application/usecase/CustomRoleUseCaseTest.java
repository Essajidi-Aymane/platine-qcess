package univ.lille.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.domain.exception.OrganizationNotFoundException;
import univ.lille.domain.exception.UserNotFoundException;
import univ.lille.domain.model.CustomRole;
import univ.lille.domain.model.Organization;
import univ.lille.domain.model.User;
import univ.lille.domain.port.out.CustomRoleRepository;
import univ.lille.domain.port.out.OrganizationRepository;
import univ.lille.domain.port.out.UserRepository;
import univ.lille.dto.role.CreateCustomRoleRequest;
import univ.lille.dto.role.CustomRoleDTO;
import univ.lille.dto.role.UpdateCustomRoleRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomRoleUseCaseTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomRoleRepository customRoleRepository;

    @InjectMocks
    private CustomRoleUseCase useCase;

    // ----------------------------------------------------------
    // createCustomRole
    // ----------------------------------------------------------

    @Test
    void createCustomRole_should_create_role_when_name_not_exists() {
        Long orgId = 1L;
        Long adminId = 10L; // pas utilisé dans la logique actuelle

        CreateCustomRoleRequest request =  CreateCustomRoleRequest.builder()
                .name("Résident")
                .description("Locataire de la résidence")
                .build();


        Organization org = Organization.builder()
                .id(orgId)
                .name("Résidence A")
                .build();

        CustomRole savedRole = CustomRole.builder()
                .id(100L)
                .name("Résident")
                .description("Locataire de la résidence")
                .orgId(org.getId())
                .createdAt(LocalDateTime.now())
                .build();

        when(organizationRepository.findById(orgId))
                .thenReturn(Optional.of(org));
        when(customRoleRepository.existsByNameAndOrganizationId("Résident", orgId))
                .thenReturn(false);
        when(customRoleRepository.save(any(CustomRole.class)))
                .thenReturn(savedRole);

        CustomRoleDTO result =
                useCase.createCustomRole(request, orgId, adminId);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getName()).isEqualTo("Résident");
        assertThat(result.getDescription()).isEqualTo("Locataire de la résidence");

        verify(organizationRepository).findById(orgId);
        verify(customRoleRepository).existsByNameAndOrganizationId("Résident", orgId);
        verify(customRoleRepository).save(any(CustomRole.class));
        verify(organizationRepository).save(org);
    }

    @Test
    void createCustomRole_should_throw_when_name_already_exists() {
        Long orgId = 1L;
        Long adminId = 10L;

        CreateCustomRoleRequest request =  CreateCustomRoleRequest.builder()
                .name("Résident")
                .description("Locataire de la résidence")
                .build();


        Organization org = Organization.builder()
                .id(orgId)
                .name("Résidence A")
                .build();

        when(organizationRepository.findById(orgId))
                .thenReturn(Optional.of(org));
        when(customRoleRepository.existsByNameAndOrganizationId("Résident", orgId))
                .thenReturn(true);

        assertThatThrownBy(() ->
                useCase.createCustomRole(request, orgId, adminId)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(customRoleRepository, never()).save(any());
    }

    @Test
    void createCustomRole_should_throw_when_org_not_found() {
        Long orgId = 99L;
        Long adminId = 10L;

        CreateCustomRoleRequest request = CreateCustomRoleRequest.builder()
                .name("Résident")
                .build();

        when(organizationRepository.findById(orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.createCustomRole(request, orgId, adminId)
        )
                .isInstanceOf(OrganizationNotFoundException.class)
                .hasMessageContaining("Organization not found with ID: " + orgId);

        verify(customRoleRepository, never()).save(any());
    }



    @Test
    void updateCustomRole_should_update_name_and_description() {
        Long orgId = 1L;
        Long roleId = 5L;
        Long adminId = 10L;

        UpdateCustomRoleRequest request =  UpdateCustomRoleRequest.builder()
                .name("Technicien")
                .description("Responsable de la maintenance")
                .build();


        CustomRole existing = CustomRole.builder()
                .id(roleId)
                .name("Ancien nom")
                .description("Ancienne description")
                .orgId(orgId)
                .build();

        CustomRole saved = CustomRole.builder()
                .id(roleId)
                .name("Technicien")
                .description("Responsable de la maintenance")
                .orgId(existing.getOrgId())
                .build();

        when(customRoleRepository.findByIdAndOrganizationId(roleId, orgId))
                .thenReturn(Optional.of(existing));
        when(customRoleRepository.save(existing))
                .thenReturn(saved);

        CustomRoleDTO result =
                useCase.updateCustomRole(roleId, orgId, adminId, request);

        assertThat(result.getId()).isEqualTo(roleId);
        assertThat(result.getName()).isEqualTo("Technicien");
        assertThat(result.getDescription()).isEqualTo("Responsable de la maintenance");

        verify(customRoleRepository).findByIdAndOrganizationId(roleId, orgId);
        verify(customRoleRepository).save(existing);
    }

    @Test
    void updateCustomRole_should_throw_when_role_not_found_in_org() {
        Long orgId = 1L;
        Long roleId = 5L;
        Long adminId = 10L;

        UpdateCustomRoleRequest request =  UpdateCustomRoleRequest.builder()
                .name("Technicien")
                .build();

        when(customRoleRepository.findByIdAndOrganizationId(roleId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.updateCustomRole(roleId, orgId, adminId, request)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Custom role not found with ID: " + roleId);

        verify(customRoleRepository, never()).save(any());
    }



    @Test
    void deleteCustomRole_should_remove_role_and_detach_from_users() {
        Long orgId = 1L;
        Long roleId = 5L;
        Long adminId = 10L;

        CustomRole role = CustomRole.builder()
                .id(roleId)
                .name("Résident")
                .orgId(orgId)
                .build();

        User user1 = User.builder()
                .id(100L)
                .email("user1@example.com")
                .customRole(role)
                .build();

        User user2 = User.builder()
                .id(101L)
                .email("user2@example.com")
                .customRole(role)
                .build();

        when(customRoleRepository.findByIdAndOrganizationId(roleId, orgId))
                .thenReturn(Optional.of(role));
        when(userRepository.findByOrganizationIdAndCustomRoleId(orgId, roleId))
                .thenReturn(List.of(user1, user2));

        useCase.deleteCustomRole(roleId, orgId, adminId);

        verify(userRepository, times(2)).save(any(User.class));
        verify(customRoleRepository).deleteById(roleId);
        verify(organizationRepository, never()).save(any());
    }

    @Test
    void deleteCustomRole_should_throw_when_role_not_in_org() {
        Long orgId = 1L;
        Long roleId = 5L;
        Long adminId = 10L;

        when(customRoleRepository.findByIdAndOrganizationId(roleId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.deleteCustomRole(roleId, orgId, adminId)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Custom role not found with ID: " + roleId + " in organization: " + orgId);

        verify(userRepository, never()).findByOrganizationIdAndCustomRoleId(anyLong(), anyLong());
        verify(customRoleRepository, never()).deleteById(roleId);
    }

    @Test
    void deleteCustomRole_should_throw_when_role_not_found() {
        Long orgId = 1L;
        Long roleId = 5L;
        Long adminId = 10L;

        when(customRoleRepository.findByIdAndOrganizationId(roleId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.deleteCustomRole(roleId, orgId, adminId)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Custom role not found with ID: " + roleId + " in organization: " + orgId);

        verify(customRoleRepository, never()).deleteById(roleId);
    }



    @Test
    void getCustomRolesByOrganization_should_return_mapped_list() {
        Long orgId = 1L;

        Organization org = Organization.builder()
                .id(orgId)
                .build();

        CustomRole r1 = CustomRole.builder()
                .id(10L)
                .name("Résident")
                .description("Locataire")
                .orgId(org.getId())
                .build();

        CustomRole r2 = CustomRole.builder()
                .id(11L)
                .name("Gestionnaire")
                .description("Gère la résidence")
                .orgId(org.getId())
                .build();

        when(customRoleRepository.getCustomRolesByOrganizationId(orgId))
                .thenReturn(List.of(r1, r2));

        List<CustomRoleDTO> result =
                useCase.getCustomRolesByOrganization(orgId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Résident");
        assertThat(result.get(1).getName()).isEqualTo("Gestionnaire");
    }

    // ----------------------------------------------------------
    // getCustomRoleForUser
    // ----------------------------------------------------------

    @Test
    void getCustomRoleForUser_should_return_role_when_user_has_one() {
        Long userId = 100L;
        Long orgId = 1L;

        Organization org = Organization.builder()
                .id(orgId)
                .name("Résidence A")
                .build();

        CustomRole role = CustomRole.builder()
                .id(10L)
                .name("Résident")
                .description("Locataire")
                .orgId(org.getId())
                .build();

        User user = User.builder()
                .id(userId)
                .email("user@example.com")
                .customRole(role)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        CustomRoleDTO result =
                useCase.getCustomRoleForUser(userId, orgId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Résident");
    }

    @Test
    void getCustomRoleForUser_should_return_null_when_user_has_no_role() {
        Long userId = 100L;
        Long orgId = 1L;

        User user = User.builder()
                .id(userId)
                .email("user@example.com")
                .customRole(null)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        CustomRoleDTO result =
                useCase.getCustomRoleForUser(userId, orgId);

        assertThat(result).isNull();
    }

    @Test
    void getCustomRoleForUser_should_throw_when_user_not_found() {
        Long userId = 100L;
        Long orgId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.getCustomRoleForUser(userId, orgId)
        )
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found with ID: " + userId);
    }
}
