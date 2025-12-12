import 'package:flutter/material.dart';
import 'package:mobile/features/maintenance/data/models/comment.dart';

class TicketCommentsList extends StatelessWidget {
  final List<Comment> comments;
  final ScrollController? controller;
  const TicketCommentsList({super.key, required this.comments, this.controller});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    if (comments.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.chat_bubble_outline, size: 64, color: theme.colorScheme.onSurfaceVariant.withOpacity(0.3)),
            const SizedBox(height: 16),
            Text('Aucun commentaire', style: TextStyle(fontSize: 16, color: theme.colorScheme.onSurfaceVariant)),
            const SizedBox(height: 8),
            Text('Soyez le premier à commenter', style: TextStyle(fontSize: 14, color: theme.colorScheme.onSurfaceVariant.withOpacity(0.7))),
          ],
        ),
      );
    }

    return ListView.separated(
      controller: controller,
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
      itemCount: comments.length,
      shrinkWrap: true,
      keyboardDismissBehavior: ScrollViewKeyboardDismissBehavior.onDrag,
      separatorBuilder: (_, __) => const SizedBox(height: 16),
      itemBuilder: (context, index) {
        final comment = comments[index];
        final isUserComment = comment.type == CommentType.user;
        return Row(
          mainAxisAlignment: isUserComment ? MainAxisAlignment.end : MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (!isUserComment) ...[
              _buildAvatar(context, false),
              const SizedBox(width: 12),
            ],
            Flexible(
              child: Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: isUserComment ? theme.colorScheme.primary.withOpacity(0.1) : theme.colorScheme.surface,
                  borderRadius: BorderRadius.only(
                    topLeft: Radius.circular(isUserComment ? 16 : 4),
                    topRight: Radius.circular(isUserComment ? 4 : 16),
                    bottomLeft: const Radius.circular(16),
                    bottomRight: const Radius.circular(16),
                  ),
                  border: Border.all(
                    color: isUserComment ? theme.colorScheme.primary.withOpacity(0.2) : theme.colorScheme.outline,
                    width: 1,
                  ),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Icon(isUserComment ? Icons.person : Icons.support_agent, size: 14, color: isUserComment ? theme.colorScheme.primary : Colors.blue),
                        const SizedBox(width: 6),
                        Text(comment.type.getDisplayName(), style: TextStyle(fontSize: 12, fontWeight: FontWeight.w600, color: isUserComment ? theme.colorScheme.primary : Colors.blue)),
                        const Spacer(),
                        Text(comment.createdAt.toIso8601String(), style: TextStyle(fontSize: 11, color: theme.colorScheme.onSurfaceVariant)),
                      ],
                    ),
                    const SizedBox(height: 8),
                    Text(comment.content, style: TextStyle(fontSize: 14, color: theme.colorScheme.onSurface, height: 1.4)),
                  ],
                ),
              ),
            ),
            if (isUserComment) ...[
              const SizedBox(width: 12),
              _buildAvatar(context, true),
            ],
          ],
        );
      },
    );
  }

  Widget _buildAvatar(BuildContext context, bool isUserComment) {
    final theme = Theme.of(context);
    return Container(
      width: 36,
      height: 36,
      decoration: BoxDecoration(
        color: isUserComment ? theme.colorScheme.primary.withOpacity(0.1) : Colors.blue.withOpacity(0.1),
        shape: BoxShape.circle,
        border: Border.all(
          color: isUserComment ? theme.colorScheme.primary.withOpacity(0.3) : Colors.blue.withOpacity(0.3),
          width: 2,
        ),
      ),
      child: Icon(isUserComment ? Icons.person : Icons.support_agent, size: 18, color: isUserComment ? theme.colorScheme.primary : Colors.blue),
    );
  }
}
