package univ.lille.domain.port.out;

import univ.lille.domain.model.User;
import univ.lille.enums.UserRole;

import java.util.Optional;
import java.util.List;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByIdAndOrganizationId(Long id , Long organizationId) ;
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAll();
    List<User> findByOrganizationId(Long organizationId);
    List<User> findByIdInAndOrganizationId(List<Long> ids, Long orgId);

    List<User> findByOrganizationIdAndRole(Long organizationId, UserRole role);
    Optional<User> findByResetPasswordToken(String token);
    List<User> findByOrganizationIdAndCustomRoleId(Long organizationId, Long customRoleId);


    void deleteUser(User user);


    void saveAll(List<User> users);
}
