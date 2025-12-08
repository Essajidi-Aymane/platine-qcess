import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mobile/features/auth/logic/bloc/auth_bloc.dart';
import 'package:mobile/features/auth/logic/bloc/auth_state.dart';
import 'package:mobile/features/notification/logic/bloc/push_notification_bloc.dart';

final GlobalKey<ScaffoldMessengerState> rootScaffoldMessengerKey =
    GlobalKey<ScaffoldMessengerState>();

class NotificationInitializer extends StatelessWidget {
  final Widget child;

  const NotificationInitializer({
    super.key,
    required this.child,
  });

  @override
  Widget build(BuildContext context) {
    return MultiBlocListener(
      listeners: [
        BlocListener<AuthBloc, AuthState>(
          listenWhen: (previous, current) {
            return previous is! AuthAuthenticated &&
                current is AuthAuthenticated;
          },
          listener: (context, state) {
            if (state is AuthAuthenticated) {
              context.read<PushNotificationBloc>().add(
                    const PushNotificationInitRequested(),
                  );
            }
          },
        ),
        BlocListener<PushNotificationBloc, PushNotificationState>(
          listenWhen: (previous, current) {
            return current.lastNotification != null &&
                previous.lastNotification != current.lastNotification;
          },
          listener: (context, state) {
            final notification = state.lastNotification;
            if (notification != null) {
              _showNotificationSnackBar(notification);
            }
          },
        ),
      ],
      child: child,
    );
  }

  void _showNotificationSnackBar(PushNotificationReceived notification) {
    final messenger = rootScaffoldMessengerKey.currentState;
    if (messenger == null) return;

    final title = notification.title ?? 'Notification';
    final body = notification.body ?? '';

    messenger.showSnackBar(
      SnackBar(
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            if (body.isNotEmpty) Text(body),
          ],
        ),
        duration: const Duration(seconds: 4),
        behavior: SnackBarBehavior.floating,
        action: SnackBarAction(
          label: 'OK',
          onPressed: () {
            messenger.hideCurrentSnackBar();
          },
        ),
      ),
    );
  }
}