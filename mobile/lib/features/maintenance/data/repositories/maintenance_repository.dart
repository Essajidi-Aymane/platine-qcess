import 'package:mobile/core/network/base_api_repository.dart';
import 'package:mobile/features/maintenance/data/dto/create_ticket_request.dart';
import 'package:mobile/features/maintenance/data/dto/ticket_dto.dart';
import 'package:mobile/features/maintenance/data/dto/update_ticket_request.dart';
import 'package:mobile/features/maintenance/data/dto/add_comment_request.dart';
import 'package:mobile/features/maintenance/data/repositories/i_maintenance_repository.dart';
import 'package:mobile/features/maintenance/data/models/status.dart' as model;
import 'package:mobile/features/maintenance/data/models/priority.dart' as model;

class MaintenanceRepository extends BaseApiRepository implements IMaintenanceRepository {
  MaintenanceRepository(super.dio);

  static const String _basePath = '/api/maintenance/tickets';

  @override
  Future<List<TicketDTO>> getMyTickets({model.Status? status, model.Priority? priority}) {
    return get<List<TicketDTO>>(
      '$_basePath/me',
      queryParameters: {
        if (status != null) 'status': _mapStatus(status),
        if (priority != null) 'priority': _mapPriority(priority),
      },
      fromJson: (data) {
        final list = data as List<dynamic>;
        return list
            .map((e) => TicketDTO.fromJson(e as Map<String, dynamic>))
            .toList();
      },
    );
  }

  @override
  Future<TicketDTO> getTicketById(int id) {
    return get<TicketDTO>(
      '$_basePath/$id',
      fromJson: (data) => TicketDTO.fromJson(data as Map<String, dynamic>),
    );
  }

  @override
  Future<TicketDTO> createTicket(CreateTicketRequest request) async {
    return post<TicketDTO>(
      _basePath,
      data: request.toJson(),
      fromJson: (data) => TicketDTO.fromJson(data as Map<String, dynamic>),
    );
  }

  @override
  Future<TicketDTO> updateTicket(int id, UpdateTicketRequest request) async {
    return put<TicketDTO>(
      '$_basePath/$id',
      data: request.toJson(),
      fromJson: (data) => TicketDTO.fromJson(data as Map<String, dynamic>),
    );
  }

  @override
  Future<TicketDTO> cancelTicket(int id) async {
    return delete<TicketDTO>(
      '$_basePath/$id',
      fromJson: (data) => TicketDTO.fromJson(data as Map<String, dynamic>),
    );
  }

  @override
  Future<TicketDTO> addUserComment(int ticketId, AddCommentRequest request) async {
    return post<TicketDTO>(
      '$_basePath/$ticketId/comments',
      data: request.toJson(),
      fromJson: (data) => TicketDTO.fromJson(data as Map<String, dynamic>),
    );
  }

  String _mapStatus(model.Status s) => switch (s) {
        model.Status.open => 'OPEN',
        model.Status.inProgress => 'IN_PROGRESS',
        model.Status.resolved => 'RESOLVED',
        model.Status.rejected => 'REJECTED',
        model.Status.cancelled => 'CANCELLED',
      };

  String _mapPriority(model.Priority p) => switch (p) {
        model.Priority.low => 'LOW',
        model.Priority.normal => 'NORMAL',
        model.Priority.high => 'HIGH',
      };
}