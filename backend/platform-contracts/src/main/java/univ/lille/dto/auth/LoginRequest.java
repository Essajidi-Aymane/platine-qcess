package univ.lille.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    @Email
    private String email ;
    @NotBlank(message = "Le mot de passe ou le code de connexion est obligatoire")
    private String password ; //Pour admins
    private String loginCode ; //Pour users
}
