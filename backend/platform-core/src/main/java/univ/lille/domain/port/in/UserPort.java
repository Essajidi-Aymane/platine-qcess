package univ.lille.domain.port.in;

import univ.lille.dto.auth.user.CreateUserRequest;
import univ.lille.dto.auth.user.UserDTO;

import java.util.List;

public interface UserPort {
    UserDTO createUser(CreateUserRequest createUserRequest);
    List<UserDTO> getUsersByOrganizationId(Long organizationId);

    //UserDTO assignRoleToUser(Long userId, Long adminId , Long organizationId , Long roleId);

    void  assignCustomRoleToUsers(Long roleId, List<Long> userIds, Long orgId, Long adminId);
    void unassignCustomRoleFromUsers(Long roleId, List<Long> userIds, Long orgId, Long adminId);
}
