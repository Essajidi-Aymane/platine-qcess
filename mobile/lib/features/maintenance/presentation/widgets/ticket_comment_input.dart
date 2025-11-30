import 'package:flutter/material.dart';
import 'package:mobile/core/theme/app_colors.dart';

class TicketCommentInput extends StatelessWidget {
  final TextEditingController controller;
  final bool disabled;
  final VoidCallback onSend;

  const TicketCommentInput({super.key, required this.controller, required this.disabled, required this.onSend});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppColors.cardBackground,
        boxShadow: [
          BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 10, offset: const Offset(0, -5)),
        ],
      ),
      child: SafeArea(
        child: Row(
          children: [
            Expanded(
              child: TextField(
                controller: controller,
                decoration: InputDecoration(
                  hintText: 'Écrivez votre commentaire...',
                  filled: true,
                  fillColor: AppColors.background,
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(24),
                    borderSide: BorderSide.none,
                  ),
                  contentPadding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                ),
                maxLines: null,
                textCapitalization: TextCapitalization.sentences,
              ),
            ),
            const SizedBox(width: 12),
            Container(
              decoration: BoxDecoration(
                gradient: LinearGradient(colors: [AppColors.primary, AppColors.primaryDark]),
                shape: BoxShape.circle,
                boxShadow: [
                  BoxShadow(color: AppColors.primary.withOpacity(0.3), blurRadius: 8, offset: const Offset(0, 4)),
                ],
              ),
              child: IconButton(
                onPressed: disabled ? null : onSend,
                icon: const Icon(Icons.send, color: Colors.white, size: 20),
                padding: const EdgeInsets.all(12),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
