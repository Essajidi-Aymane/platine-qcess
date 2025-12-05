package univ.lille.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DeviceToken {
    private Integer id;
    private Integer userId;
    private String fcmToken;
}