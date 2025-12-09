import 'dart:async';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mobile/features/notification/data/dto/notification_dto.dart';
import 'package:mobile/features/notification/data/repositories/i_device_token_repository.dart';
import 'package:mobile/features/notification/data/repositories/i_notification_repository.dart';

part 'push_notification_event.dart';
part 'push_notification_state.dart';

class PushNotificationBloc extends Bloc<PushNotificationEvent, PushNotificationState> {
  final IDeviceTokenRepository _repository;
  final INotificationRepository _notificationRepository;
  
  StreamSubscription<String>? _tokenRefreshSubscription;
  StreamSubscription<RemoteMessage>? _foregroundMessageSubscription;
  bool _initialized = false;

  PushNotificationBloc({
    required IDeviceTokenRepository repository,
    required INotificationRepository notificationRepository,
  })  : _repository = repository,
        _notificationRepository = notificationRepository,
        super(const PushNotificationState()) {
    on<PushNotificationInitRequested>(_onInitRequested);
    on<PushNotificationTokenRefreshed>(_onTokenRefreshed);
    on<PushNotificationReceived>(_onNotificationReceived);
    on<NotificationsRequested>(_onNotificationsRequested);
    on<NotificationsLoadMore>(_onLoadMore);
    on<NotificationMarkAsRead>(_onMarkAsRead);
    on<NotificationsMarkAllAsRead>(_onMarkAllAsRead);
    on<NotificationsUnreadCountRequested>(_onUnreadCountRequested);
  }

  Future<void> _onInitRequested(
    PushNotificationInitRequested event,
    Emitter<PushNotificationState> emit,
  ) async {
    emit(state.copyWith(status: PushNotificationStatus.loading));

    try {
      await FirebaseMessaging.instance.requestPermission(
        alert: true,
        badge: true,
        sound: true,
      );

      final token = await _repository.getToken();

      if (token != null) {
        await _repository.registerToken(token);

        emit(state.copyWith(
          status: PushNotificationStatus.registered,
          fcmToken: token,
        ));
      } else {
        emit(state.copyWith(
          status: PushNotificationStatus.error,
          errorMessage: 'Failed to get FCM token',
        ));
        return;
      }

      if (!_initialized) {
        _tokenRefreshSubscription = _repository.onTokenRefresh().listen((newToken) {
          add(PushNotificationTokenRefreshed(newToken));
        });

        _foregroundMessageSubscription = FirebaseMessaging.onMessage.listen((message) {
          add(PushNotificationReceived(
            title: message.notification?.title,
            body: message.notification?.body,
            data: message.data,
          ));
        });
        _initialized = true;
      }
    } catch (e) {
      emit(state.copyWith(
        status: PushNotificationStatus.error,
        errorMessage: e.toString(),
      ));
    }
  }

  Future<void> _onNotificationsRequested(
    NotificationsRequested event,
    Emitter<PushNotificationState> emit,
  ) async {
    emit(state.copyWith(notificationsStatus: NotificationsStatus.loading, errorMessage: null));

    try {
      final notifications = await _notificationRepository.getNotifications(page: 0, size: 20);
      final unreadCount = await _notificationRepository.getUnreadCount();

      emit(state.copyWith(
        notificationsStatus: NotificationsStatus.loaded,
        notifications: notifications,
        unreadCount: unreadCount,
        hasReachedMax: notifications.length < 20,
        currentPage: 0,
      ));
    } catch (e) {
      emit(state.copyWith(
        notificationsStatus: NotificationsStatus.failure,
        errorMessage: e.toString(),
      ));
    }
  }

  Future<void> _onLoadMore(
    NotificationsLoadMore event,
    Emitter<PushNotificationState> emit,
  ) async {
    if (state.hasReachedMax || state.notificationsStatus == NotificationsStatus.loadingMore) {
      return;
    }

    emit(state.copyWith(notificationsStatus: NotificationsStatus.loadingMore));

    try {
      final nextPage = state.currentPage + 1;
      final newNotifications = await _notificationRepository.getNotifications(
        page: nextPage,
        size: 20,
      );

      emit(state.copyWith(
        notificationsStatus: NotificationsStatus.loaded,
        notifications: [...state.notifications, ...newNotifications],
        hasReachedMax: newNotifications.length < 20,
        currentPage: nextPage,
      ));
    } catch (e) {
      emit(state.copyWith(
        notificationsStatus: NotificationsStatus.failure,
        errorMessage: e.toString(),
      ));
    }
  }

  Future<void> _onMarkAsRead(
    NotificationMarkAsRead event,
    Emitter<PushNotificationState> emit,
  ) async {
    try {
      final updated = await _notificationRepository.markAsRead(event.notificationId);

      final updatedNotifications = state.notifications.map((n) {
        return n.id == event.notificationId ? updated : n;
      }).toList();

      final newUnreadCount = state.unreadCount > 0 ? state.unreadCount - 1 : 0;

      emit(state.copyWith(
        notifications: updatedNotifications,
        unreadCount: newUnreadCount,
      ));
    } catch (e) {
      emit(state.copyWith(errorMessage: e.toString()));
    }
  }

  Future<void> _onMarkAllAsRead(
    NotificationsMarkAllAsRead event,
    Emitter<PushNotificationState> emit,
  ) async {
    try {
      await _notificationRepository.markAllAsRead();

      final updatedNotifications = state.notifications.map((n) {
        return n.copyWith(read: true, readAt: DateTime.now());
      }).toList();

      emit(state.copyWith(
        notifications: updatedNotifications,
        unreadCount: 0,
      ));
    } catch (e) {
      emit(state.copyWith(errorMessage: e.toString()));
    }
  }

  Future<void> _onUnreadCountRequested(
    NotificationsUnreadCountRequested event,
    Emitter<PushNotificationState> emit,
  ) async {
    try {
      final unreadCount = await _notificationRepository.getUnreadCount();
      emit(state.copyWith(unreadCount: unreadCount));
    } catch (_) {}
  }

  Future<void> _onTokenRefreshed(
    PushNotificationTokenRefreshed event,
    Emitter<PushNotificationState> emit,
  ) async {
    try {
      await _repository.registerToken(event.token);
      emit(state.copyWith(
        status: PushNotificationStatus.registered,
        fcmToken: event.token,
      ));
    } catch (e) {
      emit(state.copyWith(
        status: PushNotificationStatus.error,
        errorMessage: 'Failed to re-register token: $e',
      ));
    }
  }

  void _onNotificationReceived(
    PushNotificationReceived event,
    Emitter<PushNotificationState> emit,
  ) {
    emit(state.copyWith(lastNotification: event));
    add(const NotificationsRequested(refresh: true));

  }

  @override
  Future<void> close() {
    _tokenRefreshSubscription?.cancel();
    _foregroundMessageSubscription?.cancel();
    return super.close();
  }
}
