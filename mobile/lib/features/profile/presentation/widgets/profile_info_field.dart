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
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          padding: const EdgeInsets.all(10),
          decoration: BoxDecoration(
            color: Theme.of(context).colorScheme.primary.withOpacity(0.1),
            borderRadius: BorderRadius.circular(10),
          ),
          child: Icon(
            icon,
            color: Theme.of(context).colorScheme.primary,
            size: 20,
          ),
        ),
        const SizedBox(width: 16),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(label, style: Theme.of(context).textTheme.labelMedium),
              const SizedBox(height: 6),
              enabled
                  ? TextFormField(
                      controller: controller,
                      keyboardType: keyboardType,
                      style: Theme.of(context).textTheme.bodyMedium,
                      decoration: InputDecoration(
                        isDense: true,
                        filled: true,
                        fillColor: Theme.of(context).scaffoldBackgroundColor,
                        contentPadding: const EdgeInsets.symmetric(
                          horizontal: 12,
                          vertical: 10,
                        ),
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(8),
                          borderSide: BorderSide(
                            color: Theme.of(context).dividerColor,
                          ),
                        ),
                        enabledBorder: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(8),
                          borderSide: BorderSide(
                            color: Theme.of(context).dividerColor,
                          ),
                        ),
                        focusedBorder: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(8),
                          borderSide: BorderSide(
                            color: Theme.of(context).colorScheme.primary,
                            width: 2,
                          ),
                        ),
                        hintText: 'Non renseigné',
                        hintStyle: TextStyle(
                          color: Theme.of(context).hintColor.withOpacity(0.5),
                        ),
                      ),
                    )
                  : Text(
                      controller.text.isNotEmpty
                          ? controller.text
                          : 'Non renseigné',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: controller.text.isEmpty
                            ? Theme.of(context).hintColor
                            : null,
                        fontWeight: controller.text.isEmpty
                            ? FontWeight.normal
                            : FontWeight.w500,
                      ),
                    ),
            ],
          ),
        ),
      ],
    );
  }
}
