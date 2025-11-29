package univ.lille.application.usecase.mapper;

import univ.lille.dto.role.CustomRoleDTO;

public class CustomRoleMapper {


    public static CustomRoleDTO toDTO (univ.lille.domain.model.CustomRole customRole) {
        if(customRole == null ) return null;
        CustomRoleDTO customRoleDTO = new CustomRoleDTO();
        customRoleDTO.setId(customRole.getId());
        customRoleDTO.setName(customRole.getName());
        customRoleDTO.setDescription(customRole.getDescription());
        customRoleDTO.setOrganizationId(customRole.getOrganization().getId());
        customRoleDTO.setCreatedAt(customRole.getCreatedAt());

        return customRoleDTO;
    }
}
