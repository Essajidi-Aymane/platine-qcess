part of 'push_notification_bloc.dart';

enum PushNotificationStatus {
  initial,
  loading,
  registered,
  error,
}

enum NotificationsStatus {
  initial,
  loading,
  loaded,
  loadingMore,
  failure,
}

final class PushNotificationState {
  final PushNotificationStatus status;
  final String? fcmToken;
  final String? errorMessage;
  
  final PushNotificationReceived? lastNotification;
  
  final NotificationsStatus notificationsStatus;
  final List<NotificationDto> notifications;
  final int unreadCount;
  final bool hasReachedMax;
  final int currentPage;

  const PushNotificationState({
    this.status = PushNotificationStatus.initial,
    this.fcmToken,
    this.errorMessage,
    this.lastNotification,
    this.notificationsStatus = NotificationsStatus.initial,
    this.notifications = const [],
    this.unreadCount = 0,
    this.hasReachedMax = false,
    this.currentPage = 0,
  });

  PushNotificationState copyWith({
    PushNotificationStatus? status,
    String? fcmToken,
    String? errorMessage,
    PushNotificationReceived? lastNotification,
    NotificationsStatus? notificationsStatus,
    List<NotificationDto>? notifications,
    int? unreadCount,
    bool? hasReachedMax,
    int? currentPage,
  }) {
    return PushNotificationState(
      status: status ?? this.status,
      fcmToken: fcmToken ?? this.fcmToken,
      errorMessage: errorMessage,
      lastNotification: lastNotification ?? this.lastNotification,
      notificationsStatus: notificationsStatus ?? this.notificationsStatus,
      notifications: notifications ?? this.notifications,
      unreadCount: unreadCount ?? this.unreadCount,
      hasReachedMax: hasReachedMax ?? this.hasReachedMax,
      currentPage: currentPage ?? this.currentPage,
    );
  }
}
