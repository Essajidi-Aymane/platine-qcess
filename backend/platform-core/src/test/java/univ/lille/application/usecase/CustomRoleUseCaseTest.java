package univ.lille.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import univ.lille.application.service.AuthenticationService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
public class CustomRoleUseCaseTest {


    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private CustomRoleRepository customRoleRepository;
    @Mock
    private UserRepository userRepository ;

    @InjectMocks
    private CustomRoleUseCase customRoleUseCase;

    private Organization buildOrg(Long id) {
        return Organization.builder()
                .id(id)
                .name("Org " + id)
                .address("Address " + id)
                .phone("000000000" + id)
                .description("Desc " + id)
                .build();
    }

    private User buildAdmin(Long id, Organization org) {
        return User.builder()
                .id(id)
                .email("admin" + id + "@example.com")
                .organization(org)
                .build();
    }

    private CustomRole buildRole(Long id, Organization org) {
        return CustomRole.builder()
                .id(id)
                .name("Role " + id)
                .description("Description " + id)
                .organization(org)
                .createdAt(LocalDateTime.now())
                .build();
    }
    @Test
    void createCustomRole_should_create_role_when_admin_in_org_and_name_not_used() {
        Long orgId = 1L;
        Long adminId = 10L;

        Organization org = buildOrg(orgId);
        User admin = buildAdmin(adminId, org);

        CreateCustomRoleRequest request = CreateCustomRoleRequest.builder()
                .name("Résident")
                .description("Locataire de la résidence")
                .build();


        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(customRoleRepository.existsByNameAndOrganizationId("Résident", orgId))
                .thenReturn(false);

        CustomRole savedRole = CustomRole.builder()
                .id(100L)
                .organization(org)
                .name("Résident")
                .description("Locataire de la résidence")
                .createdAt(LocalDateTime.now())
                .build();

        when(customRoleRepository.save(any(CustomRole.class))).thenReturn(savedRole);

        // WHEN
        CustomRoleDTO result =customRoleUseCase.createCustomRole(request, orgId, adminId);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getName()).isEqualTo("Résident");
        assertThat(result.getDescription()).isEqualTo("Locataire de la résidence");

