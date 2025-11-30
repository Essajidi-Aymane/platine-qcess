package univ.lille.application.usecase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import univ.lille.application.service.AuthenticationService;
import univ.lille.application.usecase.mapper.CustomRoleMapper;
import univ.lille.domain.exception.OrganizationNotFoundException;
import univ.lille.domain.exception.RoleInUseException;
import univ.lille.domain.exception.UserNotFoundException;
import univ.lille.domain.model.CustomRole;
import univ.lille.domain.model.Organization;
import univ.lille.domain.model.User;
import univ.lille.domain.port.in.CustomRolePort;
import univ.lille.domain.port.in.OrganizationManagementPort;
import univ.lille.domain.port.out.CustomRoleRepository;
import univ.lille.domain.port.out.OrganizationRepository;
import univ.lille.domain.port.out.UserRepository;
import univ.lille.dto.org.OrganizationUpdateRequest;
import univ.lille.dto.role.CreateCustomRoleRequest;
import univ.lille.dto.role.CustomRoleDTO;
import univ.lille.dto.role.UpdateCustomRoleRequest;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrganizationUseCase implements  OrganizationManagementPort {
    private final OrganizationRepository organizationRepository;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final CustomRoleRepository customRoleRepository;


    //@Override
   // public boolean hasAccessToZone(String qrCode, Long userId) {
       // return false;
    //}

    /**
     * update organization details
     * @param OrganizationUpdateRequest request
     * */
    @Override
    public void updateOrganizationDetails(OrganizationUpdateRequest request) {
        Long organizationId = authenticationService.getCurrentUserOrganizationId();
        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(
                        "Organization not found with ID: " + organizationId
                ));
        org.setName(request.getName());
        org.setAddress(request.getAddress());
        org.setPhone(request.getPhoneNumber());
        org.setDescription(request.getDescription());
        organizationRepository.save(org);

    }

    @Override
    public void activateModule(Long organizationId, String moduleKey, Long adminId) {
        //TODO implement activation method for modules
    }

    @Override
    public void deactivateModule(Long organizationId, String moduleKey) {
//TODO implement deactivation method for modules
    }

}
