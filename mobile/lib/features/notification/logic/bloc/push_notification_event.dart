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


sealed class NotificationsEvent extends PushNotificationEvent {
  const NotificationsEvent();
}

final class NotificationsRequested extends NotificationsEvent {
  final bool refresh;
  const NotificationsRequested({this.refresh = false});
}

final class NotificationsLoadMore extends NotificationsEvent {
  const NotificationsLoadMore();
}

final class NotificationMarkAsRead extends NotificationsEvent {
  final int notificationId;
  const NotificationMarkAsRead(this.notificationId);
}

final class NotificationsMarkAllAsRead extends NotificationsEvent {
  const NotificationsMarkAllAsRead();
}

final class NotificationsUnreadCountRequested extends NotificationsEvent {
  const NotificationsUnreadCountRequested();
}
