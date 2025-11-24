package univ.lille.dto.auth;

import lombok.Builder;
import lombok.Data;
import univ.lille.enums.UserRole;


@Data
@Builder
public class CurrentUserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String role;
}
