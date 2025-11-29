package univ.lille.dto.role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCustomRoleRequest {

        private String name;

        private String description;
}
