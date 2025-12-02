package univ.lille.application.usecase.mapper;

import univ.lille.domain.model.Zone;
import univ.lille.dto.zone.ZoneDTO;

public class ZoneMapper {

    public static ZoneDTO toDTO (Zone zone) {
        ZoneDTO dto = new ZoneDTO();
        dto.setId(zone.getId());
        dto.setName(zone.getName());
        dto.setDescription(zone.getDescription());
        dto.setOrganizationId(zone.getOrgId());
        dto.setCreatedAt(zone.getCreatedAt());
        dto.setStatus(zone.getStatus().name());

        dto.setAllowedRolesNames(
                zone.getAllowedRoles().stream()
                        .map(role -> role.getName())
                        .toList()
        );

        return dto;
    }
}
