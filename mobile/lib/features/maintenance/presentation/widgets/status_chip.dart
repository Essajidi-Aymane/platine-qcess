import 'package:flutter/material.dart';
import 'package:mobile/features/maintenance/data/models/status.dart';

class StatusChip extends StatelessWidget {
  final Status status;
  
  const StatusChip({super.key, required this.status});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final (color, icon) = _getStatusData(theme);
    
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

  (Color, IconData) _getStatusData(ThemeData theme) {
    switch (status) {
      case Status.open:
        return (Colors.orange, Icons.pending);
      case Status.inProgress:
        return (Colors.blue, Icons.autorenew);
      case Status.resolved:
        return (Colors.green, Icons.check_circle);
      case Status.rejected:
        return (Colors.red, Icons.cancel);
      case Status.cancelled:
        return (theme.colorScheme.onSurfaceVariant, Icons.block);
    }
  }
}