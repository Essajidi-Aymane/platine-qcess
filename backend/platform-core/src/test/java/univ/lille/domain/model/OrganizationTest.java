package univ.lille.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrganizationTest {


    @Test
    void addUser_should_add_user_and_set_organization_on_user() {
        Organization org = new Organization();
        User user = new User();

        org.addUser(user);

        // Vérifie que l'utilisateur est ajouté
        assertThat(org.getUsers()).containsExactly(user);

        // Vérifie que la relation inverse est mise à jour
        assertThat(user.getOrganization()).isEqualTo(org);
    }

    @Test
    void addUser_should_add_multiple_users() {
        Organization org = new Organization();
        User u1 = new User();
        User u2 = new User();

        org.addUser(u1);
        org.addUser(u2);

        assertThat(org.getUsers()).containsExactly(u1, u2);
    }


    @Test
    void addZone_should_add_zone_and_set_organization_on_zone() {
        Organization org = new Organization();
        Zone zone = mock(Zone.class);

        org.addZone(zone);

        assertThat(org.getZones()).containsExactly(zone);
        verify(zone).setOrgId(org.getId());
    }

    @Test
    void addZone_should_add_multiple_zones() {
        Organization org = new Organization();
        Zone z1 = mock(Zone.class);
        Zone z2 = mock(Zone.class);

        org.addZone(z1);
        org.addZone(z2);

        assertThat(org.getZones()).containsExactly(z1, z2);

        verify(z1).setOrgId(org.getId());
        verify(z2).setOrgId(org.getId());
    }


    @Test
    void addCustomRole_should_add_role_and_set_organization_on_role() {
        Organization org = new Organization();
        CustomRole role = mock(CustomRole.class);

        org.addCustomRole(role);

        assertThat(org.getCustomRoles()).containsExactly(role);
        verify(role).setOrganization(org);
    }

    @Test
    void addCustomRole_should_add_multiple_roles() {
        Organization org = new Organization();
        CustomRole r1 = mock(CustomRole.class);
        CustomRole r2 = mock(CustomRole.class);

        org.addCustomRole(r1);
        org.addCustomRole(r2);

        assertThat(org.getCustomRoles()).containsExactly(r1, r2);

        verify(r1).setOrganization(org);
        verify(r2).setOrganization(org);
    }
}
