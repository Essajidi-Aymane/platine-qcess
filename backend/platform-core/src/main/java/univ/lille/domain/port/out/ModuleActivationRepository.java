package univ.lille.domain.port.out;

import univ.lille.domain.model.ModuleActivation;

import java.util.List;
import java.util.Optional;

public interface ModuleActivationRepository {
    ModuleActivation save(ModuleActivation moduleActivation);
    Optional<ModuleActivation> findByOrganizationIdAndModuleName(Long organizationId, String moduleName);
    List<ModuleActivation> findByOrganizationId(Long organizationId);
    List<ModuleActivation> findByOrganizationIdAndIsActiveTrue(Long organizationId);

}
