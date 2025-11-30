import 'package:mobile/features/maintenance/data/models/priority.dart';
import 'package:mobile/features/maintenance/data/models/status.dart';

class TicketFilterRequest {
  final Status? status;
  final Priority? priority;
  final int? organizationId;
  final int? userId;

  TicketFilterRequest({
    this.status,
    this.priority,
    this.organizationId,
    this.userId,
  });

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> json = {};

    if (status != null) {
      json['status'] = _convertStatusToBackend(status!);
    }
    if (priority != null) {
      json['priority'] = priority!.name.toUpperCase();
    }
    if (organizationId != null) {
      json['organizationId'] = organizationId;
    }
    if (userId != null) {
      json['userId'] = userId;
    }

    return json;
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