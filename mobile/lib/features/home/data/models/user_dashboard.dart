import 'package:equatable/equatable.dart';

class UserDashboard extends Equatable {
  final String username;
  final String? profilePictureUrl;
  final int totalAccess;
  final DateTime? lastAccess;
  final bool? lastAccessGranted;
  final String? lastAccessReason;
  final int totalZones;

  const UserDashboard({
    required this.username,
    this.profilePictureUrl,
    required this.totalAccess,
    this.lastAccess,
    this.lastAccessGranted,
    this.lastAccessReason,
    this.totalZones = 0,
  });

  @override
  List<Object?> get props => [
        username,
        profilePictureUrl,
        totalAccess,
        lastAccess,
      lastAccessGranted,
      lastAccessReason,
        totalZones,
      ];
}