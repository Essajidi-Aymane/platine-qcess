class UpdateProfileRequest {
  final String? firstName;
  final String? lastName;
  final String? email;

  const UpdateProfileRequest({
    this.firstName,
    this.lastName,
    this.email,
  });

  Map<String, dynamic> toJson() {
    return {
      if (firstName != null) 'firstName': firstName!.trim(),
      if (lastName != null) 'lastName': lastName!.trim(),
      if (email != null) 'email': email!.trim(),
    };
  }
}
