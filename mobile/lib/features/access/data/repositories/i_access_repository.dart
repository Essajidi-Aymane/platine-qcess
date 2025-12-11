import 'package:mobile/features/access/data/dto/access_log_dto.dart';
import 'package:mobile/features/access/data/dto/access_request_dto.dart';
import 'package:mobile/features/access/data/dto/access_response_dto.dart';

abstract class IAccessRepository {
  Future<AccessResponseDTO> scanQrCode(AccessRequestDTO request);
  Future<List<AccessLogDTO>> getUserAccessLogs({int limit = 10});
}
