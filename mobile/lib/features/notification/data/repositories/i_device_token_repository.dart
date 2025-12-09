
abstract class IDeviceTokenRepository {
  Future<void> registerToken(String fcmToken);
  Future<String?> getToken();
  Stream<String> onTokenRefresh();
}