import 'package:dio/dio.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'i_push_notification_repository.dart';

class PushNotificationRepository implements IPushNotificationRepository {
  final Dio _dio;
  final FirebaseMessaging _firebaseMessaging;

  PushNotificationRepository({
    required Dio dio,
    FirebaseMessaging? firebaseMessaging,
  })  : _dio = dio,
        _firebaseMessaging = firebaseMessaging ?? FirebaseMessaging.instance;

  @override
  Future<void> registerToken(String fcmToken) async {
    await _dio.post(
      '/api/devices/register-token',
      data: {'fcmToken': fcmToken},
    );
  }

  @override
  Future<String?> getToken() async {
    return await _firebaseMessaging.getToken();
  }

  @override
  Stream<String> onTokenRefresh() {
    return _firebaseMessaging.onTokenRefresh;
  }
}
