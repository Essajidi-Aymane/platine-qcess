package univ.lille.domain.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import univ.lille.enums.UserRole;
import univ.lille.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserTest {


    @Test
    void getDisplayName_should_return_fullName_for_admin_when_present() {
        User user = User.builder()
                .role(UserRole.ADMIN)
                .fullName("Admin Full Name")
                .email("admin@test.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        String displayName = user.getDisplayName();

        assertThat(displayName).isEqualTo("Admin Full Name");
    }

    @Test
    void getDisplayName_should_return_first_and_last_name_for_non_admin() {
        User user = User.builder()
                .role(UserRole.USER)
                .firstName("John")
                .lastName("Doe")
                .email("user@test.com")
                .build();

        String displayName = user.getDisplayName();

        assertThat(displayName).isEqualTo("John Doe");
    }

    @Test
    void getDisplayName_should_fallback_to_email_when_no_name() {
        User user = User.builder()
                .role(UserRole.USER)
                .email("user@test.com")
                .build();

        String displayName = user.getDisplayName();

        assertThat(displayName).isEqualTo("user@test.com");
    }


    @Test
    void isAdmin_should_return_true_when_role_is_admin() {
        User user = User.builder()
                .role(UserRole.ADMIN)
                .build();

        assertThat(user.isAdmin()).isTrue();
    }

    @Test
    void isAdmin_should_return_false_when_role_is_not_admin() {
        User user = User.builder()
                .role(UserRole.USER)
                .build();

        assertThat(user.isAdmin()).isFalse();
    }

    // ---------------------------------------------------

    @Test
    void isActive_should_return_true_when_status_is_active() {
        User user = User.builder()
                .userStatus(UserStatus.ACTIVE)
                .build();

        assertThat(user.isActive()).isTrue();
    }

    @Test
    void activate_should_set_status_to_active() {
        User user = User.builder()
                .userStatus(UserStatus.PENDING)
                .build();

        user.activate();

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.isActive()).isTrue();
    }


    @Test
    void updateLastLogin_should_set_lastLoginAt_to_now() {
        User user = new User();
        assertThat(user.getLastLoginAt()).isNull();

        user.updateLastLogin();

        assertThat(user.getLastLoginAt()).isNotNull();
        assertThat(user.getLastLoginAt()).isBeforeOrEqualTo(LocalDateTime.now());}


    @Test
    void assignRole_should_add_role_if_not_present() {
        CustomRole role = mock(CustomRole.class);
        when(role.getName()).thenReturn("MANAGER");

        User user = new User();

        user.assignRole(role);
        user.assignRole(role);

        assertThat(user.getCustomRoles()).hasSize(1);
        assertThat(user.getCustomRoles()).contains(role);
    }

    @Test
    void removeRole_should_remove_role_from_list() {
        CustomRole role = mock(CustomRole.class);
        when(role.getName()).thenReturn("MANAGER");

        User user = new User();
        user.assignRole(role);

        user.removeRole(role);

        assertThat(user.getCustomRoles()).doesNotContain(role);
    }

    @Test
    void hasRole_should_return_true_when_role_present_with_same_name_ignoring_case() {
        CustomRole roleInUser = mock(CustomRole.class);
        when(roleInUser.getName()).thenReturn("MANAGER");

        CustomRole roleToCheck = mock(CustomRole.class);
        when(roleToCheck.getName()).thenReturn("manager");

        User user = new User();
        user.assignRole(roleInUser);

        assertThat(user.hasRole(roleToCheck)).isTrue();
    }

    @Test
    void hasRole_should_return_false_when_role_not_present() {
        CustomRole roleInUser = mock(CustomRole.class);
        when(roleInUser.getName()).thenReturn("MANAGER");

        CustomRole otherRole = mock(CustomRole.class);
        when(otherRole.getName()).thenReturn("EMPLOYEE");

        User user = new User();
        user.assignRole(roleInUser);

        assertThat(user.hasRole(otherRole)).isFalse();
    }

    @Test
    void hasAnyAllowedRole_should_return_true_when_at_least_one_role_matches() {
        CustomRole role1 = mock(CustomRole.class);
        CustomRole role2 = mock(CustomRole.class);
        CustomRole role3 = mock(CustomRole.class);

        when(role1.getName()).thenReturn("ROLE_1");
        when(role2.getName()).thenReturn("ROLE_2");
        when(role3.getName()).thenReturn("ROLE_3");

        User user = new User();
        user.assignRole(role2);

        List<CustomRole> allowed = List.of(role2, role3);

        assertThat(user.hasAnyAllowedRole(allowed)).isTrue();
    }

    @Test
    void hasAnyAllowedRole_should_return_false_when_no_roles_match() {
        CustomRole roleUser = mock(CustomRole.class);
        CustomRole allowed1 = mock(CustomRole.class);
        CustomRole allowed2 = mock(CustomRole.class);

        when(roleUser.getName()).thenReturn("ROLE_USER");
        when(allowed1.getName()).thenReturn("ROLE_ADMIN");
        when(allowed2.getName()).thenReturn("ROLE_MANAGER");

        User user = new User();
        user.assignRole(roleUser);

        List<CustomRole> allowed = List.of(allowed1, allowed2);

        assertThat(user.hasAnyAllowedRole(allowed)).isFalse();
    }

    // ---------------------------------------------------
    // canAccessZone()
    // ---------------------------------------------------

    @Test
    void canAccessZone_should_delegate_to_zone_isAccessibleBy() {
        User user = new User();

        Zone zone = Mockito.mock(Zone.class);
        when(zone.isAccessibleBy(user)).thenReturn(true);

        boolean result = user.canAccessZone(zone);

        assertThat(result).isTrue();
        verify(zone).isAccessibleBy(user);
    }
}
