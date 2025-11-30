import 'package:mobile/features/maintenance/data/models/comment.dart';

class CommentDTO {
  final int id;
  final String content;
  final int authorUserId;
  final String authorUserName;
  final CommentType type;
  final DateTime createdAt;

  CommentDTO({
    required this.id,
    required this.content,
    required this.authorUserId,
    required this.authorUserName,
    required this.type,
    required this.createdAt,
  });

  factory CommentDTO.fromJson(Map<String, dynamic> json) {
    return CommentDTO(
      id: json['id'],
      content: json['content'],
      authorUserId: json['authorUserId'],
      authorUserName: json['authorUserName'],
      type: CommentType.values.byName((json['type'] as String).toLowerCase()),
      createdAt: DateTime.parse(json['createdAt']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'content': content,
      'authorUserId': authorUserId,
      'authorUserName': authorUserName,
      'type': type.name.toUpperCase(),
      'createdAt': createdAt.toIso8601String(),
    };
  }
}