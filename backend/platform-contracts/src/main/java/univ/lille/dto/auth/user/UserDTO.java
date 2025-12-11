package univ.lille.dto.auth.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import univ.lille.enums.UserRole;
import univ.lille.enums.UserStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;

    // pour Admin
    private String fullName;
    // pour users
    private String firstName;
    private String lastName;
    private UserRole role;
    private UserStatus userStatus;
    private Long customRoleId;
    private String customRoleName;
    private Long organisationId;
    private String organizationName;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessAt; 
    private LocalDateTime lastLogin ; 
    private String profilePictureUrl;

    public String getDisplayName() {
        if (role == UserRole.ADMIN) {
            return fullName;
        } else {
            return firstName + " " + lastName;
        }
    }
}
