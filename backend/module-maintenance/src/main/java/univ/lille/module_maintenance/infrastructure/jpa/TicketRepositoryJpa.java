package univ.lille.module_maintenance.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.lille.module_maintenance.domain.model.Status;
import univ.lille.module_maintenance.infrastructure.dao.TicketDao;

import java.util.List;

public interface TicketRepositoryJpa extends JpaRepository<TicketDao, Long> {

    List<TicketDao> findByCreatedByUserId(Long userId);
    
    List<TicketDao> findByOrganizationId(Long organizationId);
        
    List<TicketDao> findByOrganizationIdAndStatus(Long organizationId, Status status);
}