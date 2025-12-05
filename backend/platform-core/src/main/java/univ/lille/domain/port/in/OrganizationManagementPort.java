package univ.lille.domain.port.in;

import univ.lille.dto.org.OrganizationUpdateRequest;
import univ.lille.dto.zone.CreateZoneRequest;
import univ.lille.dto.zone.ZoneDTO;

public interface OrganizationManagementPort {

    // ancien OrganizationPort
    void updateOrganizationDetails(OrganizationUpdateRequest req);
    // ancien ActivateModulePort
    void activateModule(Long organizationId, String moduleKey, Long adminId);

    // ancien DeactivateModulePort
    void deactivateModule(Long organizationId, String moduleKey);


    // ancien CheckZoneAccessPort
    //boolean hasAccessToZone(String qrCode, Long userId);
}
