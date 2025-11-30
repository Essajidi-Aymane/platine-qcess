package univ.lille.application.usecase.mapper;

import univ.lille.dto.role.CustomRoleDTO;

public class CustomRoleMapper {


    public static CustomRoleDTO toDTO (univ.lille.domain.model.CustomRole customRole) {
        if(customRole == null ) return null;
        CustomRoleDTO customRoleDTO = CustomRoleDTO.builder()
                .id(customRole.getId())
                .name(customRole.getName())
                .description(customRole.getDescription())
                .organizationId(customRole.getOrganization().getId())
                .createdAt(customRole.getCreatedAt())
                .build();


        return customRoleDTO;
    }
}
