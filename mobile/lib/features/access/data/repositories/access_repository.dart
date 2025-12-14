import 'package:mobile/core/network/base_api_repository.dart';
import 'package:mobile/features/access/data/dto/access_log_dto.dart';
import 'package:mobile/features/access/data/dto/access_request_dto.dart';
import 'package:mobile/features/access/data/dto/access_response_dto.dart';
import 'package:mobile/features/access/data/repositories/i_access_repository.dart';

class AccessRepository extends BaseApiRepository implements IAccessRepository {
  AccessRepository(super.dio);

  static const String _basePath = '/api/access';

  @override
  Future<AccessResponseDTO> scanQrCode(AccessRequestDTO request) {
    return post<AccessResponseDTO>(
      '$_basePath/scan',
      data: request.toJson(),
      fromJson: (data) {
        final map = data as Map<String, dynamic>;
        return AccessResponseDTO.fromJson(map);
      },
    ).onError((error, stackTrace) {
      if (error is ApiException && error.statusCode == 403 && error.data is Map<String, dynamic>) {
        final map = error.data as Map<String, dynamic>;
        return AccessResponseDTO.fromJson(map);
      }
      throw error!;
    });
  }

  @override
  Future<List<AccessLogDTO>> getUserAccessLogs({int limit = 10}) {
    return get<List<AccessLogDTO>>(
      '$_basePath/logs/user',
      queryParameters: {'limit': limit},
      fromJson: (data) {
        final list = data as List;
        return list
            .map((e) => AccessLogDTO.fromJson(e as Map<String, dynamic>))
            .toList();
      },
    );
  }
}
