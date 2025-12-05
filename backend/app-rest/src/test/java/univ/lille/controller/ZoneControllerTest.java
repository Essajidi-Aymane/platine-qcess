package univ.lille.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import univ.lille.domain.port.in.ZoneManagementPort;
import univ.lille.dto.zone.AllowedRolesRequest;
import univ.lille.dto.zone.CreateZoneRequest;
import univ.lille.dto.zone.UpdateZoneRequest;
import univ.lille.dto.zone.ZoneDTO;
import univ.lille.infrastructure.adapter.security.QcessUserPrincipal;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZoneControllerTest {

    @Mock
    private ZoneManagementPort zonePort;

    @InjectMocks
    private ZoneController zoneController;

    @Test
    void createZone_should_call_port_and_return_created_zone() {
        // GIVEN
        Long orgId = 5L;

        CreateZoneRequest request = new CreateZoneRequest();
        request.setName("Open Space R&D");
        request.setDescription("Espace de développement");

        ZoneDTO createdZone = new ZoneDTO();
        createdZone.setId(10L);
        createdZone.setName("Open Space R&D");
        createdZone.setDescription("Espace de développement");
        createdZone.setOrganizationId(orgId);

        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getOrganizationId()).thenReturn(orgId);

        when(zonePort.createZone(request, orgId)).thenReturn(createdZone);

        // WHEN
        ResponseEntity<ZoneDTO> response = zoneController.createZone(request, principal);

        // THEN
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getId());
        assertEquals("Open Space R&D", response.getBody().getName());
        verify(zonePort).createZone(request, orgId);
    }

    @Test
    void deleteZone_should_call_port_and_return_success_message() {
        // GIVEN
        Long zoneId = 15L;
        Long orgId = 3L;
        Long adminId = 50L;

        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getId()).thenReturn(adminId);
        when(principal.getOrganizationId()).thenReturn(orgId);

        doNothing().when(zonePort).deleteZone(zoneId, orgId);

        // WHEN
        ResponseEntity<String> response = zoneController.deleteZone(zoneId, principal);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Zone deleted successfully", response.getBody());
        verify(zonePort).deleteZone(zoneId, orgId);
    }

    @Test
    void getZone_should_return_zone_details() {
        // GIVEN
        Long zoneId = 20L;
        Long orgId = 4L;

        ZoneDTO zoneDTO = new ZoneDTO();
        zoneDTO.setId(zoneId);
        zoneDTO.setName("Salle de Réunion A");
        zoneDTO.setDescription("Grande salle équipée");
        zoneDTO.setOrganizationId(orgId);

        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getOrganizationId()).thenReturn(orgId);

        when(zonePort.getZone(orgId, zoneId)).thenReturn(zoneDTO);

        // WHEN
        ResponseEntity<ZoneDTO> response = zoneController.getZone(zoneId, principal);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(zoneId, response.getBody().getId());
        assertEquals("Salle de Réunion A", response.getBody().getName());
        verify(zonePort).getZone(orgId, zoneId);
    }

    @Test
    void getZonesForOrg_should_return_list_of_zones() {
        // GIVEN
        Long orgId = 2L;

        ZoneDTO zone1 = new ZoneDTO();
        zone1.setId(1L);
        zone1.setName("Zone 1");
        zone1.setOrganizationId(orgId);

        ZoneDTO zone2 = new ZoneDTO();
        zone2.setId(2L);
        zone2.setName("Zone 2");
        zone2.setOrganizationId(orgId);

        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getOrganizationId()).thenReturn(orgId);

        when(zonePort.getZonesForOrg(orgId)).thenReturn(List.of(zone1, zone2));

        // WHEN
        ResponseEntity<List<ZoneDTO>> response = zoneController.getZonesForOrg(principal);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Zone 1", response.getBody().get(0).getName());
        assertEquals("Zone 2", response.getBody().get(1).getName());
        verify(zonePort).getZonesForOrg(orgId);
    }

    @Test
    void updateZone_should_call_port_and_return_updated_zone() {
        // GIVEN
        Long zoneId = 25L;
        Long orgId = 6L;

        UpdateZoneRequest request = new UpdateZoneRequest();
        request.setName("Open Space Rénové");
        request.setDescription("Espace modernisé");

        ZoneDTO updatedZone = new ZoneDTO();
        updatedZone.setId(zoneId);
        updatedZone.setName("Open Space Rénové");
        updatedZone.setDescription("Espace modernisé");
        updatedZone.setOrganizationId(orgId);

        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getOrganizationId()).thenReturn(orgId);

        when(zonePort.updateZone(zoneId, request, orgId)).thenReturn(updatedZone);

        // WHEN
        ResponseEntity<ZoneDTO> response = zoneController.updateZone(zoneId, request, principal);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Open Space Rénové", response.getBody().getName());
        verify(zonePort).updateZone(zoneId, request, orgId);
    }

    @Test
    void addAllowedRolesToZone_should_call_port_and_return_success() {
        // GIVEN
        Long zoneId = 30L;
        Long orgId = 7L;
        List<Long> roleIds = List.of(1L, 2L, 3L);

        AllowedRolesRequest request = new AllowedRolesRequest();
        request.setRoleIds(roleIds);

        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getOrganizationId()).thenReturn(orgId);

        doNothing().when(zonePort).addAllowedRolesToZone(zoneId, roleIds, orgId);

        // WHEN
        ResponseEntity<String> response = zoneController.addAllowedRolesToZone(zoneId, request, principal);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Roles added successfully to zone", response.getBody());
        verify(zonePort).addAllowedRolesToZone(zoneId, roleIds, orgId);
    }

    @Test
    void replaceAllowedRolesForZone_should_call_port_and_return_success() {
        // GIVEN
        Long zoneId = 35L;
        Long orgId = 8L;
        List<Long> roleIds = List.of(5L, 6L);

        AllowedRolesRequest request = new AllowedRolesRequest();
        request.setRoleIds(roleIds);

        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getOrganizationId()).thenReturn(orgId);

        doNothing().when(zonePort).replaceAllowedRolesForZone(zoneId, roleIds, orgId);

        // WHEN
        ResponseEntity<String> response = zoneController.replaceAllowedRolesForZone(zoneId, request, principal);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Roles replaced successfully", response.getBody());
        verify(zonePort).replaceAllowedRolesForZone(zoneId, roleIds, orgId);
    }

    @Test
    void removeAllowedRoleFromZone_should_call_port_and_return_success() {
        // GIVEN
        Long zoneId = 40L;
        Long roleId = 9L;
        Long orgId = 10L;

        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getOrganizationId()).thenReturn(orgId);

        doNothing().when(zonePort).removeAllowedRoleFromZone(zoneId, roleId, orgId);

        // WHEN
        ResponseEntity<String> response = zoneController.removeAllowedRoleFromZone(zoneId, roleId, principal);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Role removed successfully", response.getBody());
        verify(zonePort).removeAllowedRoleFromZone(zoneId, roleId, orgId);
    }
}
