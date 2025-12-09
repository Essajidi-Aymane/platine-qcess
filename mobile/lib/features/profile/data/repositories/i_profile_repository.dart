import 'package:mobile/features/profile/data/models/user_profile.dart';
import 'package:mobile/features/profile/data/dto/update_profile_request.dart';

abstract class IProfileRepository {
  Future<UserProfile> getMyProfile();
  Future<UserProfile> updateProfile(UpdateProfileRequest request);
  Future<UserProfile> updateProfilePicture(String imagePath);
}
