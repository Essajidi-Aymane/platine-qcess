class UserInfoResponse {
  final String? firstName;
  final String? lastName;
  final String? email;
  final String? profilePictureUrl;
  final String? organizationName;
  final DateTime? createdAt;

  UserInfoResponse({
    this.firstName,
    this.lastName,
    this.email,
    this.profilePictureUrl,
    this.organizationName,
    this.createdAt,
  });

  factory UserInfoResponse.fromJson(Map<String, dynamic> json) {
    return UserInfoResponse(
      firstName: json['firstName'] as String?,
      lastName: json['lastName'] as String?,
      email: json['email'] as String?,
      profilePictureUrl: json['profilePictureUrl'] as String?,
      organizationName: json['organizationName'] as String?,
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'] as String)
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'firstName': firstName,
      'lastName': lastName,
      'email': email,
      'profilePictureUrl': profilePictureUrl,
      'organizationName': organizationName,
      'createdAt': createdAt?.toIso8601String(),
    };
  }
}