package univ.lille.module_maintenance.domain.exception;

public class TicketNotFoundException extends TicketException {
    private final Long ticketId;
    
    public TicketNotFoundException(String message) {
        super(message);
        this.ticketId = null;
    }
    
    public TicketNotFoundException(Long ticketId) {
        super("Le ticket avec l'ID " + ticketId + " est introuvable");
        this.ticketId = ticketId;
    }
    
    public Long getTicketId() {
        return ticketId;
    }
    
    public static TicketNotFoundException forUser(Long ticketId, Long userId) {
        return new TicketNotFoundException(
            "Le ticket " + ticketId + " est introuvable pour l'utilisateur " + userId
        );
    }
    
    public static TicketNotFoundException forOrganization(Long ticketId, Long organizationId) {
        return new TicketNotFoundException(
            "Le ticket " + ticketId + " est introuvable pour l'organisation " + organizationId
        );
    }
}
