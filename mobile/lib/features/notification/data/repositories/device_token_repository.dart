import 'package:dio/dio.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'i_device_token_repository.dart';

class PushNotificationRepository implements IDeviceTokenRepository {
  final Dio _dio;

  PushNotificationRepository({
    required Dio dio,
  }) : _dio = dio;

  FirebaseMessaging get _firebaseMessaging => FirebaseMessaging.instance;

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
