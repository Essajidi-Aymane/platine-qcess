package univ.lille.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import univ.lille.enums.ZoneStatus;
import univ.lille.enums.ZoneType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Zone {

    private Long id;
    private String name;
    private String description;

    // 🔑 seulement l'id de l'organisation
    private Long orgId;

    // 🔑 seulement les ids des rôles autorisés
    @Builder.Default
    private List<Long> allowedRoleIds = new ArrayList<>();

    private ZoneStatus status;
    private LocalDateTime createdAt;

    /**
     * Règle métier d'accès à la zone.
     * - Un ADMIN a toujours accès.
     * - Sinon, il faut au moins un rôle custom autorisé.
     */
    public boolean isAccessibleBy(User user) {
        if (user == null) {
            return false;
        }
        if (user.isAdmin()) {
            return true;
        }
        if (allowedRoleIds == null || allowedRoleIds.isEmpty()) {
            return false;
        }
        return user.hasAnyAllowedRole(allowedRoleIds);
    }

    public void addAllowedRole(Long roleId) {
        if (roleId != null && !allowedRoleIds.contains(roleId)) {
            allowedRoleIds.add(roleId);
        }
    }

    public void removeAllowedRole(Long roleId) {
        if (roleId == null) return;
        allowedRoleIds.removeIf(id -> id != null && id.equals(roleId));
    }
}
