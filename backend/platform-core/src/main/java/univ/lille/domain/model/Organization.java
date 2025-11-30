package univ.lille.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Organization {
    private Long id ;
    private String name ;
    private String phone ;
    private String address ;
    private String description ;
    private LocalDateTime createdAt ;

    @Builder.Default
    private List<User> users = new ArrayList<>();
   // @Builder.Default
    //private List<ModuleActivation> moduleActivations = new ArrayList<>();
    @Builder.Default
    private List<Zone> zones = new ArrayList< >() ;
    @Builder.Default
    private List<CustomRole> customRoles = new ArrayList<>();


    public void addUser(User user) {
        users.add(user);
        user.setOrganization(this);
    }

    public void addZone(Zone zone) {
        zones.add(zone);
        zone.setOrganization(this);
    }

    public void removeZone(Zone zone) {
        zones.remove(zone);
        zone.setOrganization(null);
    }
    public void removeUser(User user) {
        users.remove(user);
        user.setOrganization(null);
    }
    public void removeCustomRole(CustomRole role) {
        customRoles.remove(role);
        role.setOrganization(null);
    }

    public void addCustomRole(CustomRole role) {
        customRoles.add(role);
        role.setOrganization(this);
    }

}
