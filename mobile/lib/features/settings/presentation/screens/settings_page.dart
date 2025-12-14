import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mobile/core/presentation/widgets/scaffold_with_nav_bar.dart';
import 'package:mobile/core/rooting/app_routes.dart';
import 'package:mobile/core/theme/app_theme.dart';
import 'package:mobile/features/auth/logic/bloc/auth_bloc.dart';
import 'package:mobile/features/auth/logic/bloc/auth_event.dart';
import 'package:mobile/features/auth/logic/bloc/auth_state.dart';
import 'package:mobile/features/theme/logic/bloc/theme_bloc.dart';
import 'package:mobile/features/theme/logic/bloc/theme_event.dart';
import 'package:mobile/features/theme/logic/bloc/theme_state.dart';

class SettingsPage extends StatelessWidget {
  const SettingsPage({super.key});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(title: const Text('Paramètres')),
      body: ListView(
        controller: ScaffoldWithNavBar.getScrollController(
          3,
        ), // Index 3 pour l'onglet Paramètres
        padding: const EdgeInsets.symmetric(vertical: AppTheme.spacingMedium),
        children: [
          _SectionHeader(title: 'Profil'),
          _SettingsTile(
            icon: Icons.person_outline,
            title: 'Mon profil',
            subtitle: 'Modifier nom, prénom, email et photo',
            onTap: () => context.push(AppRoutes.settingsProfile),
          ),
          const Divider(height: 1, indent: 56),

          const SizedBox(height: AppTheme.spacingLarge),

          _SectionHeader(title: 'Compte'),
          _SettingsTile(
            icon: Icons.logout,
            title: 'Se déconnecter',
            subtitle: 'Déconnexion de votre compte',
            iconColor: theme.colorScheme.error,
            titleColor: theme.colorScheme.error,
            onTap: () => _showLogoutDialog(context),
          ),

          const SizedBox(height: AppTheme.spacingLarge),

          _SectionHeader(title: 'Apparence'),
          BlocBuilder<ThemeBloc, ThemeState>(
            builder: (context, state) {
              return _SettingsTile(
                icon: _getThemeIcon(state.themeMode),
                title: 'Thème',
                subtitle: _getThemeLabel(state.themeMode),
                onTap: () => _showThemeDialog(context, state.themeMode),
              );
            },
          ),

          const SizedBox(height: AppTheme.spacingLarge),

          _SectionHeader(title: 'Notifications'),
          _SettingsTile(
            icon: Icons.notifications_outlined,
            title: 'Notifications',
            subtitle: 'Gérer les notifications',
            onTap: () => context.push(AppRoutes.settingsNotifications),
          ),

          const SizedBox(height: AppTheme.spacingLarge),

          _SectionHeader(title: 'Aide & À propos'),
          _SettingsTile(
            icon: Icons.help_outline,
            title: 'Aide',
            subtitle: 'Contacter le support',
            onTap: () => context.push(AppRoutes.settingsHelp),
          ),
          const Divider(height: 1, indent: 56),
          _SettingsTile(
            icon: Icons.info_outline,
            title: 'À propos',
            subtitle: 'Version et informations légales',
            onTap: () => context.push(AppRoutes.settingsAbout),
          ),

          const SizedBox(height: AppTheme.spacingXLarge),
        ],
      ),
    );
  }

  String _getThemeLabel(ThemeMode mode) {
    switch (mode) {
      case ThemeMode.system:
        return 'Système';
      case ThemeMode.dark:
        return 'Sombre';
      case ThemeMode.light:
        return 'Clair';
    }
  }

  IconData _getThemeIcon(ThemeMode mode) {
    switch (mode) {
      case ThemeMode.system:
        return Icons.brightness_auto_outlined;
      case ThemeMode.dark:
        return Icons.dark_mode_outlined;
      case ThemeMode.light:
        return Icons.light_mode_outlined;
    }
  }

  void _showLogoutDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Se déconnecter'),
        content: const Text('Voulez-vous vraiment vous déconnecter ?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(dialogContext).pop(),
            child: const Text('Annuler'),
          ),
          TextButton(
            onPressed: () {
              Navigator.of(dialogContext).pop();
              final authState = context.read<AuthBloc>().state;
              if (authState is AuthAuthenticated) {
                context.read<AuthBloc>().add(
                  LogoutRequested(token: authState.token),
                );
              }
            },
            style: TextButton.styleFrom(
              foregroundColor: Theme.of(dialogContext).colorScheme.error,
            ),
            child: const Text('Déconnexion'),
          ),
        ],
      ),
    );
  }

  void _showThemeDialog(BuildContext context, ThemeMode currentMode) {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(
          top: Radius.circular(AppTheme.radiusLarge),
        ),
      ),
      builder: (sheetContext) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const SizedBox(height: AppTheme.spacingMedium),
            Container(
              width: 40,
              height: 4,
              decoration: BoxDecoration(
                color: Colors.grey[300],
                borderRadius: BorderRadius.circular(2),
              ),
            ),
            const SizedBox(height: AppTheme.spacingMedium),
            Text(
              'Choisir le thème',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: AppTheme.spacingSmall),
            RadioListTile<ThemeMode>(
              title: const Text('Système'),
              subtitle: const Text('Suivre les paramètres de l\'appareil'),
              value: ThemeMode.system,
              groupValue: currentMode,
              onChanged: (value) {
                if (value != null) {
                  context.read<ThemeBloc>().add(ThemeChanged(value));
                  Navigator.of(sheetContext).pop();
                }
              },
            ),
            RadioListTile<ThemeMode>(
              title: const Text('Sombre'),
              subtitle: const Text('Toujours utiliser le thème sombre'),
              value: ThemeMode.dark,
              groupValue: currentMode,
              onChanged: (value) {
                if (value != null) {
                  context.read<ThemeBloc>().add(ThemeChanged(value));
                  Navigator.of(sheetContext).pop();
                }
              },
            ),
            RadioListTile<ThemeMode>(
              title: const Text('Clair'),
              subtitle: const Text('Toujours utiliser le thème clair'),
              value: ThemeMode.light,
              groupValue: currentMode,
              onChanged: (value) {
                if (value != null) {
                  context.read<ThemeBloc>().add(ThemeChanged(value));
                  Navigator.of(sheetContext).pop();
                }
              },
            ),
            const SizedBox(height: AppTheme.spacingMedium),
          ],
        ),
      ),
    );
  }
}

