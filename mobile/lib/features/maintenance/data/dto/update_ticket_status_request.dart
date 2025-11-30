import 'package:mobile/features/maintenance/data/models/status.dart';


class UpdateTicketStatusRequest {
  final Status newStatus;

  UpdateTicketStatusRequest({
    required this.newStatus,
  });

  Map<String, dynamic> toJson() {
    return {
      'newStatus': _convertStatusToBackend(newStatus),
    };
  }

  static String _convertStatusToBackend(Status status) {
    switch (status) {
      case Status.open:
        return 'OPEN';
      case Status.inProgress:
        return 'IN_PROGRESS';
      case Status.resolved:
        return 'RESOLVED';
      case Status.rejected:
        return 'REJECTED';
      case Status.cancelled:
        return 'CANCELLED';
    }
  }
}
