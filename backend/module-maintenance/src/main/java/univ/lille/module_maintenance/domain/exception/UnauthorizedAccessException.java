package univ.lille.module_maintenance.domain.exception;

public class UnauthorizedAccessException extends TicketException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
    
    public static UnauthorizedAccessException cannotDeleteTicket(Long userId, Long ticketId) {
        return new UnauthorizedAccessException(
            "L'utilisateur " + userId + " n'est pas autorisé à supprimer le ticket " + ticketId
        );
    }

    public static UnauthorizedAccessException cannotCancelTicket(Long userId, Long ticketId) {
        return new UnauthorizedAccessException(
            "L'utilisateur " + userId + " n'est pas autorisé à annuler le ticket " + ticketId
        );
    }
    
    public static UnauthorizedAccessException cannotUpdateTicket(Long userId, Long ticketId) {
        return new UnauthorizedAccessException(
            "L'utilisateur " + userId + " n'est pas autorisé à modifier le ticket " + ticketId
        );
    }
    
    public static UnauthorizedAccessException cannotViewTicket(Long userId, Long ticketId) {
        return new UnauthorizedAccessException(
            "L'utilisateur " + userId + " n'est pas autorisé à consulter le ticket " + ticketId
        );
    }
    
    public static UnauthorizedAccessException adminOnly(String action) {
        return new UnauthorizedAccessException(
            "L'action '" + action + "' est réservée aux administrateurs uniquement"
        );
    }
    
    public static UnauthorizedAccessException organizationMismatch(Long ticketOrgId, Long userOrgId) {
        return new UnauthorizedAccessException(
            "Vous ne pouvez accéder qu'aux tickets de votre organisation"
        );
    }
}
