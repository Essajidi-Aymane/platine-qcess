package univ.lille.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import univ.lille.enums.UserRole;

@Data
@AllArgsConstructor
@Builder
public class AuthResponse {
    private  String token ;
    private String email ;
    private UserRole role ;
    private Long organisationId ;
    private String fullName;


}
