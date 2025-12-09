import 'package:mobile/features/profile/data/dto/update_profile_request.dart';

abstract class ProfileEvent {}

class ProfileLoadRequested extends ProfileEvent {}

class ProfileUpdateRequested extends ProfileEvent {
  final UpdateProfileRequest request;

  ProfileUpdateRequested({required this.request});
}

class ProfilePictureUpdateRequested extends ProfileEvent {
  final String imagePath;

  ProfilePictureUpdateRequested({required this.imagePath});
}

class ProfileRefreshRequested extends ProfileEvent {}
