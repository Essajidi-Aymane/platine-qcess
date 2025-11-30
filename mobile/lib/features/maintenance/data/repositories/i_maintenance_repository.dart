
import 'package:mobile/features/maintenance/data/dto/add_comment_request.dart';
import 'package:mobile/features/maintenance/data/dto/create_ticket_request.dart';
import 'package:mobile/features/maintenance/data/dto/ticket_dto.dart';
import 'package:mobile/features/maintenance/data/dto/update_ticket_request.dart';
import 'package:mobile/features/maintenance/data/models/status.dart' as model;
import 'package:mobile/features/maintenance/data/models/priority.dart' as model;

abstract class IMaintenanceRepository {
  Future<List<TicketDTO>> getMyTickets({model.Status? status, model.Priority? priority});
  Future<TicketDTO> getTicketById(int id);
  Future<TicketDTO> createTicket(CreateTicketRequest request);
  Future<TicketDTO> updateTicket(int id, UpdateTicketRequest request);
  Future<TicketDTO> cancelTicket(int id);
  Future<TicketDTO> addUserComment(int ticketId, AddCommentRequest request);
}