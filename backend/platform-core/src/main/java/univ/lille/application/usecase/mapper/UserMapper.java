package univ.lille.application.usecase.mapper;

import org.springframework.stereotype.Component;
import univ.lille.domain.model.User;
import univ.lille.dto.auth.user.UserDTO;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) return null;

        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .userStatus(user.getUserStatus())
                .organisationId(user.getOrganization() != null ? user.getOrganization().getId() : null)
                .organizationName(user.getOrganization() != null ? user.getOrganization().getName() : null)
                .createdAt(user.getCreatedAt())
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();

        if (user.getCustomRole() != null) {
            userDTO.setCustomRoleName(user.getCustomRole().getName());
        }

        return userDTO;
    }
}
