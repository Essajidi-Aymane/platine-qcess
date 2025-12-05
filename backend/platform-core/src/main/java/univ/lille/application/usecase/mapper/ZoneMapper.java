package univ.lille.application.usecase.mapper;

import lombok.RequiredArgsConstructor;
import univ.lille.domain.model.CustomRole;
import univ.lille.domain.model.Zone;
import univ.lille.domain.port.out.CustomRoleRepository;
import univ.lille.dto.zone.ZoneDTO;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ZoneMapper {
    private final CustomRoleRepository customRoleRepository;

    public static ZoneDTO toDTO(Zone zone) {
        ZoneDTO dto = new ZoneDTO();
        dto.setId(zone.getId());
        dto.setName(zone.getName());
        dto.setDescription(zone.getDescription());
        dto.setOrganizationId(zone.getOrgId());
        dto.setCreatedAt(zone.getCreatedAt());
        dto.setStatus(zone.getStatus() != null ? zone.getStatus().name() : null);

        dto.setAllowedRoleIds(
                zone.getAllowedRoleIds() != null
                        ? new ArrayList<>(zone.getAllowedRoleIds())
                        : new ArrayList<>()
        );

        dto.setAllowedRolesNames(new ArrayList<>());

        return dto;
    }


    private ZoneDTO toDTOWithRoleNames(Zone zone) {
        ZoneDTO dto = ZoneMapper.toDTO(zone);

        if (zone.getAllowedRoleIds() != null && !zone.getAllowedRoleIds().isEmpty()) {
            List<CustomRole> roles = customRoleRepository
                    .findByIdInAndOrganizationId(zone.getAllowedRoleIds(), zone.getOrgId());

            dto.setAllowedRolesNames(
                    roles.stream()
                            .map(CustomRole::getName)
                            .toList()
            );
        } else {
            dto.setAllowedRolesNames(List.of());
        }

        return dto;
    }
}
