package univ.lille.domain.port.in;

import univ.lille.dto.auth.user.CreateUserRequest;
import univ.lille.dto.auth.user.UserDTO;

import java.util.List;

public interface UserPort {
    UserDTO createUser(CreateUserRequest createUserRequest);
    List<UserDTO> getUsersByOrganizationId(Long organizationId);
}
