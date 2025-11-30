package univ.lille.application.usecase;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import univ.lille.application.usecase.mapper.CustomRoleMapper;
import univ.lille.domain.exception.OrganizationNotFoundException;
import univ.lille.domain.exception.UserNotFoundException;
import univ.lille.domain.model.CustomRole;
import univ.lille.domain.model.Organization;
import univ.lille.domain.model.User;
import univ.lille.domain.port.in.CustomRolePort;
import univ.lille.domain.port.out.CustomRoleRepository;
import univ.lille.domain.port.out.OrganizationRepository;
import univ.lille.domain.port.out.UserRepository;
import univ.lille.dto.role.CreateCustomRoleRequest;
import univ.lille.dto.role.CustomRoleDTO;
import univ.lille.dto.role.UpdateCustomRoleRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomRoleUseCase implements CustomRolePort {
        private final OrganizationRepository organizationRepository;
        private final UserRepository userRepository;
        private final CustomRoleRepository customRoleRepository;


    @Override
    @Transactional
    public CustomRoleDTO createCustomRole(CreateCustomRoleRequest customRoleRequest, Long organizationId, Long adminId) {
        Organization org = getOrganizationOrThrow(organizationId);

        if ( customRoleRepository.existsByNameAndOrganizationId(customRoleRequest.getName(), organizationId)){
            throw new IllegalArgumentException(
                    "A custom role with the name '" + customRoleRequest.getName() +
                            "' already exists in the organization."
            );
        }
        CustomRole role = CustomRole.builder()
                .organization(org)
                .createdAt(LocalDateTime.now())
                .name(customRoleRequest.getName())
                .description(customRoleRequest.getDescription())
                .build();
        org.addCustomRole(role);
        organizationRepository.save(org);
        CustomRole roleSaved = customRoleRepository.save(role);

        return CustomRoleMapper.toDTO(roleSaved);
    }

    /**
     * @param roleId
     * @param orgId
     * @param adminId
     * @param request
     * @return
     */
    @Override
    @Transactional
    public CustomRoleDTO updateCustomRole(Long roleId, Long orgId, Long adminId, UpdateCustomRoleRequest request) {
        CustomRole role = customRoleRepository.findByIdAndOrganizationId(roleId, orgId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Custom role not found with ID: " + roleId
                ));
        if (request.getName() != null && !request.getName().isBlank()) {
            role.setName(request.getName());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            role.setDescription(request.getDescription());
        }
        CustomRole updatedRole = customRoleRepository.save(role);
        return CustomRoleMapper.toDTO(updatedRole);
    }

    /**
     * delete a custom role by id
     * @param roleId
     * @param organizationId
     * @param adminId
     */
    @Override
    @Transactional
    public void deleteCustomRole(Long roleId, Long organizationId, Long adminId) {
        Organization org = getOrganizationOrThrow(organizationId);

        CustomRole role = customRoleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Custom role not found with ID: " + roleId
                ));

        if (role.getOrganization()== null || !role.getOrganization().getId().equals(organizationId)) {
            throw new AccessDeniedException(
                    "Custom role does not belong to the specified organization."
            );
        }
        List<User> userWithRole = userRepository.findByOrganizationIdAndCustomRoleId(organizationId, roleId);
        for (User user : userWithRole) {
            user.removeRole(role);
            userRepository.save(user);
        }
        org.removeCustomRole(role);
        organizationRepository.save(org);
        customRoleRepository.delete(role);

    }

    /**
     * @param organizationId
     * @return
     */
    @Override
    public List<CustomRoleDTO> getCustomRolesByOrganization(Long organizationId) {
        List<CustomRole> roles = customRoleRepository.getCustomRolesByOrganizationId(organizationId);
        return roles.stream()
                .map(CustomRoleMapper::toDTO)
                .toList();
    }

    /**
     * @param userId
     * @param organizationId
     * @return
     */
    @Override
    public CustomRoleDTO getCustomRoleForUser(Long userId, Long organizationId) {
        User user = userRepository.findById(userId).orElseThrow(()->
                new UserNotFoundException("User not found with ID: " + userId
                ));

        CustomRole role = user.getCustomRole();

        if (role == null) return null ;

        return  CustomRoleMapper.toDTO(role);
    }
    private  Organization getOrganizationOrThrow(Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(
                        "Organization not found with ID: " + organizationId
                ));
    }

}
