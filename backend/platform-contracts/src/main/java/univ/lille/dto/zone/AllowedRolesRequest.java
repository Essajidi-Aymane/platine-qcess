package univ.lille.dto.zone;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllowedRolesRequest {
@NotEmpty(message = "La liste des rôles ne peut pas être vide.")
    private List<Long> roleIds;


}
