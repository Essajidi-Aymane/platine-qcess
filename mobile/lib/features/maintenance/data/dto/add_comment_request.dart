class AddCommentRequest {
  final String content;

  AddCommentRequest({
    required this.content,
  });

  Map<String, dynamic> toJson() {
    return {
      'content': content.trim(),
    };
  }

  String? validateContent() {
    if (content.trim().isEmpty) {
      return 'Le contenu du commentaire est requis';
    }
    return null;
  }

  bool isValid() {
    return validateContent() == null;
  }
}