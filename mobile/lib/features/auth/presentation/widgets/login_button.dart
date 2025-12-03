import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mobile/core/presentation/widgets/custom_snackbar.dart';
import 'package:mobile/core/presentation/widgets/loading_widget.dart';
import 'package:mobile/features/auth/logic/bloc/auth_bloc.dart';
import 'package:mobile/features/auth/logic/bloc/auth_event.dart';
import 'package:mobile/features/auth/logic/bloc/auth_state.dart';

class LoginButton extends StatelessWidget {
  final GlobalKey<FormState> formKey;
  final TextEditingController emailController;
  final TextEditingController accessCodeController;
  final String buttonText;
  final VoidCallback? onSuccess;

  const LoginButton({
    super.key,
    required this.formKey,
    required this.emailController,
    required this.accessCodeController,
    this.buttonText = 'Se connecter',
    this.onSuccess,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return BlocConsumer<AuthBloc, AuthState>(
      listenWhen: (previous, current) {
        return previous is AuthLoading &&
              (current is AuthAuthenticated || current is AuthUnauthenticated);
      },
      listener: (context, state) {
        if (state is AuthUnauthenticated) {
          CustomSnackBar.showError(
            context,
            message: (state.error?.contains('Invalid') == true)
                ? 'Code d\'accès invalide ou expiré'
                : (state.error ?? 'Erreur inconnue'),
          );
        } else if (state is AuthAuthenticated) {
          CustomSnackBar.showSuccess(
            context,
            message: 'Connexion réussie',
          );

          // Onsuccess Maybe we will implement it later (Mahiedine)
          if (onSuccess != null) {
            onSuccess!();
          }
        }
      },
      builder: (context, state) {
        final isLoading = state is AuthLoading;
        
        return AnimatedContainer(
          duration: const Duration(milliseconds: 200),
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(16),
            color: isLoading
                ? theme.colorScheme.primary.withOpacity(0.8)
                : theme.colorScheme.primary,
            boxShadow: [
              BoxShadow(
                color: theme.colorScheme.primary.withOpacity(isLoading ? 0.2 : 0.3),
                blurRadius: isLoading ? 8 : 12,
                offset: Offset(0, isLoading ? 2 : 4),
              ),
            ],
          ),
          child: Material(
            color: Colors.transparent,
            child: InkWell(
              borderRadius: BorderRadius.circular(16),
              onTap: isLoading
                  ? null
                  : () {
                      if (formKey.currentState!.validate()) {
                        FocusScope.of(context).unfocus();
                        context.read<AuthBloc>().add(
                              LoginRequested(
                                username: emailController.text.trim(),
                                accessCode: accessCodeController.text.trim().toUpperCase(),
                              ),
                            );
                      }
                    },
              child: Container(
                height: 56,
                alignment: Alignment.center,
                child: AnimatedSwitcher(
                  duration: const Duration(milliseconds: 200),
                  child: isLoading
                      ? const InlineLoadingWidget()
                      : Text(
                          buttonText,
                          style: TextStyle(
                            color: theme.colorScheme.onPrimary,
                            fontSize: 17,
                            fontWeight: FontWeight.w600,
                            letterSpacing: 0.2,
                          ),
                        ),
                ),
              ),
            ),
          ),
        );
      },
    );
  }
}