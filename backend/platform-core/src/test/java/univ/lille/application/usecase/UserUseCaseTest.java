package univ.lille.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.application.service.AuthenticationService;
import univ.lille.application.utils.NameUtils;
import univ.lille.domain.exception.CustomRoleException;
import univ.lille.domain.exception.EmailAlreadyExistsException;
import univ.lille.domain.exception.OrganizationNotFoundException;
import univ.lille.domain.exception.UserNotFoundException;
import univ.lille.domain.model.CustomRole;
import univ.lille.domain.model.Organization;
import univ.lille.domain.model.User;
import univ.lille.domain.port.out.CustomRoleRepository;
import univ.lille.domain.port.out.EmailPort;
import univ.lille.domain.port.out.NotificationPort;
import univ.lille.domain.port.out.OrganizationRepository;
import univ.lille.domain.port.out.UserRepository;
import univ.lille.dto.auth.user.CreateUserRequest;
import univ.lille.dto.auth.user.UserDTO;
import univ.lille.enums.UserRole;
import univ.lille.enums.UserStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private EmailPort emailPort;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private CustomRoleRepository customRoleRepository;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private UserUseCase userUseCase;

    // ========================================
    // CREATE USER TESTS
    // ========================================

    @Test
    void createUser_should_create_user_and_send_email_when_data_is_valid() {
        Long orgId = 1L;

        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("user@test.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setRole(UserRole.USER);

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
                .organization(org)
                .role(UserRole.USER)
                .userStatus(UserStatus.PENDING)
                .loginCode("123456")
                .build();

        when(authenticationService.getCurrentUserOrganizationId()).thenReturn(orgId);
        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDTO result = userUseCase.createUser(request);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("user@test.com");

        verify(userRepository).save(any(User.class));
        verify(emailPort).sendWelcomeEmail(
                eq("user@test.com"),
                eq(fullName),
                anyString()
        );
    }

    @Test
    void createUser_should_throw_when_organization_not_found() {
        Long orgId = 999L;

        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("user@test.com");
        request.setFirstName("John");
        request.setLastName("Doe");

        when(authenticationService.getCurrentUserOrganizationId()).thenReturn(orgId);
        when(organizationRepository.findById(orgId)).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> userUseCase.createUser(request));

        assertThat(thrown)
                .isInstanceOf(OrganizationNotFoundException.class);

        verify(userRepository, never()).save(any());
        verify(emailPort, never()).sendWelcomeEmail(any(), any(), any());
    }

    @Test
    void createUser_should_throw_when_email_already_exists() {
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

        Throwable thrown = catchThrowable(() -> userUseCase.createUser(request));

        assertThat(thrown)
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
        verify(emailPort, never()).sendWelcomeEmail(any(), any(), any());
    }

    // ========================================
    // GET USERS BY ORGANIZATION TESTS
    // ========================================

    @Test
    void getUsersByOrganizationId_should_return_users_when_org_exists() {
        Long orgId = 2L;
        Organization org = Organization.builder()
                .id(orgId)
                .name("Test Org")
                .build();

        when(organizationRepository.existsById(orgId)).thenReturn(true);

        User user1 = User.builder()
                .id(1L)
                .email("user1@test.fr")
                .role(UserRole.USER)
                .organization(org)
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("user2@test.fr")
                .organization(org)
                .role(UserRole.USER)
                .build();

        when(userRepository.findByOrganizationIdAndRole(orgId, UserRole.USER))
                .thenReturn(List.of(user1, user2));

        List<UserDTO> result = userUseCase.getUsersByOrganizationId(orgId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("user1@test.fr");
        assertThat(result.get(1).getEmail()).isEqualTo("user2@test.fr");
        assertThat(result.get(0).getRole()).isEqualTo(UserRole.USER);
        assertThat(result.get(1).getRole()).isEqualTo(UserRole.USER);

        verify(organizationRepository).existsById(orgId);
        verify(userRepository).findByOrganizationIdAndRole(orgId, UserRole.USER);
    }

    @Test
    void getUsersByOrganizationId_should_throw_when_org_does_not_exist() {
        Long orgId = 99L;
        when(organizationRepository.existsById(orgId)).thenReturn(false);

        assertThatThrownBy(() -> userUseCase.getUsersByOrganizationId(orgId))
                .isInstanceOf(OrganizationNotFoundException.class);

        verify(organizationRepository).existsById(orgId);
        verify(userRepository, never()).findByOrganizationIdAndRole(anyLong(), any());
    }

    // ========================================
    // ACTIVATE USER TESTS
    // ========================================

    @Test
    void activateUser_should_activate_suspended_user() {
        Long userId = 5L;
        Long orgId = 1L;

        User user = User.builder()
                .id(userId)
                .email("user@test.com")
                .userStatus(UserStatus.SUSPENDED)
                .build();

        when(userRepository.findByIdAndOrganizationId(userId, orgId))
                .thenReturn(Optional.of(user));

        userUseCase.activateUser(userId, orgId);

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        verify(userRepository).save(user);
    }

    @Test
    void activateUser_should_do_nothing_when_user_is_deleted() {
        Long userId = 5L;
        Long orgId = 1L;

        User user = User.builder()
                .id(userId)
                .email("user@test.com")
                .userStatus(UserStatus.DELETED)
                .build();

        when(userRepository.findByIdAndOrganizationId(userId, orgId))
                .thenReturn(Optional.of(user));

        userUseCase.activateUser(userId, orgId);

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.DELETED);
        verify(userRepository, never()).save(any());
    }

    @Test
    void activateUser_should_throw_when_user_not_found() {
        Long userId = 999L;
        Long orgId = 1L;

        when(userRepository.findByIdAndOrganizationId(userId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userUseCase.activateUser(userId, orgId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("n'existe pas dans cette organisation");
    }

    // ========================================
    // SUSPEND USER TESTS
    // ========================================

    @Test
    void suspendUser_should_suspend_active_user() {
        Long userId = 5L;
        Long orgId = 1L;

        User user = User.builder()
                .id(userId)
                .email("user@test.com")
                .userStatus(UserStatus.ACTIVE)
                .build();

        when(userRepository.findByIdAndOrganizationId(userId, orgId))
                .thenReturn(Optional.of(user));

        userUseCase.suspendUser(userId, orgId);

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.SUSPENDED);
        verify(userRepository).save(user);
    }

    @Test
    void suspendUser_should_throw_when_user_not_found() {
        Long userId = 999L;
        Long orgId = 1L;

        when(userRepository.findByIdAndOrganizationId(userId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userUseCase.suspendUser(userId, orgId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("n'existe pas dans cette organisation");
    }

    // ========================================
    // ASSIGN CUSTOM ROLE TESTS
    // ========================================

    @Test
    void assignCustomRoleToUsers_should_assign_role_to_users() {
        Long roleId = 10L;
        Long orgId = 1L;
        List<Long> userIds = List.of(1L, 2L);

        CustomRole role = CustomRole.builder()
                .id(roleId)
                .name("Manager")
                .orgId(orgId)
                .build();

        User user1 = User.builder()
                .id(1L)
                .email("user1@test.com")
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("user2@test.com")
                .build();

        when(customRoleRepository.findByIdAndOrganizationId(roleId, orgId))
                .thenReturn(Optional.of(role));
        when(userRepository.findByIdInAndOrganizationId(userIds, orgId))
                .thenReturn(List.of(user1, user2));

        userUseCase.assignCustomRoleToUsers(roleId, userIds, orgId);

        assertThat(user1.getCustomRole()).isEqualTo(role);
        assertThat(user2.getCustomRole()).isEqualTo(role);
        verify(userRepository).saveAll(List.of(user1, user2));
    }

    @Test
    void assignCustomRoleToUsers_should_throw_when_role_not_found() {
        Long roleId = 999L;
        Long orgId = 1L;
        List<Long> userIds = List.of(1L, 2L);

        when(customRoleRepository.findByIdAndOrganizationId(roleId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userUseCase.assignCustomRoleToUsers(roleId, userIds, orgId))
                .isInstanceOf(CustomRoleException.class)
                .hasMessageContaining("Custom role not found");

        verify(userRepository, never()).saveAll(any());
    }

    @Test
    void assignCustomRoleToUsers_should_throw_when_some_users_not_found() {
        Long roleId = 10L;
        Long orgId = 1L;
        List<Long> userIds = List.of(1L, 2L, 3L);

        CustomRole role = CustomRole.builder()
                .id(roleId)
                .name("Manager")
                .orgId(orgId)
                .build();

        User user1 = User.builder()
                .id(1L)
                .email("user1@test.com")
                .build();

        when(customRoleRepository.findByIdAndOrganizationId(roleId, orgId))
                .thenReturn(Optional.of(role));
        when(userRepository.findByIdInAndOrganizationId(userIds, orgId))
                .thenReturn(List.of(user1)); // Only 1 user found instead of 3

        assertThatThrownBy(() -> userUseCase.assignCustomRoleToUsers(roleId, userIds, orgId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Some users do not belong to the organization");

        verify(userRepository, never()).saveAll(any());
    }

    // ========================================
    // UNASSIGN CUSTOM ROLE TESTS
    // ========================================

    @Test
    void unassignCustomRoleFromUsers_should_remove_role_from_users() {
        Long roleId = 10L;
        Long orgId = 1L;
        Long adminId = 100L;
        List<Long> userIds = List.of(1L, 2L);

        CustomRole role = CustomRole.builder()
                .id(roleId)
                .name("Manager")
                .orgId(orgId)
                .build();

        User user1 = User.builder()
                .id(1L)
                .email("user1@test.com")
                .customRole(role)
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("user2@test.com")
                .customRole(role)
                .build();

        when(customRoleRepository.findByIdAndOrganizationId(roleId, orgId))
                .thenReturn(Optional.of(role));
        when(userRepository.findByIdInAndOrganizationId(userIds, orgId))
                .thenReturn(List.of(user1, user2));

        userUseCase.unassignCustomRoleFromUsers(roleId, userIds, orgId, adminId);

        assertThat(user1.getCustomRole()).isNull();
        assertThat(user2.getCustomRole()).isNull();
        verify(userRepository).saveAll(List.of(user1, user2));
    }

    @Test
    void unassignCustomRoleFromUsers_should_throw_when_role_not_found() {
        Long roleId = 999L;
        Long orgId = 1L;
        Long adminId = 100L;
        List<Long> userIds = List.of(1L, 2L);

        when(customRoleRepository.findByIdAndOrganizationId(roleId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> 
                userUseCase.unassignCustomRoleFromUsers(roleId, userIds, orgId, adminId))
                .isInstanceOf(CustomRoleException.class)
                .hasMessageContaining("Custom role not found");

        verify(userRepository, never()).saveAll(any());
    }
}
