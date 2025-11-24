package univ.lille.module_maintenance.domain.exception;

public class TicketNotFoundException extends TicketException {
    private final Long ticketId;
    
    public TicketNotFoundException(String message) {
        super(message);
        this.ticketId = null;
    }
    
    public TicketNotFoundException(Long ticketId) {
        super(TicketErrorMessages.ticketNotFound(ticketId));
        this.ticketId = ticketId;
    }
    
    public Long getTicketId() {
        return ticketId;
    }
    
    public static TicketNotFoundException forUser(Long ticketId, Long userId) {
        return new TicketNotFoundException(
            TicketErrorMessages.ticketNotFound(ticketId) + " pour l'utilisateur " + userId
        );
    }
    
    public static TicketNotFoundException forOrganization(Long ticketId, Long organizationId) {
        return new TicketNotFoundException(
            TicketErrorMessages.ticketNotFound(ticketId) + " pour l'organisation " + organizationId
        );
    }
}
