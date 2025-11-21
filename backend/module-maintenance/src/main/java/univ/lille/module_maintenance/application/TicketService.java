package univ.lille.module_maintenance.application;

@RequiredArgsConstructor
@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final TicketCatalog ticketCatalog;

    public Ticket createTicket(Ticket ticket) {
        TicketCatalog current = ticketCatalog.get();
        Set<Ticket> updatedTickets = new HashSet<>(current.getTickets());
        updatedTickets.add(ticket);
        ticketRepository.save(ticketCatalog.with(updatedTickets));
    }

    public Set<Ticket> getAllTicketsOrganisation(Organization organization) {
        return ticketRepository.get().getTickets();
    }

    public Set<Ticket> getAllTicketsUser(String userId) {
        return ticketRepository.get().getTickets().stream()
                .filter(ticket -> ticket.getCreatedByUserId().equals(userId))
                .collect(Collectors.toSet());
    }

    public void addCommentToTicket(Long ticketId, Comment comment) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.getComments().add(comment);
            ticketRepository.save(ticketCatalog.with(ticket));
        } else {
            throw new TicketNotFoundException("Ticket with ID " + ticketId + " not found.");
        }
    }

    public void deleteTicket(Long ticketId) {
        TicketCatalog current = ticketCatalog.get();
        Set<Ticket> updatedTickets = current.getTickets().stream()
                .filter(ticket -> !ticket.getId().equals(ticketId))
                .collect(Collectors.toSet());
        ticketRepository.save(ticketCatalog.with(updatedTickets));
    }

    public void updateTicketStatus(Long ticketId, Status newStatus) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setStatus(newStatus);
            ticketRepository.save(ticketCatalog.with(ticket));
        } else {
            throw TicketException.ticketNotFound(ticketId);
        }
    }

    public void updateTicketTitle(Long ticketId, String newTitle) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setTitle(newTitle);
            ticketRepository.save(ticketCatalog.with(ticket));
        } else {
            throw TicketException.ticketNotFound(ticketId);
        }
    }


}