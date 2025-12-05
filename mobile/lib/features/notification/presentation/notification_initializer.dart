import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mobile/features/auth/logic/bloc/auth_bloc.dart';
import 'package:mobile/features/auth/logic/bloc/auth_state.dart';
import 'package:mobile/features/notification/logic/bloc/push_notification_bloc.dart';

class NotificationInitializer extends StatelessWidget {
  final Widget child;

  const NotificationInitializer({
    super.key,
    required this.child,
  });

  @override
  Widget build(BuildContext context) {
    return BlocListener<AuthBloc, AuthState>(
      listenWhen: (previous, current) {
        return previous is! AuthAuthenticated && current is AuthAuthenticated;
      },
      listener: (context, state) {
        if (state is AuthAuthenticated) {
          context.read<PushNotificationBloc>().add(
                const PushNotificationInitRequested(),
              );
        }
      },
      child: child,
    );
  }
}