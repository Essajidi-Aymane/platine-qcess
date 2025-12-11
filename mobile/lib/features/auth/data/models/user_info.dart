import 'package:equatable/equatable.dart';

class UserInfo extends Equatable {
  final int id;
  final String email;
  final String? fullName;
  final String? firstName;
  final String? lastName;
  final String role;
  final String? customRoleName;
  final int? organizationId;
  final String? organizationName;
  final String? profilePictureUrl;

  const UserInfo({
    required this.id,
    required this.email,
    this.fullName,
    this.firstName,
    this.lastName,
    required this.role,
    this.customRoleName,
    this.organizationId,
    this.organizationName,
    this.profilePictureUrl,
  });

  String get displayName {
    if (role == 'ADMIN' && fullName != null) {
      return fullName!;
    }
    if (firstName != null && lastName != null) {
      return '$firstName $lastName';
    }
    if (firstName != null) {
      return firstName!;
    }
    return email;
  }

  factory UserInfo.fromJson(Map<String, dynamic> json) {
    return UserInfo(
      id: json['id'] as int,
      email: json['email'] as String,
      fullName: json['fullName'] as String?,
      firstName: json['firstName'] as String?,
      lastName: json['lastName'] as String?,
      role: json['role'] as String,
      customRoleName: json['customRoleName'] as String?,
      organizationId: json['organisationId'] as int?,
      organizationName: json['organizationName'] as String?,
      profilePictureUrl: json['profilePictureUrl'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'email': email,
      'fullName': fullName,
      'firstName': firstName,
      'lastName': lastName,
      'role': role,
      'customRoleName': customRoleName,
      'organisationId': organizationId,
      'organizationName': organizationName,
      'profilePictureUrl': profilePictureUrl,
    };
  }

  @override
  List<Object?> get props => [
        id,
        email,
        fullName,
        firstName,
        lastName,
        role,
        customRoleName,
        organizationId,
        organizationName,
        profilePictureUrl,
      ];
}
