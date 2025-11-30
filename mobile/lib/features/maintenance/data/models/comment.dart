enum CommentType {
  user,
  admin;

  String getDisplayName() {
    return switch (this) {
      CommentType.user => 'Utilisateur',
      CommentType.admin => 'Admin',
    };
  }
}

class Comment {
  final int? id;
  final String content;
  final int authorUserId;
  final String? authorUserName;
  final CommentType type;
  final DateTime createdAt;

  Comment({
    this.id,
    required this.content,
    required this.authorUserId,
    this.authorUserName,
    required this.type,
    required this.createdAt,
  });

  factory Comment.fromJson(Map<String, dynamic> json) {
    return Comment(
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