package univ.lille.module_maintenance.domain.exception;

public class CommentException extends TicketException {
    public CommentException(String message) {
        super(message);
    }
    
    public static CommentException emptyContent() {
        return new CommentException("Le contenu du commentaire ne peut pas être vide");
    }
    
    public static CommentException notFound(Long commentId) {
        return new CommentException("Le commentaire avec l'ID " + commentId + " est introuvable");
    }
    
    public static CommentException unauthorized(Long userId, Long commentId) {
        return new CommentException(
            "L'utilisateur " + userId + " n'est pas autorisé à modifier le commentaire " + commentId
        );
    }

    public static CommentException contentTooLong(int max) {
        return new CommentException("Le contenu du commentaire dépasse la longueur maximale (" + max + ")");
    }
}
