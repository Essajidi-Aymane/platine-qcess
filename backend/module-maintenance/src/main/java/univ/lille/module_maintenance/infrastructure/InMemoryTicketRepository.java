package univ.lille.module_maintenance.infrastructure;

@RequiredArgsConstructor
public class InMemoryTicketRepository implements TicketRepository {
    private TicketCatalog ticketCatalog = TicketCatalog.EMPTY;

    @Override
    public Optional<Ticket> findById(Long id) {
        return ticketCatalog.getTickets().stream()
                .filter(ticket -> ticket.getId().equals(id))
                .findFirst();
    }
    
    @Override
    public TicketCatalog get() {
        return ticketCatalog;
    }

    @Override
    public void save(TicketCatalog ticketCatalog) {
        this.ticketCatalog = ticketCatalog;
    }

}