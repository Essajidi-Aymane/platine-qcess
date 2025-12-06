import 'package:dio/dio.dart';
import 'package:mobile/core/network/base_api_repository.dart';
import 'package:mobile/features/profile/data/dto/update_profile_request.dart';
import 'package:mobile/features/profile/data/models/user_profile.dart';
import 'package:mobile/features/profile/data/repositories/i_profile_repository.dart';

class ProfileRepository extends BaseApiRepository implements IProfileRepository {
  static const String _basePath = '/api/users';

  ProfileRepository(Dio dio) : super(dio);

  @override
  Future<UserProfile> getMyProfile() async {
    return get<UserProfile>(
      '$_basePath/me',
      fromJson: (data) => UserProfile.fromJson(data),
    );
  }

  @override
  Future<UserProfile> updateProfile(UpdateProfileRequest request) async {
    return put<UserProfile>(
      '$_basePath/me',
      data: request.toJson(),
      fromJson: (data) => UserProfile.fromJson(data),
    );
  }

  @override
  Future<UserProfile> updateProfilePicture(String imagePath) async {
    try {
      final fileName = imagePath.split('/')..removeWhere((e) => e.isEmpty);
      final formData = FormData.fromMap({
        'file': await MultipartFile.fromFile(
          imagePath,
          filename: fileName.isNotEmpty ? fileName.last : null,
        ),
      });

      final response = await dio.put(
        '$_basePath/me/profile-picture',
        data: formData,
      );

      return UserProfile.fromJson(response.data);
    } catch (e) {
      rethrow;
    }
  }
}