        verify(organizationRepository).findById(orgId);
        verify(userRepository).findById(adminId);
        verify(customRoleRepository).existsByNameAndOrganizationId("Résident", orgId);
        verify(customRoleRepository).save(any(CustomRole.class));
    }

    // ----------------------------------------------------------
    // 2. createCustomRole() - ADMIN NOT IN ORG
    // ----------------------------------------------------------
    @Test
    void createCustomRole_should_throw_AccessDenied_when_admin_not_in_org() {
        Long orgId = 1L;
        Long adminId = 10L;

        Organization org = buildOrg(orgId);
        Organization otherOrg = buildOrg(2L);
        User admin = buildAdmin(adminId, otherOrg); // pas la même org

        CreateCustomRoleRequest request =  CreateCustomRoleRequest.builder()
                .name("Résident")
                .description("Locataire")
                .build();

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));

        // WHEN + THEN
        assertThatThrownBy(() ->
                customRoleUseCase.createCustomRole(request, orgId, adminId)
        )
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("does not belong to the specified organization");

        verify(customRoleRepository, never()).save(any());
    }

    // ----------------------------------------------------------
    // 3. createCustomRole() - NAME ALREADY EXISTS
    // ----------------------------------------------------------
    @Test
    void createCustomRole_should_throw_IllegalArgument_when_name_already_exists() {
        Long orgId = 1L;
        Long adminId = 10L;

        Organization org = buildOrg(orgId);
        User admin = buildAdmin(adminId, org);

        CreateCustomRoleRequest request = CreateCustomRoleRequest.builder()
                .name("Résident")
                .description("Locataire")
                .build();


        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(customRoleRepository.existsByNameAndOrganizationId("Résident", orgId))
                .thenReturn(true);

        assertThatThrownBy(() ->
                customRoleUseCase.createCustomRole(request, orgId, adminId)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(customRoleRepository, never()).save(any());
    }

    // ----------------------------------------------------------
    // 4. updateCustomRole() - SUCCESS
    // ----------------------------------------------------------
    @Test
    void updateCustomRole_should_update_fields_and_save() {
        Long orgId = 1L;
        Long roleId = 5L;
        Long adminId = 10L;

        Organization org = buildOrg(orgId);
        CustomRole existingRole = buildRole(roleId, org);

        UpdateCustomRoleRequest request = UpdateCustomRoleRequest.builder()
                .name("Nouveau nom")
                .description("Nouvelle description")
                .build();



        when(customRoleRepository.findByIdAndOrganizationId(roleId, orgId))
                .thenReturn(Optional.of(existingRole));
        when(customRoleRepository.save(any(CustomRole.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        CustomRoleDTO result =
                customRoleUseCase.updateCustomRole(roleId, orgId, adminId, request);

        // THEN
        assertThat(result.getName()).isEqualTo("Nouveau nom");
        assertThat(result.getDescription()).isEqualTo("Nouvelle description");

        verify(customRoleRepository).findByIdAndOrganizationId(roleId, orgId);
        verify(customRoleRepository).save(existingRole);
    }

    // ----------------------------------------------------------
    // 5. updateCustomRole() - ROLE NOT FOUND
    // ----------------------------------------------------------
    @Test
    void updateCustomRole_should_throw_when_role_not_found() {
        Long orgId = 1L;
        Long roleId = 5L;
        Long adminId = 10L;

        UpdateCustomRoleRequest request = UpdateCustomRoleRequest.builder()
                .name("Nouveau nom")
                .build();

        when(customRoleRepository.findByIdAndOrganizationId(roleId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                customRoleUseCase.updateCustomRole(roleId, orgId, adminId, request)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Custom role not found with ID");

        verify(customRoleRepository, never()).save(any());
    }

    // ----------------------------------------------------------
    // 6. deleteCustomRole() - SUCCESS
    // ----------------------------------------------------------
    @Test
    void deleteCustomRole_should_remove_role_from_users_and_delete() {
        Long orgId = 1L;
        Long adminId = 10L;
        Long roleId = 5L;

        Organization org = buildOrg(orgId);
        User admin = buildAdmin(adminId, org);
        CustomRole role = buildRole(roleId, org);

        User user1 = User.builder()
                .id(201L)
                .organization(org)
                .build();

        User user2 = User.builder()
                .id(202L)
                .organization(org)
                .build();

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(customRoleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(userRepository.findByOrganizationIdAndCustomRoleId(orgId, roleId))
                .thenReturn(List.of(user1, user2));

        // WHEN
        customRoleUseCase.deleteCustomRole(roleId, orgId, adminId);

        // THEN
        verify(userRepository).findByOrganizationIdAndCustomRoleId(orgId, roleId);
        verify(userRepository, times(2)).save(any(User.class));
        verify(customRoleRepository).delete(role);
        verify(organizationRepository).save(org);
    }

    // ----------------------------------------------------------
    // 7. getCustomRolesByOrganization() - SUCCESS
    // ----------------------------------------------------------
    @Test
    void getCustomRolesByOrganization_should_return_mapped_list() {
        Long orgId = 1L;
        Organization org = buildOrg(orgId);

        CustomRole role1 = buildRole(1L, org);
        CustomRole role2 = buildRole(2L, org);

        when(customRoleRepository.getCustomRolesByOrganizationId(orgId))
                .thenReturn(List.of(role1, role2));

        // WHEN
        List<CustomRoleDTO> result =
                customRoleUseCase.getCustomRolesByOrganization(orgId);

        // THEN
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(CustomRoleDTO::getId)
                .containsExactlyInAnyOrder(1L, 2L);

        verify(customRoleRepository).getCustomRolesByOrganizationId(orgId);
    }

}
