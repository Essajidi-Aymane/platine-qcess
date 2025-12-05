package univ.lille.dto.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import  univ.lille.dto.auth.user.UserDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
private List<UserDTO> data;
private Map<String, Object> meta;


}
