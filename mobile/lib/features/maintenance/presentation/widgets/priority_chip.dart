import 'package:flutter/material.dart';
import 'package:mobile/core/theme/app_colors.dart';
import 'package:mobile/features/maintenance/data/models/priority.dart';

class PriorityChip extends StatelessWidget {
  final Priority priority;
  
  const PriorityChip({super.key, required this.priority});

  @override
  Widget build(BuildContext context) {
    final (color, icon) = _getPriorityData();
    
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
          Icon(icon, size: 14, color: color),
          const SizedBox(width: 4),
          Text(
            priority.getDisplayName(),
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

  (Color, IconData) _getPriorityData() {
    switch (priority) {
      case Priority.low:
        return (AppColors.success, Icons.arrow_downward);
      case Priority.normal:
        return (AppColors.warning, Icons.remove);
      case Priority.high:
        return (AppColors.error, Icons.arrow_upward);
    }
  }
}
