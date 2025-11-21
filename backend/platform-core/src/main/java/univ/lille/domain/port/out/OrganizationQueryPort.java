package univ.lille.domain.port.out;

import univ.lille.domain.model.CustomRole;
import univ.lille.domain.model.User;
import univ.lille.domain.model.Zone;
import univ.lille.dto.module.ModuleStatusDTO;

import java.util.List;

public interface OrganizationQueryPort {

    List<User> getUsersByOrganization(Long organizationId);

    List<Zone> getZonesByOrganization(Long organizationId);

    List<CustomRole> getCustomRolesByOrganization(Long organizationId);

    ModuleStatusDTO getModuleStatus(Long organizationId, String moduleKey);
}