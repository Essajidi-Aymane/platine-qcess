package univ.lille.domain.model;

import lombok.*;
import univ.lille.enums.ModuleKey;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleActivation {


    private Long id ;
    private String moduleName ;
    @Getter
    private boolean active ;
    private Organization organization ;
    private ModuleKey moduleKey;
    private LocalDateTime activatedAt ;
    private LocalDateTime deactivatedAt ;
    private Long activatedBy ;


    public void active(Long userId) {
        this.active = true ;
        this.activatedAt = LocalDateTime.now() ;
        this.activatedBy = userId ;

    }

    public void deactivate() {
        this.active = false ;
        this.deactivatedAt = LocalDateTime.now() ;
    }

}
