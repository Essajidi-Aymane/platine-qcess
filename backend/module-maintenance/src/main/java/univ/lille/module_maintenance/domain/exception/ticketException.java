package univ.lille.module_maintenance.domain.exception;

public class TicketException extends RuntimeException {
    public TicketException(String message) {
        super(message);
    }

    public static TicketException ticketNotFound(Long id) {
        return new TicketNotFoundException("Ticket with id " + id + " not found.");
    }

    

}
