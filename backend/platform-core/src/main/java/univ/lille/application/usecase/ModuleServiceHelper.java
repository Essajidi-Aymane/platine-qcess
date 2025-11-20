package univ.lille.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import univ.lille.domain.model.ModuleActivation;
import univ.lille.domain.model.Organization;
import univ.lille.domain.port.out.ModuleActivationRepository;
import univ.lille.enums.ModuleKey;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ModuleServiceHelper {

    private final Optional<ModuleActivationRepository> moduleActivationRepository;



    public void initializeModules(Organization organization) {
        moduleActivationRepository.ifPresent(repo -> {
            Arrays.stream(ModuleKey.values()).forEach(moduleKey -> {
                ModuleActivation activation = ModuleActivation.builder()
                        .organization(organization)
                        .moduleKey(moduleKey)
                        .active(moduleKey.isMandatory())
                        .build();

                repo.save(activation);
            });
        });
    }
}
