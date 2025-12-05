package univ.lille.application.usecase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import univ.lille.application.service.AuthenticationService;


import org.springframework.stereotype.Service;
import univ.lille.application.usecase.mapper.UserMapper;
import univ.lille.application.utils.NameUtils;
import univ.lille.domain.exception.CustomRoleException;
import univ.lille.domain.exception.EmailAlreadyExistsException;
import univ.lille.domain.exception.OrganizationNotFoundException;
import univ.lille.domain.exception.UserNotFoundException;
import univ.lille.domain.model.CustomRole;
import univ.lille.domain.model.Organization;
import univ.lille.domain.model.User;
import univ.lille.domain.port.in.UserPort;
import univ.lille.domain.port.out.CustomRoleRepository;
import univ.lille.domain.port.out.EmailPort;
import univ.lille.domain.port.out.OrganizationRepository;
import univ.lille.domain.port.out.UserRepository;
import univ.lille.dto.auth.user.CreateUserRequest;
import univ.lille.dto.auth.user.UserDTO;
import univ.lille.enums.UserRole;
import univ.lille.enums.UserStatus;
import univ.lille.infrastructure.utils.CodeGenerator;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserUseCase implements UserPort {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final EmailPort emailPort;
    private final AuthenticationService authenticationService;
    private final  CustomRoleRepository customRoleRepository;


    @Override
    @Transactional
    public UserDTO createUser(CreateUserRequest createUserRequest) {
        Long organizationId = authenticationService.getCurrentUserOrganizationId();
        String fullName = NameUtils.buildFullName(
                createUserRequest.getFirstName(),
                createUserRequest.getLastName()
        );

        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(
                        "Organization not found with ID: " + organizationId
                ));

        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already exists: " + createUserRequest.getEmail()
            );
        }

        String loginCode = CodeGenerator.generateLoginCode();
        User user = User.builder()
                .email(createUserRequest.getEmail())
                .fullName(fullName)
                .firstName(createUserRequest.getFirstName())
                .lastName(createUserRequest.getLastName())
                .role(createUserRequest.getRole())
                .loginCode( loginCode)
                .userStatus(UserStatus.PENDING)
                .organization(org)
                .createdAt(LocalDateTime.now())
                .build();
        user = userRepository.save(user);

        emailPort.sendWelcomeEmail(user.getEmail(), user.getFullName(), loginCode);
        return UserMapper.toDTO(user);


    }



    @Override
    public List<UserDTO> getUsersByOrganizationId(Long organizationId) {
        if(!organizationRepository.existsById(organizationId)) {
            throw new OrganizationNotFoundException(
                    "Organization not found with ID: " + organizationId
            );
        }
        List<User> users = userRepository.findByOrganizationIdAndRole(organizationId, UserRole.USER);
        return users.stream().map(UserMapper::toDTO).toList();
    }

    /**
     * @param userId
     * @param orgId
     */
    @Override
    public void activateUser(Long userId, Long orgId) {
        User user = userRepository.findByIdAndOrganizationId(userId,orgId).orElseThrow(()->
                new  UserNotFoundException("Cet utilisateur n'existe pas dans cette organisation"));
        if (user.getUserStatus() == UserStatus.DELETED) {
            return;
        }
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    /**
     * @param userId
     * @param orgId
     */
    @Override
    public void suspendUser(Long userId, Long orgId) {
        User user = userRepository.findByIdAndOrganizationId(userId,orgId).orElseThrow(()->
                new  UserNotFoundException("Cet utilisateur n'existe pas dans cette organisation"));

        user.setUserStatus(UserStatus.SUSPENDED);

        userRepository.save(user);

    }


    /**
     * @param roleId
     * @param userIds
     * @param orgId
     */
    @Override
    @Transactional
    public void assignCustomRoleToUsers(Long roleId, List<Long> userIds, Long orgId) {

        CustomRole role = customRoleRepository.findByIdAndOrganizationId(roleId , orgId)
                .orElseThrow(() -> new CustomRoleException(
                        "Custom role not found with ID: " + roleId + " in organization ID: " + orgId
                ));
        List<User> users = userRepository.findByIdInAndOrganizationId(userIds,orgId) ;

        if (users.size() != userIds.size()) {
            throw new UserNotFoundException("Some users do not belong to the organization or do not exist.");
        }
        for (User user :users){
            user.setCustomRole(role);
        }

        userRepository.saveAll(users);
    log.info("Assigned custom role ID {} to users {} in organization ID {} by admin ID {}",
        roleId, userIds, orgId);

    }

    /**
     * @param roleId
     * @param userIds
     * @param orgId
     * @param adminId
     */
    @Override
    public void unassignCustomRoleFromUsers(Long roleId, List<Long> userIds, Long orgId, Long adminId) {
        CustomRole role = customRoleRepository.findByIdAndOrganizationId(roleId , orgId)
                .orElseThrow(() -> new CustomRoleException(
                        "Custom role not found with ID: " + roleId + " in organization ID: " + orgId
                ));
        List<User> users = userRepository.findByIdInAndOrganizationId(userIds,orgId) ;

        for (User user :users){
            user.removeRole();
        }

        userRepository.saveAll(users);
        log.info("Desassigned custom role ID {} from users {} in organization ID {} by admin ID {}",
                roleId, userIds, orgId, adminId);

    }


}
