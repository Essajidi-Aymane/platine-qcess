package univ.lille.domain.port.in;

import univ.lille.dto.zone.CreateZoneRequest;
import univ.lille.dto.zone.ZoneDTO;

public interface ZoneManagementPort {

    ZoneDTO createZone (Long organisationId , Long adminId , CreateZoneRequest request) ;
}
