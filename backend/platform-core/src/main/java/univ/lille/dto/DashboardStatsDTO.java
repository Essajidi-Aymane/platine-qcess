package univ.lille.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Long accessToday;
    private Long accessYesterday;
    private Double accessPercentageChange;
    
    private Long activeUsers;
    private Long newUsersThisWeek;
    
    private Long openTickets;
    private Long urgentTickets;
    
    private Long configuredZones;
    private String topZones;
}
