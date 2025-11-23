package univ.lille.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.application.service.AuthenticationService;
import univ.lille.domain.model.Organization;
import univ.lille.domain.port.out.OrganizationRepository;
import univ.lille.dto.org.OrganizationUpdateRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationUseCaseTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private OrganizationUseCase organizationUseCase;

    // ----------------------------------------------------------
    // 1. updateOrganizationDetails() - SUCCESS CASE
    // ----------------------------------------------------------
    @Test
    void updateOrganizationDetails_should_update_fields_and_save() {

        Long orgId = 10L;

        OrganizationUpdateRequest request = new OrganizationUpdateRequest();
        request.setName("New Org Name");
        request.setAddress("123 Street");
        request.setPhoneNumber("0600000000");
        request.setDescription("Updated Description");

        Organization org = Organization.builder()
                .id(orgId)
                .name("Old Name")
                .address("Old Addr")
                .phone("Old Phone")
                .description("Old Desc")
                .build();

        when(authenticationService.getCurrentUserOrganizationId()).thenReturn(orgId);
        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));

        organizationUseCase.updateOrganizationDetails(request);

        assertThat(org.getName()).isEqualTo("New Org Name");
        assertThat(org.getAddress()).isEqualTo("123 Street");
        assertThat(org.getPhone()).isEqualTo("0600000000");
        assertThat(org.getDescription()).isEqualTo("Updated Description");

        verify(organizationRepository).save(org);
    }

}