import 'package:mobile/features/maintenance/data/models/comment.dart';
import 'package:mobile/features/maintenance/data/models/priority.dart';
import 'package:mobile/features/maintenance/data/models/status.dart';

class TicketDTO {
  final int id;
  final String title;
  final String description;
  final Priority priority;
  final String priorityColor;
  final Status status;
  final List<Comment> comments;
  final int? createdByUserId;
  final String? createdByUserName;
  final int? organizationId;
  final DateTime createdAt;
  final DateTime? updatedAt;

  TicketDTO({
    required this.id,
    required this.title,
    required this.description,
    required this.priority,
    required this.priorityColor,
    required this.status,
    required this.comments,
    this.createdByUserId,
    this.createdByUserName,
    this.organizationId,
    required this.createdAt,
    this.updatedAt,
  });

  factory TicketDTO.fromJson(Map<String, dynamic> json) {
    return TicketDTO(
      id: json['id'],
      title: json['title'],
      description: json['description'],
      priority: Priority.values.byName((json['priority'] as String).toLowerCase()),
      priorityColor: json['priorityColor'],
      status: Status.values.byName(_convertStatusFromBackend(json['status'] as String)),
      comments: (json['comments'] as List<dynamic>?)
              ?.map((c) => Comment.fromJson(c as Map<String, dynamic>))
              .toList() ??
          [],
      createdByUserId: json['createdByUserId'] as int?,
      createdByUserName: json['createdByUserName'] as String?,
      organizationId: json['organizationId'] as int?,
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'description': description,
      'priority': priority.name.toUpperCase(),
      'priorityColor': priorityColor,
      'status': _convertStatusToBackend(status),
      'comments': comments.map((c) => c.toJson()).toList(),
      'createdByUserId': createdByUserId,
      'createdByUserName': createdByUserName,
      'organizationId': organizationId,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }

  static String _convertStatusFromBackend(String backendStatus) {
    switch (backendStatus.toUpperCase()) {
      case 'OPEN':
        return 'open';
      case 'IN_PROGRESS':
        return 'inProgress';
      case 'RESOLVED':
        return 'resolved';
      case 'REJECTED':
        return 'rejected';
      case 'CANCELLED':
        return 'cancelled';
      default:
        return 'open';
    }
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