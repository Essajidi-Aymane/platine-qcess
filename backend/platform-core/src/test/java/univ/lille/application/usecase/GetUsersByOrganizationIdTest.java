package univ.lille.application.usecase;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.domain.exception.OrganizationNotFoundException;
import univ.lille.domain.model.Organization;
import univ.lille.domain.model.User;
import univ.lille.domain.port.out.OrganizationRepository;
import univ.lille.domain.port.out.UserRepository;
import univ.lille.dto.auth.user.UserDTO;
import univ.lille.enums.UserRole;
import org.mockito.ArgumentMatchers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetUsersByOrganizationIdTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private UserUseCase userUseCase;


    @Test
    void getUsersByOrganizationId_returnsUsersWhenOrgExists() {
        Long orgId = 2L;
        Organization org = Organization.builder()
                .id(orgId)
                .name("Test Org")
                .build();

        // l’org existe
        when(organizationRepository.existsById(orgId)).thenReturn(true);

        // users retournés par le repo
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

        // appel du use case
        List<UserDTO> result = userUseCase.getUsersByOrganizationId(orgId);

        // assertions
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("user1@test.fr");
        assertThat(result.get(1).getEmail()).isEqualTo("user2@test.fr");
        assertThat(result.get(0).getRole()).isEqualTo(UserRole.USER);
        assertThat(result.get(1).getRole()).isEqualTo(UserRole.USER);

        // vérification des appels
        verify(organizationRepository).existsById(orgId);
        verify(userRepository).findByOrganizationIdAndRole(orgId, UserRole.USER);
    }

    @Test
    void getUsersByOrganizationId_throwsWhenOrgDoesNotExist() {
        Long orgId = 99L;
        when(organizationRepository.existsById(orgId)).thenReturn(false);

        assertThrows(
                OrganizationNotFoundException.class,
                () -> userUseCase.getUsersByOrganizationId(orgId)
        );

        verify(organizationRepository).existsById(orgId);
        verify(userRepository, never()).findByOrganizationIdAndRole(anyLong(), any());
    }

}
