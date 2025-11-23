package univ.lille.infrastructure.adapter.persistence.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.domain.model.CustomRole;
import univ.lille.domain.model.Organization;
import univ.lille.domain.model.User;
import univ.lille.enums.UserRole;
import univ.lille.enums.UserStatus;
import univ.lille.infrastructure.adapter.persistence.entity.CustomRoleEntity;
import univ.lille.infrastructure.adapter.persistence.entity.OrganizationEntity;
import univ.lille.infrastructure.adapter.persistence.entity.UserEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEntityMapperTest {

    @Mock
    private OrganizationEntityMapper organizationEntityMapper;

    @Mock
    private CustomRoleEntityMapper customRoleEntityMapper;

    @InjectMocks
    private UserEntityMapper mapper;

    // ---------------------------------------------------------
    // TEST: toEntity(User domain)
    // ---------------------------------------------------------
    @Test
    void toEntity_should_map_all_fields_correctly() {

        LocalDateTime now = LocalDateTime.now();

        Organization org = new Organization();
        OrganizationEntity orgEntity = new OrganizationEntity();

        CustomRole role = new CustomRole();
        CustomRoleEntity roleEntity = new CustomRoleEntity();

        User domain = User.builder()
                .id(10L)
                .email("user@test.com")
                .password("pass")
                .fullName("John Doe")
                .firstName("John")
                .lastName("Doe")
                .loginCode("123456")
                .userStatus(UserStatus.ACTIVE)
                .role(UserRole.ADMIN)
                .organization(org)
                .customRole(role)
                .createdAt(now)
                .lastLoginAt(now.minusDays(1))
                .passwordResetToken("token")
                .passwordResetTokenExpiry(now.plusHours(1))
                .build();

        when(organizationEntityMapper.toEntity(org)).thenReturn(orgEntity);
        when(customRoleEntityMapper.toEntity(role)).thenReturn(roleEntity);

        UserEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getEmail()).isEqualTo("user@test.com");
        assertThat(entity.getPassword()).isEqualTo("pass");
        assertThat(entity.getFullName()).isEqualTo("John Doe");
        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getLastName()).isEqualTo("Doe");
        assertThat(entity.getLoginCode()).isEqualTo("123456");
        assertThat(entity.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(entity.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getLastLogin()).isEqualTo(now.minusDays(1));
        assertThat(entity.getResetPasswordToken()).isEqualTo("token");
        assertThat(entity.getResetPasswordTokenExpiry()).isEqualTo(now.plusHours(1));

        // Vérification des sous-mappers
        assertThat(entity.getOrganization()).isEqualTo(orgEntity);
        assertThat(entity.getCustomRole()).isEqualTo(roleEntity);

        verify(organizationEntityMapper).toEntity(org);
        verify(customRoleEntityMapper).toEntity(role);
    }

    @Test
    void toEntity_should_return_null_when_domain_is_null() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    // ---------------------------------------------------------
    // TEST: toDomain(UserEntity entity)
    // ---------------------------------------------------------
    @Test
    void toDomain_should_map_all_fields_correctly() {

        LocalDateTime now = LocalDateTime.now();

        OrganizationEntity orgEntity = new OrganizationEntity();
        Organization org = new Organization();

        CustomRoleEntity roleEntity = new CustomRoleEntity();
        CustomRole role = new CustomRole();

        UserEntity entity = UserEntity.builder()
                .id(20L)
                .email("entity@test.com")
                .password("encodedPass")
                .fullName("Jane Smith")
                .firstName("Jane")
                .lastName("Smith")
                .loginCode("999999")
                .status(UserStatus.PENDING)
                .role(UserRole.USER)
                .organization(orgEntity)
                .customRole(roleEntity)
                .createdAt(now.minusDays(2))
                .lastLogin(now.minusHours(5))
                .resetPasswordToken("reset")
                .resetPasswordTokenExpiry(now.plusHours(5))
                .build();

        when(organizationEntityMapper.toDomain(orgEntity)).thenReturn(org);
        when(customRoleEntityMapper.toDomain(roleEntity)).thenReturn(role);

        User domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(20L);
        assertThat(domain.getEmail()).isEqualTo("entity@test.com");
        assertThat(domain.getPassword()).isEqualTo("encodedPass");
        assertThat(domain.getFullName()).isEqualTo("Jane Smith");
        assertThat(domain.getFirstName()).isEqualTo("Jane");
        assertThat(domain.getLastName()).isEqualTo("Smith");
        assertThat(domain.getLoginCode()).isEqualTo("999999");
        assertThat(domain.getUserStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(domain.getRole()).isEqualTo(UserRole.USER);
        assertThat(domain.getCreatedAt()).isEqualTo(now.minusDays(2));
        assertThat(domain.getLastLoginAt()).isEqualTo(now.minusHours(5));
        assertThat(domain.getPasswordResetToken()).isEqualTo("reset");
        assertThat(domain.getPasswordResetTokenExpiry()).isEqualTo(now.plusHours(5));

        assertThat(domain.getOrganization()).isEqualTo(org);
        assertThat(domain.getCustomRole()).isEqualTo(role);

        verify(organizationEntityMapper).toDomain(orgEntity);
        verify(customRoleEntityMapper).toDomain(roleEntity);
    }

    @Test
    void toDomain_should_return_null_when_entity_is_null() {
        assertThat(mapper.toDomain(null)).isNull();
    }
}
