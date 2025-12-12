class UserInfoResponse {
  final int? id;
  final String? email;
  final String? fullName;
  final String? firstName;
  final String? lastName;
  final String? role;
  final String? userStatus;
  final int? customRoleId;
  final String? customRoleName;
  final int? organisationId;
  final String? organizationName;
  final DateTime? createdAt;
  final String? profilePictureUrl;

  UserInfoResponse({
    this.id,
    this.email,
    this.fullName,
    this.firstName,
    this.lastName,
    this.role,
    this.userStatus,
    this.customRoleId,
    this.customRoleName,
    this.organisationId,
    this.organizationName,
    this.createdAt,
    this.profilePictureUrl,
  });

  factory UserInfoResponse.fromJson(Map<String, dynamic> json) {
    return UserInfoResponse(
      id: json['id'] as int?,
      email: json['email'] as String?,
      fullName: json['fullName'] as String?,
      firstName: json['firstName'] as String?,
      lastName: json['lastName'] as String?,
      role: json['role']?.toString(),
      userStatus: json['userStatus']?.toString(),
      customRoleId: json['customRoleId'] as int?,
      customRoleName: json['customRoleName'] as String?,
      organisationId: json['organisationId'] as int?,
      organizationName: json['organizationName'] as String?,
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'] as String)
          : null,
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
      'userStatus': userStatus,
      'customRoleId': customRoleId,
      'customRoleName': customRoleName,
      'organisationId': organisationId,
      'organizationName': organizationName,
      'createdAt': createdAt?.toIso8601String(),
      'profilePictureUrl': profilePictureUrl,
    };
  }
}