import 'package:flutter/material.dart';
import 'package:mobile/core/theme/app_colors.dart';
import 'package:mobile/features/maintenance/data/models/status.dart';

class StatusChip extends StatelessWidget {
  final Status status;
  
  const StatusChip({super.key, required this.status});

  @override
  Widget build(BuildContext context) {
    final (color, icon) = _getStatusData();
    
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(
          color: color.withOpacity(0.3),
          width: 1,
        ),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 8,
            height: 8,
            decoration: BoxDecoration(
              color: color,
              shape: BoxShape.circle,
            ),
          ),
          const SizedBox(width: 6),
          Text(
            status.getDisplayName(),
            style: TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.w600,
              color: color,
            ),
          ),
        ],
      ),
    );
  }

  (Color, IconData) _getStatusData() {
    switch (status) {
      case Status.open:
        return (AppColors.warning, Icons.pending);
      case Status.inProgress:
        return (AppColors.info, Icons.autorenew);
      case Status.resolved:
        return (AppColors.success, Icons.check_circle);
      case Status.rejected:
        return (AppColors.error, Icons.cancel);
      case Status.cancelled:
        return (AppColors.textSecondary, Icons.block);
    }
  }
}