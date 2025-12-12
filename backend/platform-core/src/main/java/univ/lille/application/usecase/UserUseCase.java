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
import univ.lille.domain.port.out.NotificationPort;
import univ.lille.domain.port.out.OrganizationRepository;
import univ.lille.domain.port.out.UserRepository;
import java.util.Map;
import java.util.HashMap;
import univ.lille.dto.auth.user.CreateUserRequest;
import univ.lille.dto.auth.user.UpdateProfileRequest;
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
    private final CustomRoleRepository customRoleRepository;
    private final NotificationPort notificationPort;


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
        
        UserDTO userDTO = UserMapper.toDTO(user);
        notificationPort.notifyResourceUpdate(
            organizationId,
            "USER",
            user.getId(),
            convertUserDTOToMap(userDTO)
        );
        
        return userDTO;


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
        
        // Notification SSE pour l'activation
        UserDTO userDTO = UserMapper.toDTO(user);
        notificationPort.notifyResourceUpdate(
            orgId,
            "USER",
            user.getId(),
            convertUserDTOToMap(userDTO)
        );
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
        
        UserDTO userDTO = UserMapper.toDTO(user);
        notificationPort.notifyResourceUpdate(
            orgId,
            "USER",
            user.getId(),
            convertUserDTOToMap(userDTO)
        );

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
        
        // Notification SSE pour chaque utilisateur mis à jour
        for (User user : users) {
            UserDTO userDTO = UserMapper.toDTO(user);
            notificationPort.notifyResourceUpdate(
                orgId,
                "USER",
                user.getId(),
                convertUserDTOToMap(userDTO)
            );
        }
        
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
        
        // Notification SSE pour chaque utilisateur mis à jour
        for (User user : users) {
            UserDTO userDTO = UserMapper.toDTO(user);
            notificationPort.notifyResourceUpdate(
                orgId,
                "USER",
                user.getId(),
                convertUserDTOToMap(userDTO)
            );
        }
        
        log.info("Desassigned custom role ID {} from users {} in organization ID {} by admin ID {}",
                roleId, userIds, orgId, adminId);
    }
    
    // Méthode helper pour convertir UserDTO en Map pour SSE
    private Map<String, Object> convertUserDTOToMap(UserDTO dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", dto.getId());
        map.put("email", dto.getEmail());
        map.put("firstName", dto.getFirstName());
        map.put("lastName", dto.getLastName());
        map.put("fullName", dto.getFullName());
        map.put("role", dto.getRole());
        map.put("userStatus", dto.getUserStatus());
        if (dto.getLastAccessAt() != null) {
            map.put("lastAccessAt", dto.getLastAccessAt());
        }
        if (dto.getLastLogin()!= null) {
            map.put("lastLoginAt", dto.getLastLogin());
        }
        if (dto.getCreatedAt() != null) {
            map.put("createdAt", dto.getCreatedAt());
        }
        if (dto.getCustomRoleId() != null) {
            map.put("customRole", dto.getCustomRoleName());
        }
        return map;
    }

    @Override
    public UserDTO getCurrentUserProfile() {
        User currentUser = authenticationService.getCurrentUser();
        return UserMapper.toDTO(currentUser);
    }

    @Override
    @Transactional
    public UserDTO updateProfile(UpdateProfileRequest request) {
        User currentUser = authenticationService.getCurrentUser();

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            currentUser.setFirstName(request.getFirstName().trim());
        }

        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            currentUser.setLastName(request.getLastName().trim());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim().toLowerCase();
            if (!newEmail.equals(currentUser.getEmail())) {
                if (userRepository.existsByEmail(newEmail)) {
                    throw new EmailAlreadyExistsException("Email already in use: " + newEmail);
                }
                currentUser.setEmail(newEmail);
            }
        }
        return UserMapper.toDTO(userRepository.save(currentUser));
    }

    @Override
    @Transactional
    public UserDTO updateProfilePicture(String pictureUrl) {
        User currentUser = authenticationService.getCurrentUser();
        currentUser.setProfilePictureUrl(pictureUrl);
        return UserMapper.toDTO(userRepository.save(currentUser));
    }
}