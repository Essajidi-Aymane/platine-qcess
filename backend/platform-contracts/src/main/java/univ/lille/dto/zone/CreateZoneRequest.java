package univ.lille.dto.zone;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import univ.lille.enums.ZoneType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateZoneRequest {
    @NotBlank
    private  String name ;
    private String description ;
    //private ZoneType type;

}
