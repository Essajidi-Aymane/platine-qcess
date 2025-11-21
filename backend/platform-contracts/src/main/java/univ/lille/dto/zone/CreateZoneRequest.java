package univ.lille.dto.zone;

import lombok.Data;

import java.util.List;

@Data
public class CreateZoneRequest {
    private  String name ;
    private String description ;
    private List<Long> allowedRolesIds ;

}
