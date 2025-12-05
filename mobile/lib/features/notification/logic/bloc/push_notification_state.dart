part of 'push_notification_bloc.dart';

enum PushNotificationStatus {
  initial,
  loading,
  registered,
  error,
}

final class PushNotificationState {
  final PushNotificationStatus status;
  final String? fcmToken;
  final String? errorMessage;
  
  final PushNotificationReceived? lastNotification;

  const PushNotificationState({
    this.status = PushNotificationStatus.initial,
    this.fcmToken,
    this.errorMessage,
    this.lastNotification,
  });

  PushNotificationState copyWith({
    PushNotificationStatus? status,
    String? fcmToken,
    String? errorMessage,
    PushNotificationReceived? lastNotification,
  }) {
    return PushNotificationState(
      status: status ?? this.status,
      fcmToken: fcmToken ?? this.fcmToken,
      errorMessage: errorMessage,
      lastNotification: lastNotification ?? this.lastNotification,
    );
  }
}
