package univ.lille.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.domain.exception.CustomRoleException;
import univ.lille.domain.exception.ZoneNotFoundException;
import univ.lille.domain.model.CustomRole;
import univ.lille.domain.model.Zone;
import univ.lille.domain.port.out.CustomRoleRepository;
import univ.lille.domain.port.out.ZoneEventPublisher;
import univ.lille.domain.port.out.ZoneRepository;
import univ.lille.dto.zone.CreateZoneRequest;
import univ.lille.dto.zone.UpdateZoneRequest;
import univ.lille.dto.zone.ZoneDTO;
import univ.lille.enums.ZoneStatus;
import univ.lille.events.ZoneCreatedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZoneUseCaseTest {

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private ZoneEventPublisher eventPublisher;

    @Mock
    private CustomRoleRepository customRoleRepository;

    @InjectMocks
    private ZoneUseCase zoneUseCase;

    // ========================================
    // CREATE ZONE TESTS
    // ========================================

    @Test
    void createZone_should_create_zone_and_publish_event() {
        Long orgId = 1L;
        CreateZoneRequest request = new CreateZoneRequest("Zone A", "Test zone");

        Zone savedZone = Zone.builder()
                .id(10L)
                .name("Zone A")
                .description("Test zone")
                .orgId(orgId)
                .status(ZoneStatus.ACTIVE)
                .allowedRoleIds(new ArrayList<>())
                .build();

        when(zoneRepository.save(any(Zone.class))).thenReturn(savedZone);

        ZoneDTO result = zoneUseCase.createZone(request, orgId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Zone A");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");

        ArgumentCaptor<Zone> zoneCaptor = ArgumentCaptor.forClass(Zone.class);
        verify(zoneRepository).save(zoneCaptor.capture());
        Zone capturedZone = zoneCaptor.getValue();
        assertThat(capturedZone.getName()).isEqualTo("Zone A");
        assertThat(capturedZone.getOrgId()).isEqualTo(orgId);

        ArgumentCaptor<ZoneCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ZoneCreatedEvent.class);
        verify(eventPublisher).publishZoneCreated(eventCaptor.capture());
        ZoneCreatedEvent event = eventCaptor.getValue();
        assertThat(event.getZoneId()).isEqualTo(10L);
        assertThat(event.getOrganizationId()).isEqualTo(orgId);
        assertThat(event.getName()).isEqualTo("Zone A");
    }

    // ========================================
    // GET ZONE TESTS
    // ========================================

    @Test
    void getZone_should_return_zone_when_exists() {
        Long orgId = 1L;
        Long zoneId = 10L;

        Zone zone = Zone.builder()
                .id(zoneId)
                .name("Zone A")
                .orgId(orgId)
                .status(ZoneStatus.ACTIVE)
                .allowedRoleIds(new ArrayList<>())
                .build();

        when(zoneRepository.findByIdAndOrganizationId(zoneId, orgId))
                .thenReturn(Optional.of(zone));

        ZoneDTO result = zoneUseCase.getZone(orgId, zoneId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(zoneId);
        assertThat(result.getName()).isEqualTo("Zone A");
    }

    @Test
    void getZone_should_throw_when_zone_not_found() {
        Long orgId = 1L;
        Long zoneId = 999L;

        when(zoneRepository.findByIdAndOrganizationId(zoneId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> zoneUseCase.getZone(orgId, zoneId))
                .isInstanceOf(ZoneNotFoundException.class)
                .hasMessageContaining("Zone not found");
    }

    // ========================================
    // DELETE ZONE TESTS
    // ========================================

    @Test
    void deleteZone_should_mark_zone_as_inactive_and_clear_roles() {
        Long orgId = 1L;
        Long zoneId = 10L;

        Zone zone = Zone.builder()
                .id(zoneId)
                .name("Zone A")
                .orgId(orgId)
                .status(ZoneStatus.ACTIVE)
                .allowedRoleIds(new ArrayList<>(List.of(1L, 2L)))
                .build();

        when(zoneRepository.findByIdAndOrganizationId(zoneId, orgId))
                .thenReturn(Optional.of(zone));

        zoneUseCase.deleteZone(zoneId, orgId);

        assertThat(zone.getStatus()).isEqualTo(ZoneStatus.INACTIVE);
        assertThat(zone.getAllowedRoleIds()).isEmpty();
        verify(zoneRepository).save(zone);
    }

    @Test
    void deleteZone_should_do_nothing_when_already_inactive() {
        Long orgId = 1L;
        Long zoneId = 10L;

        Zone zone = Zone.builder()
                .id(zoneId)
                .name("Zone A")
                .orgId(orgId)
                .status(ZoneStatus.INACTIVE)
                .allowedRoleIds(new ArrayList<>())
                .build();

        when(zoneRepository.findByIdAndOrganizationId(zoneId, orgId))
                .thenReturn(Optional.of(zone));

        zoneUseCase.deleteZone(zoneId, orgId);

        assertThat(zone.getStatus()).isEqualTo(ZoneStatus.INACTIVE);
        verify(zoneRepository, never()).save(any());
    }

    @Test
    void deleteZone_should_throw_when_zone_not_found() {
        Long orgId = 1L;
        Long zoneId = 999L;

        when(zoneRepository.findByIdAndOrganizationId(zoneId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> zoneUseCase.deleteZone(zoneId, orgId))
                .isInstanceOf(ZoneNotFoundException.class)
                .hasMessageContaining("Zone not found");
    }

    // ========================================
    // UPDATE ZONE TESTS
    // ========================================

    @Test
    void updateZone_should_update_name_and_description() {
        Long orgId = 1L;
        Long zoneId = 10L;

        UpdateZoneRequest request = new UpdateZoneRequest("Updated Zone", "Updated description");

        Zone zone = Zone.builder()
                .id(zoneId)
                .name("Old Zone")
                .description("Old description")
                .orgId(orgId)
                .status(ZoneStatus.ACTIVE)
                .allowedRoleIds(new ArrayList<>())
                .build();

        Zone updatedZone = Zone.builder()
                .id(zoneId)
                .name("Updated Zone")
                .description("Updated description")
                .orgId(orgId)
                .status(ZoneStatus.ACTIVE)
                .allowedRoleIds(new ArrayList<>())
                .build();

        when(zoneRepository.findByIdAndOrganizationId(zoneId, orgId))
                .thenReturn(Optional.of(zone));
        when(zoneRepository.save(zone)).thenReturn(updatedZone);

        ZoneDTO result = zoneUseCase.updateZone(zoneId, request, orgId);

        assertThat(result.getName()).isEqualTo("Updated Zone");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        verify(zoneRepository).save(zone);
    }

    @Test
    void updateZone_should_ignore_blank_name() {
        Long orgId = 1L;
        Long zoneId = 10L;

        UpdateZoneRequest request = new UpdateZoneRequest("", "New description");

        Zone zone = Zone.builder()
                .id(zoneId)
                .name("Original Zone")
                .description("Old description")
                .orgId(orgId)
                .status(ZoneStatus.ACTIVE)
                .allowedRoleIds(new ArrayList<>())
                .build();

        when(zoneRepository.findByIdAndOrganizationId(zoneId, orgId))
                .thenReturn(Optional.of(zone));
        when(zoneRepository.save(zone)).thenReturn(zone);

        ZoneDTO result = zoneUseCase.updateZone(zoneId, request, orgId);

        assertThat(result.getName()).isEqualTo("Original Zone");
        assertThat(result.getDescription()).isEqualTo("New description");
    }

    @Test
    void updateZone_should_throw_when_zone_not_found() {
        Long orgId = 1L;
        Long zoneId = 999L;

        UpdateZoneRequest request = new UpdateZoneRequest("Updated Zone", null);

        when(zoneRepository.findByIdAndOrganizationId(zoneId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> zoneUseCase.updateZone(zoneId, request, orgId))
                .isInstanceOf(ZoneNotFoundException.class);
    }

    // ========================================
    // ADD ALLOWED ROLES TESTS
    // ========================================

    @Test
    void addAllowedRolesToZone_should_add_roles_when_valid() {
        Long orgId = 1L;
        Long zoneId = 10L;
        List<Long> roleIds = List.of(1L, 2L);

        Zone zone = Zone.builder()
                .id(zoneId)
                .name("Zone A")
                .orgId(orgId)
                .allowedRoleIds(new ArrayList<>())
                .build();

        CustomRole role1 = CustomRole.builder().id(1L).orgId(orgId).build();
        CustomRole role2 = CustomRole.builder().id(2L).orgId(orgId).build();

        when(zoneRepository.findByIdAndOrganizationId(zoneId, orgId))
                .thenReturn(Optional.of(zone));
        when(customRoleRepository.findByIdInAndOrganizationId(roleIds, orgId))
                .thenReturn(List.of(role1, role2));

        zoneUseCase.addAllowedRolesToZone(zoneId, roleIds, orgId);

        assertThat(zone.getAllowedRoleIds()).containsExactlyInAnyOrder(1L, 2L);
        verify(zoneRepository).save(zone);
    }

    @Test
    void addAllowedRolesToZone_should_do_nothing_when_roleIds_empty() {
        Long orgId = 1L;
        Long zoneId = 10L;

        Zone zone = Zone.builder()
                .id(zoneId)
                .name("Zone A")
                .orgId(orgId)
                .allowedRoleIds(new ArrayList<>())
                .build();

        when(zoneRepository.findByIdAndOrganizationId(zoneId, orgId))
                .thenReturn(Optional.of(zone));

        zoneUseCase.addAllowedRolesToZone(zoneId, List.of(), orgId);

        verify(customRoleRepository, never()).findByIdInAndOrganizationId(any(), any());
        verify(zoneRepository, never()).save(any());
    }

    @Test
    void addAllowedRolesToZone_should_throw_when_role_not_found_in_org() {
        Long orgId = 1L;
        Long zoneId = 10L;
        List<Long> roleIds = List.of(1L, 2L, 3L);

        Zone zone = Zone.builder()
                .id(zoneId)
                .name("Zone A")
                .orgId(orgId)
                .allowedRoleIds(new ArrayList<>())
                .build();

        CustomRole role1 = CustomRole.builder().id(1L).orgId(orgId).build();

        when(zoneRepository.findByIdAndOrganizationId(zoneId, orgId))
                .thenReturn(Optional.of(zone));
        when(customRoleRepository.findByIdInAndOrganizationId(roleIds, orgId))
                .thenReturn(List.of(role1)); // Only 1 role found instead of 3

        assertThatThrownBy(() -> zoneUseCase.addAllowedRolesToZone(zoneId, roleIds, orgId))
                .isInstanceOf(CustomRoleException.class)
                .hasMessageContaining("n'existent pas dans l'organisation");

        verify(zoneRepository, never()).save(any());
    }

    // ========================================
    // REMOVE ALLOWED ROLE TESTS
    // ========================================

    @Test
    void removeAllowedRoleFromZone_should_remove_role() {
        Long orgId = 1L;
        Long zoneId = 10L;
        Long roleId = 2L;

        Zone zone = Zone.builder()
                .id(zoneId)
                .name("Zone A")
                .orgId(orgId)
                .allowedRoleIds(new ArrayList<>(List.of(1L, 2L, 3L)))
                .build();

        when(zoneRepository.findByIdAndOrganizationId(zoneId, orgId))
                .thenReturn(Optional.of(zone));
        when(customRoleRepository.existsByIdAndOrganizationId(roleId, orgId))
                .thenReturn(true);

        zoneUseCase.removeAllowedRoleFromZone(zoneId, roleId, orgId);

        assertThat(zone.getAllowedRoleIds()).containsExactlyInAnyOrder(1L, 3L);
        verify(zoneRepository).save(zone);
    }

    @Test
    void removeAllowedRoleFromZone_should_throw_when_role_not_in_org() {
        Long orgId = 1L;
        Long zoneId = 10L;
        Long roleId = 999L;

        Zone zone = Zone.builder()
                .id(zoneId)
                .name("Zone A")
                .orgId(orgId)
                .allowedRoleIds(new ArrayList<>(List.of(1L, 2L)))
                .build();

        when(zoneRepository.findByIdAndOrganizationId(zoneId, orgId))
                .thenReturn(Optional.of(zone));
        when(customRoleRepository.existsByIdAndOrganizationId(roleId, orgId))
                .thenReturn(false);

        assertThatThrownBy(() -> zoneUseCase.removeAllowedRoleFromZone(zoneId, roleId, orgId))
                .isInstanceOf(CustomRoleException.class)
                .hasMessageContaining("doesn't exist in this organization");
    }

    // ========================================
    // REPLACE ALLOWED ROLES TESTS
    // ========================================

    @Test
    void replaceAllowedRolesForZone_should_replace_roles() {
        Long orgId = 1L;
        Long zoneId = 10L;
        List<Long> newRoleIds = List.of(5L, 6L);

        Zone zone = Zone.builder()
                .id(zoneId)
                .name("Zone A")
                .orgId(orgId)
                .allowedRoleIds(new ArrayList<>(List.of(1L, 2L, 3L)))
                .build();

        CustomRole role5 = CustomRole.builder().id(5L).orgId(orgId).build();
        CustomRole role6 = CustomRole.builder().id(6L).orgId(orgId).build();

        when(zoneRepository.findByIdAndOrganizationId(zoneId, orgId))
                .thenReturn(Optional.of(zone));
        when(customRoleRepository.findByIdInAndOrganizationId(newRoleIds, orgId))
                .thenReturn(List.of(role5, role6));

        zoneUseCase.replaceAllowedRolesForZone(zoneId, newRoleIds, orgId);

        assertThat(zone.getAllowedRoleIds()).containsExactlyInAnyOrder(5L, 6L);
        verify(zoneRepository).save(zone);
    }

    @Test
    void replaceAllowedRolesForZone_should_clear_roles_when_roleIds_empty() {
        Long orgId = 1L;
        Long zoneId = 10L;

        Zone zone = Zone.builder()
                .id(zoneId)
                .name("Zone A")
                .orgId(orgId)
                .allowedRoleIds(new ArrayList<>(List.of(1L, 2L)))
                .build();

        when(zoneRepository.findByIdAndOrganizationId(zoneId, orgId))
                .thenReturn(Optional.of(zone));

        zoneUseCase.replaceAllowedRolesForZone(zoneId, List.of(), orgId);

        assertThat(zone.getAllowedRoleIds()).isEmpty();
        verify(zoneRepository).save(zone);
        verify(customRoleRepository, never()).findByIdInAndOrganizationId(any(), any());
    }

    @Test
    void replaceAllowedRolesForZone_should_throw_when_role_not_found() {
        Long orgId = 1L;
        Long zoneId = 10L;
        List<Long> newRoleIds = List.of(5L, 6L);

        Zone zone = Zone.builder()
                .id(zoneId)
                .name("Zone A")
                .orgId(orgId)
                .allowedRoleIds(new ArrayList<>(List.of(1L, 2L)))
                .build();

        CustomRole role5 = CustomRole.builder().id(5L).orgId(orgId).build();

        when(zoneRepository.findByIdAndOrganizationId(zoneId, orgId))
                .thenReturn(Optional.of(zone));
        when(customRoleRepository.findByIdInAndOrganizationId(newRoleIds, orgId))
                .thenReturn(List.of(role5)); // Only 1 role found

        assertThatThrownBy(() -> zoneUseCase.replaceAllowedRolesForZone(zoneId, newRoleIds, orgId))
                .isInstanceOf(CustomRoleException.class)
                .hasMessageContaining("n'existent pas dans l'organisation");
    }

    // ========================================
    // GET ZONES FOR ORG TESTS
    // ========================================

    @Test
    void getZonesForOrg_should_return_active_zones() {
        Long orgId = 1L;

        Zone zone1 = Zone.builder()
                .id(1L)
                .name("Zone A")
                .orgId(orgId)
                .status(ZoneStatus.ACTIVE)
                .allowedRoleIds(new ArrayList<>())
                .build();

        Zone zone2 = Zone.builder()
                .id(2L)
                .name("Zone B")
                .orgId(orgId)
                .status(ZoneStatus.ACTIVE)
                .allowedRoleIds(new ArrayList<>())
                .build();

        when(zoneRepository.findByOrganizationIdAndStatus(orgId, ZoneStatus.ACTIVE))
                .thenReturn(List.of(zone1, zone2));

        List<ZoneDTO> result = zoneUseCase.getZonesForOrg(orgId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Zone A");
        assertThat(result.get(1).getName()).isEqualTo("Zone B");
    }

    @Test
    void getZonesForOrg_should_return_empty_list_when_no_active_zones() {
        Long orgId = 1L;

        when(zoneRepository.findByOrganizationIdAndStatus(orgId, ZoneStatus.ACTIVE))
                .thenReturn(List.of());

        List<ZoneDTO> result = zoneUseCase.getZonesForOrg(orgId);

        assertThat(result).isEmpty();
    }
}
