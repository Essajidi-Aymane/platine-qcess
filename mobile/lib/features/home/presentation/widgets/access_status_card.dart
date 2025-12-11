import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:mobile/core/theme/app_colors.dart';
import 'package:mobile/core/theme/app_theme.dart';

class AccessStatusCard extends StatelessWidget {
  final DateTime? lastAccess;
  final bool? lastAccessGranted;
  final String? lastAccessReason;

  const AccessStatusCard({
    super.key,
    this.lastAccess,
    this.lastAccessGranted,
    this.lastAccessReason,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Container(
      decoration: BoxDecoration(
        color: AppColors.cardBackground,
        borderRadius: BorderRadius.circular(AppTheme.radiusLarge),
        boxShadow: AppTheme.shadowSmall,
      ),
      child: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(
                color: lastAccessGranted == null
                  ? AppColors.textSecondary.withOpacity(0.1)
                  : (lastAccessGranted! ? AppColors.success.withOpacity(0.1) : AppColors.error.withOpacity(0.1)),
              borderRadius: BorderRadius.only(
                topLeft: Radius.circular(AppTheme.radiusLarge),
                topRight: Radius.circular(AppTheme.radiusLarge),
              ),
            ),
            child: Row(
              children: [
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: lastAccessGranted == null
                      ? AppColors.textSecondary.withOpacity(0.2)
                      : (lastAccessGranted! ? AppColors.success.withOpacity(0.2) : AppColors.error.withOpacity(0.2)),
                    borderRadius: BorderRadius.circular(12),
                  ),
                    child: Icon(
                      lastAccessGranted == null
                          ? Icons.help_outline
                          : (lastAccessGranted! ? Icons.check_circle : Icons.cancel),
                      color: lastAccessGranted == null
                          ? AppColors.textSecondary
                          : (lastAccessGranted! ? AppColors.success : AppColors.error),
                      size: 28,
                    ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        lastAccessGranted == null
                            ? 'Aucun accès récent'
                            : (lastAccessGranted! ? 'Accès autorisé' : 'Accès refusé'),
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                          color: lastAccessGranted == null
                              ? AppColors.textSecondary
                              : (lastAccessGranted! ? AppColors.success : AppColors.error),
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        _getStatusSubtitle(),
                        style: TextStyle(
                          fontSize: 13,
                          color: AppColors.textSecondary,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),

          Container(
            height: 1,
            color: AppColors.borderLight,
          ),

          Padding(
            padding: const EdgeInsets.all(20),
            child: Column(
              children: [
                _buildInfoRow(
                  icon: Icons.schedule,
                  label: 'Dernière tentative',
                  value: _formatLastAccess(),
                  iconColor: AppColors.primary,
                ),

                if (lastAccessGranted != null && lastAccessGranted == false && lastAccessReason != null && lastAccessReason!.isNotEmpty) ...[
                  const SizedBox(height: 16),
                  _buildReasonCard(),
                ],
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildInfoRow({
    required IconData icon,
    required String label,
    required String value,
    required Color iconColor,
  }) {
    return Row(
      children: [
        Container(
          padding: const EdgeInsets.all(8),
          decoration: BoxDecoration(
            color: iconColor.withOpacity(0.1),
            borderRadius: BorderRadius.circular(8),
          ),
          child: Icon(
            icon,
            size: 20,
            color: iconColor,
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                label,
                style: TextStyle(
                  fontSize: 12,
                  color: AppColors.textSecondary,
                  fontWeight: FontWeight.w500,
                ),
              ),
              const SizedBox(height: 2),
              Text(
                value,
                style: const TextStyle(
                  fontSize: 15,
                  fontWeight: FontWeight.w600,
                  color: AppColors.text,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildReasonCard() {
    final isGranted = lastAccessGranted == null ? null : lastAccessGranted!;
    final color = isGranted == null
        ? AppColors.textSecondary.withOpacity(0.05)
        : (isGranted ? AppColors.success.withOpacity(0.05) : AppColors.error.withOpacity(0.05));
    final borderColor = isGranted == null
        ? AppColors.textSecondary.withOpacity(0.2)
        : (isGranted ? AppColors.success.withOpacity(0.2) : AppColors.error.withOpacity(0.2));
    final iconColor = isGranted == null
        ? AppColors.textSecondary
        : (isGranted ? AppColors.success : AppColors.error);
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: color,
        borderRadius: BorderRadius.circular(AppTheme.radiusMedium),
        border: Border.all(
          color: borderColor,
          width: 1,
        ),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(
            Icons.info_outline,
            size: 20,
            color: iconColor,
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Raison',
                  style: TextStyle(
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
                    color: iconColor,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  lastAccessReason ?? '',
                  style: TextStyle(
                    fontSize: 14,
                    color: AppColors.text,
                    height: 1.4,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  String _getStatusSubtitle() {
    if (lastAccessGranted == null) {
      return 'Aucun accès récent';
    } else if (lastAccessGranted!) {
      return 'Vous pouvez accéder aux zones autorisées';
    } else {
      return 'Contactez l\'administrateur si nécessaire';
    }
  }

  String _formatLastAccess() {
    if (lastAccess == null) return 'Aucun accès récent';

    final now = DateTime.now();
    var difference = now.difference(lastAccess!);
    if (difference.isNegative) {
      difference = Duration(seconds: 0);
    }

    if (difference.inSeconds < 60) {
      return 'Il y a quelques secondes';
    } else if (difference.inMinutes < 60) {
      final minutes = difference.inMinutes;
      return 'Il y a $minutes min';
    } else if (difference.inHours < 24) {
      final hours = difference.inHours;
      return 'Il y a ${hours}h';
    } else if (difference.inDays == 1) {
      return 'Hier à ${DateFormat('HH:mm').format(lastAccess!)}';
    } else if (difference.inDays < 7) {
      return 'Il y a ${difference.inDays} jours';
    } else {
      return DateFormat('dd/MM/yyyy à HH:mm').format(lastAccess!);
    }
  }
}