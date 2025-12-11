import 'package:equatable/equatable.dart';
import 'package:mobile/features/auth/data/models/user_info.dart';

abstract class DashboardEvent extends Equatable {
  @override
  List<Object?> get props => [];
}

class LoadDashboard extends DashboardEvent {
  final UserInfo userInfo;

  LoadDashboard({required this.userInfo});

  @override
  List<Object?> get props => [userInfo];
}

class RefreshDashboard extends DashboardEvent {
  final UserInfo userInfo;

  RefreshDashboard({required this.userInfo});

  @override
  List<Object?> get props => [userInfo];
}