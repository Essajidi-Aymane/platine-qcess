package univ.lille.dto.role;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UnassignCustomRoleRequest {
    @NotNull
    private Long roleId;

    @NotEmpty
    private List<Long> userIds;

}
