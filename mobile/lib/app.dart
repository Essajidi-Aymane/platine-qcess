import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mobile/core/di/di.dart';
import 'package:mobile/core/rooting/app_router.dart';
import 'package:mobile/core/theme/app_theme.dart';
import 'package:mobile/features/auth/data/repositories/i_auth_repository.dart';
import 'package:mobile/features/auth/logic/bloc/auth_bloc.dart';
import 'package:mobile/features/home/data/repositories/I_dashboard_user_repository.dart';
import 'package:mobile/features/home/logic/bloc/dashboard_bloc.dart';
import 'package:mobile/features/maintenance/data/repositories/i_maintenance_repository.dart';
import 'package:mobile/features/maintenance/logic/bloc/tickets_bloc.dart';
import 'package:mobile/features/notification/logic/bloc/push_notification_bloc.dart';
import 'package:mobile/features/notification/presentation/notification_initializer.dart';
import 'package:mobile/features/splash/logic/bloc/splash_bloc.dart';

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: [
        BlocProvider<SplashBloc>(
          create: (_) => sl<SplashBloc>(),
        ),
        BlocProvider<AuthBloc>(
          create: (_) => AuthBloc(authRepository: sl<IAuthRepository>()),
        ),
        BlocProvider<DashboardBloc>(
          create: (_) => DashboardBloc(
              dashboardUserRepository: sl<IDashboardUserRepository>()),
        ),
        BlocProvider<TicketsBloc>(
          create: (_) => TicketsBloc(
              maintenanceRepository: sl<IMaintenanceRepository>()),
        ),
        BlocProvider<PushNotificationBloc>(
          create: (_) => sl<PushNotificationBloc>(),
        ),
      ],
      child: NotificationInitializer(
        child: Builder(
          builder: (context) {
            final authBloc = context.read<AuthBloc>();
            final splashBloc = context.read<SplashBloc>();
            final appRouter = AppRouter(authBloc, splashBloc);

            return MaterialApp.router(
              scaffoldMessengerKey: rootScaffoldMessengerKey,
              title: 'Qcess',
              theme: AppTheme.lightTheme,
              debugShowCheckedModeBanner: false,
              routerConfig: appRouter.router,
            );
          },
        ),
      ),
    );
  }
}