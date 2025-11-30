import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:mobile/core/theme/app_colors.dart';
import 'package:mobile/features/maintenance/data/dto/ticket_dto.dart';
import 'package:mobile/features/maintenance/presentation/widgets/priority_chip.dart';
import 'package:mobile/features/maintenance/presentation/widgets/status_chip.dart';

class TicketInfoSection extends StatelessWidget {
  final TicketDTO ticket;
  const TicketInfoSection({super.key, required this.ticket});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              StatusChip(status: ticket.status),
              const SizedBox(width: 8),
              PriorityChip(priority: ticket.priority),
              const Spacer(),
              Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Text(
                    'Créé le',
                    style: TextStyle(fontSize: 11, color: AppColors.textSecondary),
                  ),
                  Text(
                    DateFormat('dd/MM/yyyy').format(ticket.createdAt),
                    style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w600, color: AppColors.text),
                  ),
                ],
              ),
            ],
          ),
          const SizedBox(height: 20),
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: AppColors.cardBackground,
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: AppColors.borderLight, width: 1),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: const [
                    Icon(Icons.description_outlined, size: 18, color: AppColors.primary),
                    SizedBox(width: 8),
                    Text('Description', style: TextStyle(fontSize: 14, fontWeight: FontWeight.w600, color: AppColors.text)),
                  ],
                ),
                const SizedBox(height: 12),
                Text(
                  ticket.description,
                  style: TextStyle(fontSize: 14, color: AppColors.textSecondary, height: 1.5),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}