class _SectionHeader extends StatelessWidget {
  final String title;

  const _SectionHeader({required this.title});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Padding(
      padding: const EdgeInsets.symmetric(
        horizontal: AppTheme.spacingMedium,
        vertical: AppTheme.spacingSmall,
      ),
      child: Text(
        title.toUpperCase(),
        style: theme.textTheme.labelMedium?.copyWith(
          color: theme.colorScheme.onSurfaceVariant,
          fontWeight: FontWeight.w600,
          letterSpacing: 0.8,
        ),
      ),
    );
  }
}

class _SettingsTile extends StatelessWidget {
  final IconData icon;
  final String title;
  final String? subtitle;
  final VoidCallback? onTap;
  final Color? iconColor;
  final Color? titleColor;
  final Widget? trailing;

  const _SettingsTile({
    required this.icon,
    required this.title,
    this.subtitle,
    this.onTap,
    this.iconColor,
    this.titleColor,
    this.trailing,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final defaultColor = theme.colorScheme.primary;

    return ListTile(
      leading: Container(
        width: 40,
        height: 40,
        decoration: BoxDecoration(
          color: (iconColor ?? defaultColor).withOpacity(0.1),
          borderRadius: BorderRadius.circular(AppTheme.radiusSmall),
        ),
        child: Icon(icon, color: iconColor ?? defaultColor, size: 22),
      ),
      title: Text(
        title,
        style: theme.textTheme.bodyLarge?.copyWith(
          color: titleColor,
          fontWeight: FontWeight.w500,
        ),
      ),
      subtitle: subtitle != null
          ? Text(
              subtitle!,
              style: theme.textTheme.bodySmall?.copyWith(
                color: theme.colorScheme.onSurfaceVariant,
              ),
            )
          : null,
      trailing:
          trailing ??
          Icon(Icons.chevron_right, color: theme.colorScheme.outline),
      onTap: onTap,
    );
  }
}
