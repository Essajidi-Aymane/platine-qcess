package univ.lille.module_maintenance.domain.exception;

/**
 * Centralized error message helpers to avoid duplication and ease future i18n.
 */
public final class TicketErrorMessages {
    private TicketErrorMessages() {}

    public static String userNotAuthorized(String action, Long userId, Long ticketId) {
        return "L'utilisateur " + userId + " n'est pas autorisé à " + action + " le ticket " + ticketId;
    }

    public static String ticketNotFound(Long ticketId) {
        return "Le ticket avec l'ID " + ticketId + " est introuvable";
    }

    public static String transitionInvalid(String from, String to) {
        return "Transition invalide du statut '" + from + "' vers '" + to + "'";
    }
}