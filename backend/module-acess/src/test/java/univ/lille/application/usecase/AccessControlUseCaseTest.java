package univ.lille.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.domain.exception.UserNotFoundException;
import univ.lille.domain.exception.ZoneNotFoundException;
import univ.lille.domain.model.AccessLog;
import univ.lille.domain.model.CustomRole;
import univ.lille.domain.model.User;
import univ.lille.domain.model.Zone;
import univ.lille.domain.port.out.AccessLogNotificationPort;
import univ.lille.domain.port.out.AccessLogRepository;
import univ.lille.domain.port.out.UserRepository;
import univ.lille.domain.port.out.ZoneRepository;
import univ.lille.dto.access.AccessResponseDTO;
import univ.lille.enums.ZoneStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessControlUseCaseTest {

    @Mock
    private ZoneRepository zoneRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccessLogRepository accessLogRepository;
    @Mock 
    private AccessLogNotificationPort notifictionPort; 

    @InjectMocks
    private AccessControlUseCase accessControlUseCase;

    @Test
    void validateAccess_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> accessControlUseCase.validateAccess(1L, 1L));
        verifyNoInteractions(zoneRepository, accessLogRepository);
    }

    @Test
    void validateAccess_ZoneNotFound_ThrowsException() {
        User user = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(zoneRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ZoneNotFoundException.class, () -> accessControlUseCase.validateAccess(1L, 1L));
        verifyNoInteractions(accessLogRepository);
    }

    @Test
    void validateAccess_ZoneInactive_AccessDenied() {
        User user = User.builder().id(1L).build();
        Zone zone = Zone.builder().id(1L).status(ZoneStatus.INACTIVE).orgId(10L).name("Zone A").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(zone));

        AccessResponseDTO response = accessControlUseCase.validateAccess(1L, 1L);

        assertFalse(response.isGranted());
        assertEquals("ZONE_INACTIVE", response.getReason());
        verify(accessLogRepository).save(any(AccessLog.class));
    }

    @Test
    void validateAccess_PublicZone_AccessGranted() {
        User user = User.builder().id(1L).build();
        // allowedRoleIds is null or empty
        Zone zone = Zone.builder().id(1L).status(ZoneStatus.ACTIVE).orgId(10L).name("Zone A").allowedRoleIds(null).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(zone));

        AccessResponseDTO response = accessControlUseCase.validateAccess(1L, 1L);

        assertTrue(response.isGranted());
        assertEquals("PUBLIC_ZONE", response.getReason());
        verify(accessLogRepository).save(any(AccessLog.class));
    }

    @Test
    void validateAccess_AuthorizedRole_AccessGranted() {
        CustomRole role = CustomRole.builder().id(5L).build();
        User user = User.builder().id(1L).customRole(role).build();
        Zone zone = Zone.builder()
                .id(1L)
                .status(ZoneStatus.ACTIVE)
                .orgId(10L)
                .name("Zone A")
                .allowedRoleIds(List.of(5L))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(zone));

        AccessResponseDTO response = accessControlUseCase.validateAccess(1L, 1L);

        assertTrue(response.isGranted());
        assertEquals("AUTHORIZED_ROLE", response.getReason());
        verify(accessLogRepository).save(any(AccessLog.class));
    }

    @Test
    void validateAccess_RoleNotAllowed_AccessDenied() {
        CustomRole role = CustomRole.builder().id(99L).build();
        User user = User.builder().id(1L).customRole(role).build();
        Zone zone = Zone.builder()
                .id(1L)
                .status(ZoneStatus.ACTIVE)
                .orgId(10L)
                .name("Zone A")
                .allowedRoleIds(List.of(5L))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(zone));

        AccessResponseDTO response = accessControlUseCase.validateAccess(1L, 1L);

        assertFalse(response.isGranted());
        assertEquals("ROLE_NOT_ALLOWED", response.getReason());
        verify(accessLogRepository).save(any(AccessLog.class));
    }
}
