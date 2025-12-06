import 'package:flutter/material.dart';
import 'package:mobile/features/profile/presentation/widgets/profile_avatar.dart';

class ProfileAppBar extends StatelessWidget {
  final String? imageUrl;
  final String initials;
  final bool isEditing;
  final VoidCallback onBackPressed;
  final VoidCallback onEditToggle;
  final VoidCallback? onChangePhoto;

  const ProfileAppBar({
    super.key,
    this.imageUrl,
    required this.initials,
    required this.isEditing,
    required this.onBackPressed,
    required this.onEditToggle,
    this.onChangePhoto,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    
    return SliverAppBar(
      expandedHeight: 200,
      floating: false,
      pinned: true,
      stretch: true,
      leading: IconButton(
        icon: Container(
          padding: const EdgeInsets.all(8),
          decoration: BoxDecoration(
            color: Colors.black26,
            borderRadius: BorderRadius.circular(12),
          ),
          child: const Icon(Icons.arrow_back, color: Colors.white),
        ),
        onPressed: onBackPressed,
      ),
      actions: [
        IconButton(
          icon: Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: Colors.black26,
              borderRadius: BorderRadius.circular(12),
            ),
            child: Icon(
              isEditing ? Icons.close : Icons.edit,
              color: Colors.white,
            ),
          ),
          onPressed: onEditToggle,
        ),
        const SizedBox(width: 8),
      ],
      flexibleSpace: FlexibleSpaceBar(
        background: Container(
          decoration: BoxDecoration(
            gradient: LinearGradient(
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
              colors: [
                theme.colorScheme.primary,
                theme.colorScheme.primary.withValues(alpha: 0.7),
              ],
            ),
          ),
          child: SafeArea(
            child: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const SizedBox(height: 20),
                  ProfileAvatar(
                    imageUrl: imageUrl,
                    initials: initials,
                    showEditButton: isEditing,
                    onEditPressed: onChangePhoto,
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}