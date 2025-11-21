package univ.lille.module_maintenance.domain.port;

public interface TicketRepository {
    Optional<Ticket> findById(Long id);
    void save(Ticket ticket);
    TicketCatalog get();
}