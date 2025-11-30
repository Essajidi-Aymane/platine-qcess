import 'package:flutter_test/flutter_test.dart';
import 'package:mobile/features/maintenance/data/dto/add_comment_request.dart';
import 'package:mobile/features/maintenance/data/dto/create_ticket_request.dart';
import 'package:mobile/features/maintenance/data/dto/update_ticket_request.dart';
import 'package:mobile/features/maintenance/data/models/priority.dart' as model;
import 'package:mobile/features/maintenance/data/models/status.dart' as model;
import 'package:mobile/features/maintenance/data/dto/ticket_dto.dart';

void main() {
  group('MaintenanceRepository - mapping basique', () {

    test('CreateTicketRequest.toJson mappe correctement la priorité', () {
      final request = CreateTicketRequest(
        title: '  Titre  ',
        description: '  Desc  ',
        priority: model.Priority.high,
      );

      final json = request.toJson();

      expect(json['title'], 'Titre');
      expect(json['description'], 'Desc');
      expect(json['priority'], 'HIGH');
    });

    test('UpdateTicketRequest.toJson nettoie les champs', () {
      final request = UpdateTicketRequest(
        title: '  Titre2  ',
        description: '  Desc2  ',
      );

      final json = request.toJson();

      expect(json['title'], 'Titre2');
      expect(json['description'], 'Desc2');
    });

    test('AddCommentRequest.toJson contient le contenu', () {
      final request = AddCommentRequest(content: '  hello  ');

      final json = request.toJson();

      expect(json['content'], 'hello');
    });

    test('TicketDTO.fromJson parse les champs principaux', () {
      final now = DateTime.now().toIso8601String();
      final json = {
        'id': 1,
        'title': 'Test',
        'description': 'Desc',
        'priority': 'NORMAL',
        'priorityColor': 'blue',
        'status': 'OPEN',
        'comments': <dynamic>[],
        'createdByUserId': 1,
        'createdByUserName': 'User',
        'organizationId': 1,
        'createdAt': now,
        'updatedAt': now,
      };

      final dto = TicketDTO.fromJson(json);

      expect(dto.id, 1);
      expect(dto.title, 'Test');
      expect(dto.description, 'Desc');
      expect(dto.priority, model.Priority.normal);
      expect(dto.status, model.Status.open);
      expect(dto.comments, isEmpty);
    });
  });
}
