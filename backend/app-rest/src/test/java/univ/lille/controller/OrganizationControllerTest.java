package univ.lille.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import univ.lille.application.usecase.OrganizationUseCase;
import univ.lille.domain.port.in.CustomRolePort;
import univ.lille.domain.port.in.OrganizationManagementPort;
import univ.lille.dto.org.OrganizationUpdateRequest;
import univ.lille.dto.role.CreateCustomRoleRequest;
import univ.lille.dto.role.CustomRoleDTO;
import univ.lille.dto.role.UpdateCustomRoleRequest;
import univ.lille.infrastructure.adapter.security.QcessUserPrincipal;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationControllerTest {

    @Mock
    private OrganizationManagementPort organizationManagementPort;
    @Mock
    private CustomRolePort customRolePort;

    private OrganizationController controller;

    @BeforeEach
    void setUp() {
        controller = new OrganizationController(organizationManagementPort, customRolePort);
    }


    @Test
    void updateOrgDetails_should_call_usecase_and_return_ok() {
        // Given
        OrganizationUpdateRequest request = new OrganizationUpdateRequest();
        request.setName("New Name");
        request.setAddress("New Address");
        request.setDescription("Updated description");
        request.setPhoneNumber("0102030405");

        // When
        ResponseEntity<String> response = controller.updateOrgDetails(request);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Organization details updated successfully.");

        verify(organizationManagementPort, times(1))
                .updateOrganizationDetails(request);
    }
    @Test
    void getCustomRoles_should_return_roles_for_current_org() {
        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getOrganizationId()).thenReturn(3L);

        CustomRoleDTO role1 = CustomRoleDTO.builder()
                .id(1L)
                .name("Résident")
                .description("Locataire de la résidence")
                .build();

        CustomRoleDTO role2 = CustomRoleDTO.builder()
                .id(2L)
                .name("Gestionnaire")
                .description("Gère la résidence")
                .build();

        List<CustomRoleDTO> roles = List.of(role1, role2);

        // 🔑 utiliser anyLong() pour éviter les soucis de matching
        when(customRolePort.getCustomRolesByOrganization(anyLong()))
                .thenReturn(roles);

        ResponseEntity<List<CustomRoleDTO>> response =
                controller.getCustomRoles(principal);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).containsExactly(role1, role2);

        verify(customRolePort, times(1))
                .getCustomRolesByOrganization(3L);
    }

    // ----------------------------------------------------------
    // createCustomRole()
    // ----------------------------------------------------------
    @Test
    void createCustomRole_should_call_port_and_return_created_role() {
        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getId()).thenReturn(10L);
        when(principal.getOrganizationId()).thenReturn(5L);

        CreateCustomRoleRequest request = CreateCustomRoleRequest.builder()
                .name("Technicien")
                .description("Gère la maintenance")
                .build();


        CustomRoleDTO created = CustomRoleDTO.builder()
                .id(100L)
                .name("Technicien")
                .description("Gère la maintenance")
                .build();

        // 🔑 matcher les arguments avec any() / eq()
        when(customRolePort.createCustomRole(
                any(CreateCustomRoleRequest.class),
                anyLong(),
                anyLong()
        )).thenReturn(created);

        ResponseEntity<CustomRoleDTO> response =
                controller.createCustomRole(request, principal);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(created);

        verify(customRolePort, times(1))
                .createCustomRole(
                        eq(request),
                        eq(5L),
                        eq(10L)
                );
    }

    // ----------------------------------------------------------
    // updateCustomRole()
    // ----------------------------------------------------------
    @Test
    void updateCustomRole_should_call_port_and_return_updated_role() {
        Long roleId = 7L;

        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getId()).thenReturn(20L);
        when(principal.getOrganizationId()).thenReturn(4L);

        UpdateCustomRoleRequest request = UpdateCustomRoleRequest.builder()
                .name("Nouveau nom")
                .description("Nouvelle description")
                .build();


        CustomRoleDTO updated = CustomRoleDTO.builder()
                .id(roleId)
                .name("Nouveau nom")
                .description("Nouvelle description")
                .build();

        when(customRolePort.updateCustomRole(
                anyLong(),
                anyLong(),
                anyLong(),
                any(UpdateCustomRoleRequest.class)
        )).thenReturn(updated);

        ResponseEntity<CustomRoleDTO> response =
                controller.updateCustomRole(roleId, request, principal);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(updated);

        verify(customRolePort, times(1))
                .updateCustomRole(
                        eq(roleId),
                        eq(4L),
                        eq(20L),
                        eq(request)
                );
    }

    // ----------------------------------------------------------
    // deleteCustomRole()
    // ----------------------------------------------------------
    @Test
    void deleteCustomRole_should_call_port_and_return_ok_message() {
        Long roleId = 9L;

        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getId()).thenReturn(30L);
        when(principal.getOrganizationId()).thenReturn(6L);

        ResponseEntity<String> response =
                controller.deleteCustomRole(roleId, principal);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody())
                .isEqualTo("Custom role deleted successfully.");

        verify(customRolePort, times(1))
                .deleteCustomRole(9L, 6L, 30L);
    }

    @Test
    void getMyCustomRole_should_return_role_when_user_has_custom_role() {
        // Given
        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getId()).thenReturn(42L);
        when(principal.getOrganizationId()).thenReturn(3L);

        CustomRoleDTO dto = CustomRoleDTO.builder()
                .id(10L)
                .name("Résident")
                .description("Locataire de la résidence")
                .build();

        when(customRolePort.getCustomRoleForUser(42L, 3L))
                .thenReturn(dto);

        // When
        ResponseEntity<CustomRoleDTO> response =
                controller.getMyCustomRole(principal);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);

        verify(customRolePort, times(1))
                .getCustomRoleForUser(42L, 3L);
    }

    @Test
    void getMyCustomRole_should_return_no_content_when_user_has_no_custom_role() {
        // Given
        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getId()).thenReturn(42L);
        when(principal.getOrganizationId()).thenReturn(3L);

        when(customRolePort.getCustomRoleForUser(42L, 3L))
                .thenReturn(null);

        // When
        ResponseEntity<CustomRoleDTO> response =
                controller.getMyCustomRole(principal);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        assertThat(response.getBody()).isNull();

        verify(customRolePort, times(1))
                .getCustomRoleForUser(42L, 3L);
    }

}
