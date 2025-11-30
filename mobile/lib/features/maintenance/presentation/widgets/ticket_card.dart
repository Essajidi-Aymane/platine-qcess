import 'package:flutter/material.dart';
import 'package:mobile/core/theme/app_colors.dart';
import 'package:mobile/core/theme/app_theme.dart';
import 'package:mobile/features/maintenance/data/dto/ticket_dto.dart';
import 'package:mobile/features/maintenance/presentation/widgets/priority_chip.dart';
import 'package:mobile/features/maintenance/presentation/widgets/status_chip.dart';
import 'package:intl/intl.dart';

class TicketCard extends StatelessWidget {
  final TicketDTO ticket;
  final VoidCallback? onTap;
  final VoidCallback? onCancel;

  const TicketCard({
    super.key,
    required this.ticket,
    this.onTap,
    this.onCancel,
  });

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.transparent,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(AppTheme.radiusLarge),
        child: Container(
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(AppTheme.radiusLarge),
            boxShadow: AppTheme.shadowSmall,
          ),
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  _buildStatusIcon(),
                  const SizedBox(width: 12),
                  
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          ticket.title,
                          style: const TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.w600,
                            color: AppColors.text,
                          ),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                        const SizedBox(height: 4),
                        Text(
                          _formatDate(ticket.createdAt),
                          style: TextStyle(
                            fontSize: 12,
                            color: AppColors.textSecondary,
                          ),
                        ),
                      ],
                    ),
                  ),
                  
                  if (onCancel != null)
                    IconButton(
                      icon: const Icon(
                        Icons.close,
                        color: AppColors.error,
                        size: 20,
                      ),
                      onPressed: onCancel,
                      padding: EdgeInsets.zero,
                      constraints: const BoxConstraints(),
                    ),
                ],
              ),
              
              const SizedBox(height: 12),
              
              Text(
                ticket.description,
                style: TextStyle(
                  fontSize: 14,
                  color: AppColors.textSecondary,
                  height: 1.4,
                ),
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
              
              const SizedBox(height: 16),
              
              Row(
                children: [
                  PriorityChip(priority: ticket.priority),
                  const SizedBox(width: 8),
                  StatusChip(status: ticket.status),
                  const Spacer(),
                  Icon(
                    Icons.arrow_forward_ios,
                    size: 16,
                    color: AppColors.textSecondary,
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildStatusIcon() {
    final (color, icon) = _getStatusIconData();
    
    return Container(
      width: 48,
      height: 48,
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Icon(
        icon,
        color: color,
        size: 24,
      ),
    );
  }

  (Color, IconData) _getStatusIconData() {
    switch (ticket.status.name.toLowerCase()) {
      case 'open':
        return (AppColors.warning, Icons.pending_outlined);
      case 'inprogress':
        return (AppColors.info, Icons.autorenew);
      case 'resolved':
        return (AppColors.success, Icons.check_circle_outline);
      case 'rejected':
        return (AppColors.error, Icons.cancel_outlined);
      case 'cancelled':
        return (AppColors.textSecondary, Icons.block);
      default:
        return (AppColors.primary, Icons.confirmation_number_outlined);
    }
  }

  String _formatDate(DateTime? date) {
    if (date == null) return 'Date inconnue';
    
    final now = DateTime.now();
    final difference = now.difference(date);
    
    if (difference.inMinutes < 60) {
      return 'Il y a ${difference.inMinutes} min';
    } else if (difference.inHours < 24) {
      return 'Il y a ${difference.inHours}h';
    } else if (difference.inDays < 7) {
      return 'Il y a ${difference.inDays} jours';
    } else {
      return DateFormat('dd/MM/yyyy').format(date);
    }
  }
}