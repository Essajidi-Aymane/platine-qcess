package univ.lille.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String email ;
    private String password ; //Pour admins
    private String loginCode ; //Pour users
}
