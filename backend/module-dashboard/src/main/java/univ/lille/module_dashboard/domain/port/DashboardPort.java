package univ.lille.module_dashboard.domain.port;

import univ.lille.dto.DashboardStatsDTO;

public interface DashboardPort {
    DashboardStatsDTO getDashboardStats(Long organizationId);
}
