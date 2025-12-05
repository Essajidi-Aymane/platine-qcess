package univ.lille.domain.port.in;

public interface PushNotificationServicePort {
    
    /**
     * Envoie une notification à un token FCM spécifique (debug/test)
     */
    void sendPushToToken(String fcmToken, String title, String body);
    
    /**
     * Envoie une notification à tous les appareils d'un utilisateur
     */
    void sendPushToUser(int userId, String title, String body);
    void sendPushToOrganization(int organizationId, String title, String body);
}
