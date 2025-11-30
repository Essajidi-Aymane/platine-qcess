import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:mobile/features/maintenance/data/dto/add_comment_request.dart';
import 'package:mobile/features/maintenance/data/dto/create_ticket_request.dart';
import 'package:mobile/features/maintenance/data/dto/ticket_dto.dart';
import 'package:mobile/features/maintenance/data/dto/update_ticket_request.dart';
import 'package:mobile/features/maintenance/data/models/priority.dart' as model;
import 'package:mobile/features/maintenance/data/models/status.dart' as model;
import 'package:mobile/features/maintenance/data/repositories/i_maintenance_repository.dart';
import 'package:mobile/features/maintenance/logic/bloc/tickets_bloc.dart';
import 'package:mobile/features/maintenance/logic/bloc/tickets_event.dart';
import 'package:mobile/features/maintenance/logic/bloc/tickets_state.dart';

class _MockMaintenanceRepository extends Mock implements IMaintenanceRepository {}

TicketDTO _fakeTicket({
  int id = 1,
  String title = 'Titre',
  String description = 'Desc',
  model.Status status = model.Status.open,
  model.Priority priority = model.Priority.normal,
}) {
  return TicketDTO(
    id: id,
    title: title,
    description: description,
    priority: priority,
    priorityColor: 'blue',
    status: status,
    comments: const [],
    createdByUserId: 1,
    createdByUserName: 'User',
    organizationId: 1,
    createdAt: DateTime.now(),
    updatedAt: DateTime.now(),
  );
}

void main() {
  group('TicketsBloc', () {
    late _MockMaintenanceRepository repository;
    late TicketsBloc bloc;

    setUp(() {
      repository = _MockMaintenanceRepository();
      bloc = TicketsBloc(maintenanceRepository: repository);

      registerFallbackValue(
        CreateTicketRequest(title: 't', description: 'd', priority: model.Priority.normal),
      );
      registerFallbackValue(
        UpdateTicketRequest(title: 't', description: 'd'),
      );
      registerFallbackValue(
        AddCommentRequest(content: 'c'),
      );
    });

    test('initial state', () {
      expect(bloc.state.status, TicketsStatus.initial);
      expect(bloc.state.tickets, isEmpty);
      expect(bloc.state.visibleTickets, isEmpty);
    });

    test('TicketsRequested charge les tickets et met à jour visibleTickets', () async {
      final tickets = [
        _fakeTicket(id: 1, title: 'A'),
        _fakeTicket(id: 2, title: 'B'),
      ];

      when(() => repository.getMyTickets(status: any(named: 'status'), priority: any(named: 'priority')))
          .thenAnswer((_) async => tickets);

      bloc.add(const TicketsRequested());
      await Future.delayed(const Duration(milliseconds: 10));

      expect(bloc.state.status, TicketsStatus.loaded);
      expect(bloc.state.tickets.length, 2);
      expect(bloc.state.visibleTickets.length, 2);
    });

    test('TicketSearchChanged filtre les tickets par titre', () async {
      final tickets = [
        _fakeTicket(id: 1, title: 'ordinateur'),
        _fakeTicket(id: 2, title: 'chaise'),
      ];

      when(() => repository.getMyTickets(status: any(named: 'status'), priority: any(named: 'priority')))
          .thenAnswer((_) async => tickets);

      bloc.add(const TicketsRequested());
      await Future.delayed(const Duration(milliseconds: 10));

      bloc.add(const TicketSearchChanged('ordi'));
      await Future.delayed(const Duration(milliseconds: 350));

      expect(bloc.state.visibleTickets.length, 1);
      expect(bloc.state.visibleTickets.first.title, 'ordinateur');
    });

    test('TicketCreated ajoute le ticket dans la liste', () async {
      final existing = [_fakeTicket(id: 1)];
      when(() => repository.getMyTickets(status: any(named: 'status'), priority: any(named: 'priority')))
          .thenAnswer((_) async => existing);
      when(() => repository.createTicket(any())).thenAnswer((_) async => _fakeTicket(id: 2));

      bloc.add(const TicketsRequested());
      await Future.delayed(const Duration(milliseconds: 10));

      bloc.add(TicketCreated(CreateTicketRequest(title: 'Nouveau', description: 'd', priority: model.Priority.normal)));
      await Future.delayed(const Duration(milliseconds: 10));

      expect(bloc.state.tickets.map((t) => t.id), containsAll([1, 2]));
    });

    test('TicketUpdated remplace le ticket correspondant', () async {
      final existing = [_fakeTicket(id: 1, title: 'Ancien')];
      when(() => repository.getMyTickets(status: any(named: 'status'), priority: any(named: 'priority')))
          .thenAnswer((_) async => existing);
      when(() => repository.updateTicket(1, any())).thenAnswer((_) async => _fakeTicket(id: 1, title: 'Nouveau'));

      bloc.add(const TicketsRequested());
      await Future.delayed(const Duration(milliseconds: 10));

      bloc.add(TicketUpdated(id: 1, request: UpdateTicketRequest(title: 'x', description: 'y')));
      await Future.delayed(const Duration(milliseconds: 10));

      expect(bloc.state.tickets.single.title, 'Nouveau');
    });

    test('TicketCancelled remplace le ticket par la version annulée', () async {
      final existing = [_fakeTicket(id: 1, status: model.Status.open)];
      when(() => repository.getMyTickets(status: any(named: 'status'), priority: any(named: 'priority')))
          .thenAnswer((_) async => existing);
      when(() => repository.cancelTicket(1)).thenAnswer((_) async => _fakeTicket(id: 1, status: model.Status.cancelled));

      bloc.add(const TicketsRequested());
      await Future.delayed(const Duration(milliseconds: 10));

      bloc.add(const TicketCancelled(1));
      await Future.delayed(const Duration(milliseconds: 10));

      expect(bloc.state.tickets.single.status, model.Status.cancelled);
    });

    test('UserCommentAdded met à jour le ticket ciblé', () async {
      final existing = [_fakeTicket(id: 1), _fakeTicket(id: 2)];
      when(() => repository.getMyTickets(status: any(named: 'status'), priority: any(named: 'priority')))
          .thenAnswer((_) async => existing);
      when(() => repository.addUserComment(1, any())).thenAnswer((_) async => _fakeTicket(id: 1));

      bloc.add(const TicketsRequested());
      await Future.delayed(const Duration(milliseconds: 10));

      bloc.add(UserCommentAdded(ticketId: 1, request: AddCommentRequest(content: 'c')));
      await Future.delayed(const Duration(milliseconds: 10));

      expect(bloc.state.tickets.length, 2);
      verify(() => repository.addUserComment(1, any())).called(1);
    });
  });
}
