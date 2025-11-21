package univ.lille.domain.model;

import org.junit.jupiter.api.Test;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ZoneTest {


    @Test
    void isAccessibleBy_should_return_true_for_admin_user() {
        User admin = mock(User.class);
        when(admin.isAdmin()).thenReturn(true);

        Zone zone = new Zone();

        assertThat(zone.isAccessibleBy(admin)).isTrue();
    }

    @Test
    void isAccessibleBy_should_return_false_when_no_allowed_roles_and_user_not_admin() {
        User user = mock(User.class);
        when(user.isAdmin()).thenReturn(false);

        Zone zone = new Zone();

        assertThat(zone.isAccessibleBy(user)).isFalse();
    }

    @Test
    void isAccessibleBy_should_return_true_when_user_has_allowed_role() {
        // GIVEN
        CustomRole allowedRole = mock(CustomRole.class);
        CustomRole userRole = mock(CustomRole.class);

        when(allowedRole.getName()).thenReturn("MANAGER");
        when(userRole.getName()).thenReturn("MANAGER");

        User user = mock(User.class);
        when(user.isAdmin()).thenReturn(false);
        when(user.hasAnyAllowedRole(List.of(allowedRole))).thenReturn(true);

        Zone zone = new Zone();
        zone.addAllowedRole(allowedRole);

        boolean access = zone.isAccessibleBy(user);

        assertThat(access).isTrue();
    }

    @Test
    void isAccessibleBy_should_return_false_when_user_does_not_have_allowed_role() {
        CustomRole allowedRole = mock(CustomRole.class);
        when(allowedRole.getName()).thenReturn("MANAGER");

        User user = mock(User.class);
        when(user.isAdmin()).thenReturn(false);
        when(user.hasAnyAllowedRole(List.of(allowedRole))).thenReturn(false);

        Zone zone = new Zone();
        zone.addAllowedRole(allowedRole);

        assertThat(zone.isAccessibleBy(user)).isFalse();
    }


    @Test
    void addAllowedRole_should_add_role_only_once() {
        CustomRole role = mock(CustomRole.class);

        Zone zone = new Zone();

        zone.addAllowedRole(role);
        zone.addAllowedRole(role); // called twice

        assertThat(zone.getAllowedRoles()).hasSize(1);
    }


    @Test
    void removeAllowedRole_should_remove_role() {
        CustomRole role = mock(CustomRole.class);

        Zone zone = new Zone();
        zone.addAllowedRole(role);

        zone.removeAllowedRole(role);

        assertThat(zone.getAllowedRoles()).doesNotContain(role);
    }
}
