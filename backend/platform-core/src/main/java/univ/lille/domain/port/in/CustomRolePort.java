package univ.lille.domain.port.in;

import univ.lille.dto.role.CreateCustomRoleRequest;
import univ.lille.dto.role.CustomRoleDTO;
import univ.lille.dto.role.UpdateCustomRoleRequest;

import java.util.List;

public interface CustomRolePort {
    CustomRoleDTO createCustomRole(CreateCustomRoleRequest customRoleRequest,Long organizationId, Long adminId) ;
    CustomRoleDTO updateCustomRole(Long roleId, Long orgId, Long adminId, UpdateCustomRoleRequest request);

    void deleteCustomRole(Long roleId, Long organizationId, Long adminId) ;
    List<CustomRoleDTO> getCustomRolesByOrganization(Long organizationId);
    CustomRoleDTO getCustomRoleForUser(Long userId, Long organizationId);

}
