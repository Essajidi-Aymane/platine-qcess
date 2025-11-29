package univ.lille.domain.port.in;

import univ.lille.dto.role.CreateCustomRoleRequest;
import univ.lille.dto.role.CustomRoleDTO;
import univ.lille.dto.role.UpdateCustomRoleRequest;

public interface CustomRolePort {
    CustomRoleDTO createCustomRole(CreateCustomRoleRequest customRoleRequest,Long organizationId, Long adminId) ;
    CustomRoleDTO updateCustomRole(Long roleId, Long orgId, Long adminId, UpdateCustomRoleRequest request);

    void deleteCustomRole(Long roleId, Long organizationId, Long adminId) ;
}
