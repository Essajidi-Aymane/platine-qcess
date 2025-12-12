import 'package:flutter/material.dart';
import 'package:mobile/core/theme/app_colors.dart';

class ProfileHeader extends StatelessWidget {
  final String displayName;
  final String email;

  const ProfileHeader({
    super.key,
    required this.displayName,
    required this.email,
  });

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        children: [
          Text(
            displayName,
            style: const TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.bold,
              color: AppColors.text,
            ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 8),
          Text(
            email,
            style: TextStyle(
              fontSize: 15,
              color: AppColors.textSecondary,
            ),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }
}