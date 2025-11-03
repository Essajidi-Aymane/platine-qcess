package univ.lille.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Email
    private String email ;

    @NotBlank
    private String password ;

    private String organisationName ;

    private String fullName ;

}
