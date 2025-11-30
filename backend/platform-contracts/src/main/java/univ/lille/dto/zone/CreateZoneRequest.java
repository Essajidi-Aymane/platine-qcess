package univ.lille.dto.zone;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateZoneRequest {
    @NotBlank
    private  String name ;
    private String description ;


}
