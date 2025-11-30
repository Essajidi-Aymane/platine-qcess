import 'package:equatable/equatable.dart';
import 'package:mobile/features/maintenance/data/dto/ticket_dto.dart';
import 'package:mobile/features/maintenance/data/models/status.dart';
import 'package:mobile/features/maintenance/data/models/priority.dart';

enum TicketsStatus { initial, loading, loaded, submitting, success, failure }

class TicketsState extends Equatable {
  final TicketsStatus status;
  final List<TicketDTO> tickets;
  final List<TicketDTO> visibleTickets;
  final String? error;
  final TicketDTO? selectedTicket;
  final bool isDetailLoading;
  final Status? filterStatus;
  final Priority? filterPriority;
  final String? searchQuery;

  const TicketsState({
    this.status = TicketsStatus.initial,
    this.tickets = const [],
    this.visibleTickets = const [],
    this.error,
    this.selectedTicket,
    this.isDetailLoading = false,
    this.filterStatus,
    this.filterPriority,
    this.searchQuery,
  });

  TicketsState copyWith({
    TicketsStatus? status,
    List<TicketDTO>? tickets,
    List<TicketDTO>? visibleTickets,
    String? error,
    TicketDTO? selectedTicket,
    bool? isDetailLoading,
    Status? filterStatus,
    Priority? filterPriority,
    String? searchQuery,
  }) {
    return TicketsState(
      status: status ?? this.status,
      tickets: tickets ?? this.tickets,
      visibleTickets: visibleTickets ?? this.visibleTickets,
      error: error,
      selectedTicket: selectedTicket ?? this.selectedTicket,
      isDetailLoading: isDetailLoading ?? this.isDetailLoading,
      filterStatus: filterStatus == _undefined ? this.filterStatus : filterStatus as Status?,
      filterPriority: filterPriority == _undefined ? this.filterPriority : filterPriority as Priority?,
      searchQuery: searchQuery ?? this.searchQuery,
    );
  }

  static const _undefined = Object();

  @override
  List<Object?> get props => [status, tickets, visibleTickets, error, selectedTicket, isDetailLoading, filterStatus, filterPriority, searchQuery];
}
