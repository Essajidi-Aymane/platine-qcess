package univ.lille.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.lille.application.service.AuthenticationService;
import univ.lille.domain.exception.OrganizationNotFoundException;
import univ.lille.domain.model.Organization;
import univ.lille.domain.port.in.OrganizationManagementPort;
import univ.lille.domain.port.out.OrganizationRepository;
import univ.lille.dto.org.OrganizationUpdateRequest;
import univ.lille.dto.zone.CreateZoneRequest;
import univ.lille.dto.zone.ZoneDTO;
@Service
@RequiredArgsConstructor
public class OrganizationUseCase implements  OrganizationManagementPort {
    private final OrganizationRepository organizationRepository;
    private final AuthenticationService authenticationService;

    @Override
    public ZoneDTO createZone(CreateZoneRequest req, Long organizationId, Long adminId) {
        //TODO implement creation method for zones
        return null;
    }

    @Override
    public boolean hasAccessToZone(String qrCode, Long userId) {
        return false;
    }

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
