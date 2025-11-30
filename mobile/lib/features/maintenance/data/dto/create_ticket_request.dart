import 'package:mobile/features/maintenance/data/models/priority.dart';

class CreateTicketRequest {
  final String title;
  final String description;
  final Priority priority;

  CreateTicketRequest({
    required this.title,
    required this.description,
    required this.priority,
  });

  Map<String, dynamic> toJson() {
    return {
      'title': title.trim(),
      'description': description.trim(),
      'priority': priority.name.toUpperCase(),
    };
  }

  String? validateTitle() {
    if (title.trim().isEmpty) {
      return 'Le titre est requis';
    }
    if (title.trim().length > 200) {
      return 'Le titre ne doit pas dépasser 200 caractères';
    }
    return null;
  }

  String? validateDescription() {
    if (description.trim().isEmpty) {
      return 'La description est requise';
    }
    if (description.trim().length > 5000) {
      return 'La description ne doit pas dépasser 5000 caractères';
    }
    return null;
  }

  bool isValid() {
    return validateTitle() == null && validateDescription() == null;
  }
}