import 'package:mobile/core/network/base_api_repository.dart';
import 'package:mobile/features/access/data/repositories/i_access_repository.dart';
import 'package:mobile/features/auth/data/models/user_info.dart';
import 'package:mobile/features/home/data/models/user_dashboard.dart';
import 'package:mobile/features/home/data/repositories/I_dashboard_user_repository.dart';

class DashboardUserRepository extends BaseApiRepository
    implements IDashboardUserRepository {
  final IAccessRepository accessRepository;

  DashboardUserRepository(super.dio, this.accessRepository);

  @override
  Future<UserDashboard> getUserDashboard(UserInfo userInfo) async {
    final logs = await accessRepository.getUserAccessLogs(limit: 100);

    final now = DateTime.now();
    final logsThisMonth = logs.where((log) {
      if (log.accessGranted != true) return false;
      final ts = log.timestamp;
      return ts.month == now.month && ts.year == now.year;
    }).toList();

    final totalAccess = logsThisMonth.length;

    final lastAccess = logs.isNotEmpty ? logs.first.timestamp : null;
    final lastAccessGranted = logs.isNotEmpty ? logs.first.accessGranted : null;
    final lastAccessReason = logs.isNotEmpty ? logs.first.reason : null;

    final uniqueZones = logs
        .where((log) => log.zoneName != null)
        .map((log) => log.zoneName)
        .toSet()
        .length;

    final username = userInfo.displayName;

    return UserDashboard(
      username: username,
      totalAccess: totalAccess,
      lastAccess: lastAccess,
      lastAccessGranted: lastAccessGranted,
      lastAccessReason: lastAccessReason,
      profilePictureUrl: userInfo.profilePictureUrl,
      totalZones: uniqueZones,
    );
  }
}
