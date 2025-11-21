package univ.lille.dto.org;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationUpdateRequest {

    private String name ;
    private String address ;
    private  String description ;
    @Pattern(
            regexp = "^\\+?[0-9. ()-]{7,25}$",
            message = "Le numéro de téléphone est invalide"
    )
    private String phoneNumber ;

}
