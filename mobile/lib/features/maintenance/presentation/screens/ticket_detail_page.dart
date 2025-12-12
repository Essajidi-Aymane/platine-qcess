import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mobile/features/maintenance/data/dto/add_comment_request.dart';
import 'package:mobile/features/maintenance/data/dto/ticket_dto.dart';
import 'package:mobile/features/maintenance/data/models/status.dart';
import 'package:mobile/features/maintenance/logic/bloc/tickets_bloc.dart';
import 'package:mobile/features/maintenance/logic/bloc/tickets_state.dart';
import 'package:mobile/features/maintenance/logic/bloc/tickets_event.dart';
import 'package:mobile/features/maintenance/presentation/widgets/ticket_detail_header.dart';
import 'package:mobile/features/maintenance/presentation/widgets/ticket_info_section.dart';
import 'package:mobile/features/maintenance/presentation/widgets/ticket_comments_list.dart';
import 'package:mobile/features/maintenance/presentation/widgets/ticket_comment_input.dart';

class TicketDetailPage extends StatefulWidget {
  final int? ticketId;
  final TicketDTO? initialTicket;

  const TicketDetailPage({
    super.key,
    this.ticketId,
    this.initialTicket,
  });

  @override
  State<TicketDetailPage> createState() => _TicketDetailPageState();
}

class _TicketDetailPageState extends State<TicketDetailPage> {
  final _commentCtrl = TextEditingController();
  final _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    if (widget.initialTicket == null && widget.ticketId != null) {
      context.read<TicketsBloc>().add(TicketDetailRequested(widget.ticketId!));
    }
  }

  @override
  void dispose() {
    _commentCtrl.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final bloc = context.read<TicketsBloc>();
    final theme = Theme.of(context);
    
    return BlocBuilder<TicketsBloc, TicketsState>(
      builder: (context, state) {
        final effectiveTicket = _getTicket(state) ?? widget.initialTicket;

        if (effectiveTicket == null) {
          return Scaffold(
            backgroundColor: theme.colorScheme.primary,
            body: SafeArea(
              child: _buildNotFound(context),
            ),
          );
        }

        final isCancelled = effectiveTicket.status == Status.cancelled;

        return Scaffold(
          backgroundColor: theme.colorScheme.primary,
          resizeToAvoidBottomInset: true,
          body: SafeArea(
            child: LayoutBuilder(
              builder: (context, constraints) {
                return Padding(
                  padding: EdgeInsets.only(bottom: MediaQuery.of(context).viewInsets.bottom),
                  child: Column(
                    children: [
                      TicketDetailHeader(
                        ticket: effectiveTicket,
                        onBack: () => context.pop(),
                      ),
                      Expanded(
                        child: Container(
                          decoration: BoxDecoration(
                            color: theme.colorScheme.surface,
                            borderRadius: const BorderRadius.only(
                              topLeft: Radius.circular(32),
                              topRight: Radius.circular(32),
                            ),
                          ),
                          child: CustomScrollView(
                            controller: _scrollController,
                            slivers: [
                              SliverToBoxAdapter(
                                child: TicketInfoSection(ticket: effectiveTicket),
                              ),
                              const SliverToBoxAdapter(
                                child: Divider(height: 1),
                              ),
                              SliverList(
                                delegate: SliverChildBuilderDelegate(
                                  (context, index) {
                                    final comment = effectiveTicket.comments[index];
                                    return TicketCommentsList(
                                      comments: [comment],
                                    );
                                  },
                                  childCount: effectiveTicket.comments.length,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                      if (!isCancelled)
                        TicketCommentInput(
                          controller: _commentCtrl,
                          disabled: state.status == TicketsStatus.submitting ||
                              state.isDetailLoading,
                          onSend: () => _sendComment(bloc, effectiveTicket),
                        ),
                    ],
                  ),
                );
              },
            ),
          ),
        );
      },
    );
  }

  TicketDTO? _getTicket(TicketsState state) {
    if (widget.ticketId != null && state.tickets.isNotEmpty) {
      try {
        return state.tickets.firstWhere((t) => t.id == widget.ticketId);
      } catch (_) {
        return null;
      }
    }
    
    return null;
  }

  Widget _buildNotFound(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.search_off, size: 64, color: Colors.white),
          const SizedBox(height: 16),
          const Text(
            'Ticket introuvable',
            style: TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.bold,
              color: Colors.white,
            ),
          ),
          const SizedBox(height: 24),
          ElevatedButton(
            onPressed: () => context.pop(),
            child: const Text('Retour'),
          ),
        ],
      ),
    );
  }


  void _sendComment(TicketsBloc bloc, TicketDTO ticket) {
    final content = _commentCtrl.text.trim();
    if (content.isEmpty) return;

    bloc.add(UserCommentAdded(
      ticketId: ticket.id,
      request: AddCommentRequest(content: content),
    ));

    _commentCtrl.clear();

    Future.delayed(const Duration(milliseconds: 300), () {
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    });
  }

}