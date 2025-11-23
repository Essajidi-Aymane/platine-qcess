package univ.lille.module_maintenance.domain.exception;


public class InvalidTicketException extends TicketException {
    public InvalidTicketException(String message) {
        super(message);
    }
    
    public static InvalidTicketException missingTitle() {
        return new InvalidTicketException("Le titre du ticket est obligatoire");
    }
    
    public static InvalidTicketException missingPriority() {
        return new InvalidTicketException("La priorité du ticket est obligatoire");
    }
    
    public static InvalidTicketException invalidStatus(String message) {
        return new InvalidTicketException("Statut du ticket invalide: " + message);
    }
    
    public static InvalidTicketException emptyContent(String fieldName) {
        return new InvalidTicketException("Le champ '" + fieldName + "' ne peut pas être vide");
    }
    
    public static InvalidTicketException invalidTransition(String currentStatus, String targetStatus) {
        return new InvalidTicketException(
            "Transition invalide du statut '" + currentStatus + "' vers '" + targetStatus + "'"
        );
    }
    
    public static InvalidTicketException missingRequiredField(String fieldName) {
        return new InvalidTicketException("Le champ obligatoire '" + fieldName + "' est manquant");
    }
}
