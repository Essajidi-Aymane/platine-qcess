import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:mobile/core/theme/app_colors.dart';
import 'package:mobile/core/theme/app_theme.dart';
import 'package:mobile/features/notification/data/dto/notification_dto.dart';

class NotificationCard extends StatelessWidget {
  final NotificationDto notification;
  final VoidCallback? onTap;
  final VoidCallback? onMarkAsRead;

  const NotificationCard({
    super.key,
    required this.notification,
    this.onTap,
    this.onMarkAsRead,
  });

  @override
  Widget build(BuildContext context) {
    final isUnread = !notification.read;

    return Container(
      margin: const EdgeInsets.symmetric(
        horizontal: AppTheme.spacingMedium,
        vertical: AppTheme.spacingSmall,
      ),
      decoration: BoxDecoration(
        color: isUnread 
            ? AppColors.primary.withOpacity(0.08) 
            : AppColors.cardBackground,
        borderRadius: BorderRadius.circular(AppTheme.radiusMedium),
        border: isUnread
            ? Border.all(color: AppColors.primary.withOpacity(0.3), width: 1)
            : null,
        boxShadow: [
          BoxShadow(
            color: AppColors.shadowLight,
            blurRadius: 8,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          onTap: () {
            if (isUnread && onMarkAsRead != null) {
              onMarkAsRead!();
            }
            onTap?.call();
          },
          borderRadius: BorderRadius.circular(AppTheme.radiusMedium),
          child: Padding(
            padding: const EdgeInsets.all(AppTheme.spacingMedium),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                _buildIcon(),
                const SizedBox(width: AppTheme.spacingMedium),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Expanded(
                            child: Text(
                              notification.title,
                              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                                fontWeight: isUnread ? FontWeight.w600 : FontWeight.w500,
                                color: AppColors.text,
                              ),
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                            ),
                          ),
                          if (isUnread)
                            Container(
                              width: 8,
                              height: 8,
                              decoration: const BoxDecoration(
                                color: AppColors.primary,
                                shape: BoxShape.circle,
                              ),
                            ),
                        ],
                      ),
                      const SizedBox(height: AppTheme.spacingXSmall),
                      Text(
                        notification.body,
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          color: AppColors.textSecondary,
                        ),
                        maxLines: 2,
                        overflow: TextOverflow.ellipsis,
                      ),
                      const SizedBox(height: AppTheme.spacingSmall),
                      Text(
                        _formatTime(notification.createdAt),
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: AppColors.textHint,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildIcon() {
    final iconData = _getIconForType(notification.type);
    final color = _getColorForType(notification.type);

    return Container(
      padding: const EdgeInsets.all(10),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(AppTheme.radiusSmall),
      ),
      child: Icon(
        iconData,
        color: color,
        size: 22,
      ),
    );
  }

  IconData _getIconForType(String type) {
    switch (type.toUpperCase()) {
      case 'TICKET_CREATED':
        return Icons.confirmation_number_outlined;
      case 'TICKET_UPDATED':
        return Icons.edit_outlined;
      case 'TICKET_COMMENT':
        return Icons.chat_bubble_outline;
      case 'TICKET_RESOLVED':
        return Icons.check_circle_outline;
      case 'TICKET_REJECTED':
        return Icons.cancel_outlined;
      case 'MANUAL':
        return Icons.campaign_outlined;
      case 'SYSTEM':
        return Icons.info_outline;
      default:
        return Icons.notifications_outlined;
    }
  }

  Color _getColorForType(String type) {
    switch (type.toUpperCase()) {
      case 'TICKET_CREATED':
        return AppColors.info;
      case 'TICKET_UPDATED':
        return AppColors.warning;
      case 'TICKET_COMMENT':
        return AppColors.primary;
      case 'TICKET_RESOLVED':
        return AppColors.success;
      case 'TICKET_REJECTED':
        return AppColors.error;
      case 'MANUAL':
        return AppColors.historyColor;
      case 'SYSTEM':
        return AppColors.textSecondary;
      default:
        return AppColors.primary;
    }
  }

  String _formatTime(DateTime dateTime) {
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final yesterday = today.subtract(const Duration(days: 1));
    final notificationDate = DateTime(dateTime.year, dateTime.month, dateTime.day);

    final timeFormat = DateFormat('HH:mm', 'fr_FR');
    final dateFormat = DateFormat('dd/MM/yyyy', 'fr_FR');

    if (notificationDate == today) {
      return timeFormat.format(dateTime);
    }
    else if (notificationDate == yesterday) {
      return 'Hier à ${timeFormat.format(dateTime)}';
    }
    else {
      return dateFormat.format(dateTime);
    }
  }
}
