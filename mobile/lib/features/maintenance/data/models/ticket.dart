
import 'package:mobile/features/maintenance/data/models/status.dart';
import 'package:mobile/features/maintenance/data/models/priority.dart';
import 'package:mobile/features/maintenance/data/models/comment.dart';

class Ticket {
  final int? id;
  final String title;
  final String description;
  final Priority priority;
  final Status status;
  final int createdByUserId;
  final String createdByUserName;
  final int organizationId;
  final List<Comment> comments;
  final DateTime createdAt;
  final DateTime? updatedAt;

  Ticket({
    this.id,
    required this.title,
    required this.description,
    required this.priority,
    required this.status,
    required this.createdByUserId,
    required this.createdByUserName,
    required this.organizationId,
    this.comments = const [],
    required this.createdAt,
    this.updatedAt,
  });

  factory Ticket.fromJson(Map<String, dynamic> json) {
    return Ticket(
      id: json['id'],
      title: json['title'],
      description: json['description'],
      priority: Priority.values.byName((json['priority'] as String).toLowerCase()),
      status: Status.values.byName((json['status'] as String).toLowerCase()),
      createdByUserId: json['createdByUserId'],
      createdByUserName: json['createdByUserName'],
      organizationId: json['organizationId'],
      comments: (json['comments'] as List<dynamic>?)
              ?.map((c) => Comment.fromJson(c as Map<String, dynamic>))
              .toList() ??
          [],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt:
          json['updatedAt'] != null ? DateTime.parse(json['updatedAt']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'description': description,
      'priority': priority.toString().split('.').last.toUpperCase(),
      'status': status.toString().split('.').last.toUpperCase(),
      'createdByUserId': createdByUserId,
      'createdByUserName': createdByUserName,
      'organizationId': organizationId,
      'comments': comments.map((c) => c.toJson()).toList(),
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }

  bool isOpen() => status == Status.open;
  bool isInProgress() => status == Status.inProgress;
  bool isResolved() => status == Status.resolved;
  bool isRejected() => status == Status.rejected;
  bool isCancelled() => status == Status.cancelled;

  bool belongsTo(int userId) => createdByUserId == userId;
  bool belongsToOrganization(int orgId) => organizationId == orgId;
}

