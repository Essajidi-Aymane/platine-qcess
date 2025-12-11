import 'package:mobile/features/auth/data/models/user_info.dart';
import 'package:mobile/features/home/data/models/user_dashboard.dart';
import 'package:mobile/features/home/data/repositories/I_dashboard_user_repository.dart';

class DashboardUserRepositoryMock implements IDashboardUserRepository {

  @override
  Future<UserDashboard> getUserDashboard(UserInfo userInfo) async {
    await Future.delayed(Duration(milliseconds: 500));
    return UserDashboard(
      username: userInfo.displayName,
      totalAccess: 106,
      lastAccess: DateTime.now(),
      profilePictureUrl: "https://i.pravatar.cc/150?img=12",
      totalZones: 5,
    );
  }
}