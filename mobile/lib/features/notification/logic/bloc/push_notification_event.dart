part of 'push_notification_bloc.dart';

sealed class PushNotificationEvent {
  const PushNotificationEvent();
}

final class PushNotificationInitRequested extends PushNotificationEvent {
  const PushNotificationInitRequested();
}

final class PushNotificationTokenRefreshed extends PushNotificationEvent {
  final String token;
  
  const PushNotificationTokenRefreshed(this.token);
}

final class PushNotificationReceived extends PushNotificationEvent {
  final String? title;
  final String? body;
  final Map<String, dynamic>? data;

  const PushNotificationReceived({
    this.title,
    this.body,
    this.data,
  });
}
