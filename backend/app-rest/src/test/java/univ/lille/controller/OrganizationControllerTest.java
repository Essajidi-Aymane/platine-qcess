package univ.lille.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import univ.lille.application.usecase.OrganizationUseCase;
import univ.lille.dto.org.OrganizationUpdateRequest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationControllerTest {

    @Mock
    private OrganizationUseCase organizationUseCase;

    @InjectMocks
    private OrganizationController controller;

    @Test
    void updateOrgDetails_should_call_usecase_and_return_ok() {
        // Given
        OrganizationUpdateRequest request = new OrganizationUpdateRequest();
        request.setName("New Name");
        request.setAddress("New Address");
        request.setDescription("Updated description");
        request.setPhoneNumber("0102030405");

        // When
        ResponseEntity<String> response = controller.updateOrgDetails(request);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Organization details updated successfully.");

        verify(organizationUseCase, times(1))
                .updateOrganizationDetails(request);
    }
}
