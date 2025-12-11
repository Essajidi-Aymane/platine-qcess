import 'package:mobile/features/auth/data/models/user_info.dart';
import 'package:mobile/features/home/data/models/user_dashboard.dart';

abstract class IDashboardUserRepository {
  Future<UserDashboard> getUserDashboard(UserInfo userInfo);
}