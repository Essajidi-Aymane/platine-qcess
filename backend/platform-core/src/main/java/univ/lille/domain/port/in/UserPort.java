package univ.lille.domain.port.in;

import univ.lille.dto.auth.user.CreateUserRequest;
import univ.lille.dto.auth.user.UpdateProfileRequest;
import univ.lille.dto.auth.user.UserDTO;

import java.util.List;

public interface UserPort {
    UserDTO createUser(CreateUserRequest createUserRequest);
    List<UserDTO> getUsersByOrganizationId(Long organizationId);
    void activateUser(Long userId, Long orgId);
    void suspendUser(Long userId, Long orgId);
    void assignCustomRoleToUsers(Long roleId, List<Long> userIds, Long orgId);
    void unassignCustomRoleFromUsers(Long roleId, List<Long> userIds, Long orgId, Long adminId);

    UserDTO getCurrentUserProfile();
    UserDTO updateProfile(UpdateProfileRequest request);
    UserDTO updateProfilePicture(String pictureUrl);
}
