package univ.lille.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CustomRoleTest {

    @Test
    void should_create_custom_role_with_builder() {
        LocalDateTime now = LocalDateTime.now();
        
        CustomRole role = CustomRole.builder()
                .id(1L)
                .name("Manager")
                .description("Manager role with full access")
                .orgId(10L)
                .createdAt(now)
                .build();

        assertThat(role.getId()).isEqualTo(1L);
        assertThat(role.getName()).isEqualTo("Manager");
        assertThat(role.getDescription()).isEqualTo("Manager role with full access");
        assertThat(role.getOrgId()).isEqualTo(10L);
        assertThat(role.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void should_create_custom_role_with_no_args_constructor() {
        CustomRole role = new CustomRole();

        assertThat(role.getId()).isNull();
        assertThat(role.getName()).isNull();
        assertThat(role.getDescription()).isNull();
        assertThat(role.getOrgId()).isNull();
        assertThat(role.getCreatedAt()).isNull();
    }

    @Test
    void should_create_custom_role_with_all_args_constructor() {
        LocalDateTime now = LocalDateTime.now();
        
        CustomRole role = new CustomRole(1L, "Manager", "Manager role", 10L, now);

        assertThat(role.getId()).isEqualTo(1L);
        assertThat(role.getName()).isEqualTo("Manager");
        assertThat(role.getDescription()).isEqualTo("Manager role");
        assertThat(role.getOrgId()).isEqualTo(10L);
        assertThat(role.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void should_set_and_get_properties() {
        LocalDateTime now = LocalDateTime.now();
        CustomRole role = new CustomRole();

        role.setId(2L);
        role.setName("Employee");
        role.setDescription("Standard employee role");
        role.setOrgId(20L);
        role.setCreatedAt(now);

        assertThat(role.getId()).isEqualTo(2L);
        assertThat(role.getName()).isEqualTo("Employee");
        assertThat(role.getDescription()).isEqualTo("Standard employee role");
        assertThat(role.getOrgId()).isEqualTo(20L);
        assertThat(role.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void should_use_equals_and_hashcode_correctly() {
        LocalDateTime now = LocalDateTime.now();
        
        CustomRole role1 = CustomRole.builder()
                .id(1L)
                .name("Manager")
                .description("Manager role")
                .orgId(10L)
                .createdAt(now)
                .build();

        CustomRole role2 = CustomRole.builder()
                .id(1L)
                .name("Manager")
                .description("Manager role")
                .orgId(10L)
                .createdAt(now)
                .build();

        CustomRole role3 = CustomRole.builder()
                .id(2L)
                .name("Employee")
                .description("Employee role")
                .orgId(10L)
                .createdAt(now)
                .build();

        assertThat(role1).isEqualTo(role2);
        assertThat(role1.hashCode()).isEqualTo(role2.hashCode());
        assertThat(role1).isNotEqualTo(role3);
    }

    @Test
    void should_have_proper_toString() {
        CustomRole role = CustomRole.builder()
                .id(1L)
                .name("Manager")
                .description("Manager role")
                .orgId(10L)
                .build();

        String toString = role.toString();

        assertThat(toString).contains("Manager");
        assertThat(toString).contains("1");
        assertThat(toString).contains("10");
    }
}
