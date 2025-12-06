import 'package:flutter/material.dart';

class ProfileInfoField extends StatelessWidget {
  final IconData icon;
  final String label;
  final TextEditingController controller;
  final bool enabled;
  final TextInputType? keyboardType;

  const ProfileInfoField({
    super.key,
    required this.icon,
    required this.label,
    required this.controller,
    this.enabled = false,
    this.keyboardType,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          padding: const EdgeInsets.all(10),
          decoration: BoxDecoration(
            color: theme.colorScheme.primary.withValues(alpha: 0.1),
            borderRadius: BorderRadius.circular(10),
          ),
          child: Icon(icon, color: theme.colorScheme.primary, size: 20),
        ),
        const SizedBox(width: 16),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                label,
                style: theme.textTheme.bodySmall?.copyWith(
                      color: theme.colorScheme.onSurfaceVariant,
                    ),
              ),
              const SizedBox(height: 4),
              enabled
                  ? TextFormField(
                      controller: controller,
                      keyboardType: keyboardType,
                      decoration: InputDecoration(
                        isDense: true,
                        contentPadding: const EdgeInsets.symmetric(
                          horizontal: 12,
                          vertical: 10,
                        ),
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(8),
                        ),
                        hintText: 'Non renseigné',
                      ),
                    )
                  : Text(
                      controller.text.isNotEmpty
                          ? controller.text
                          : 'Non renseigné',
                      style: theme.textTheme.bodyLarge?.copyWith(
                            color: controller.text.isEmpty
                                ? theme.colorScheme.onSurfaceVariant
                                : null,
                          ),
                    ),
            ],
          ),
        ),
      ],
    );
  }
}