import 'dart:async';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mobile/features/notification/data/repositories/i_push_notification_repository.dart';

part 'push_notification_event.dart';
part 'push_notification_state.dart';

class PushNotificationBloc extends Bloc<PushNotificationEvent, PushNotificationState> {
  final IPushNotificationRepository _repository;
  
  StreamSubscription<String>? _tokenRefreshSubscription;
  StreamSubscription<RemoteMessage>? _foregroundMessageSubscription;

  PushNotificationBloc({
    required IPushNotificationRepository repository,
  })  : _repository = repository,
        super(const PushNotificationState()) {
    on<PushNotificationInitRequested>(_onInitRequested);
    on<PushNotificationTokenRefreshed>(_onTokenRefreshed);
    on<PushNotificationReceived>(_onNotificationReceived);
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

      _tokenRefreshSubscription?.cancel();
      _tokenRefreshSubscription = _repository.onTokenRefresh().listen((newToken) {
        add(PushNotificationTokenRefreshed(newToken));
      });

      _foregroundMessageSubscription?.cancel();
      _foregroundMessageSubscription = FirebaseMessaging.onMessage.listen((message) {
        add(PushNotificationReceived(
          title: message.notification?.title,
          body: message.notification?.body,
          data: message.data,
        ));
      });
    } catch (e) {
      emit(state.copyWith(
        status: PushNotificationStatus.error,
        errorMessage: e.toString(),
      ));
    }
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
  }

  @override
  Future<void> close() {
    _tokenRefreshSubscription?.cancel();
    _foregroundMessageSubscription?.cancel();
    return super.close();
  }
}
