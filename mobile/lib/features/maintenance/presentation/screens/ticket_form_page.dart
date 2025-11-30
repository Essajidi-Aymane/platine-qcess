import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mobile/core/theme/app_colors.dart';
import 'package:mobile/features/maintenance/data/dto/create_ticket_request.dart';
import 'package:mobile/features/maintenance/data/models/priority.dart';
import 'package:mobile/features/maintenance/logic/bloc/tickets_bloc.dart';
import 'package:mobile/features/maintenance/logic/bloc/tickets_event.dart';


class TicketFormPage extends StatefulWidget {
  const TicketFormPage({super.key});

  @override
  State<TicketFormPage> createState() => _TicketFormPageState();
}

class _TicketFormPageState extends State<TicketFormPage> {
  final _formKey = GlobalKey<FormState>();
  final _titleCtrl = TextEditingController();
  final _descCtrl = TextEditingController();
  Priority _priority = Priority.normal;

  @override
  void dispose() {
    _titleCtrl.dispose();
    _descCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.primary,
      body: SafeArea(
        child: Column(
          children: [
            _buildHeader(),
            
            Expanded(
              child: Container(
                decoration: BoxDecoration(
                  color: AppColors.background,
                  borderRadius: const BorderRadius.only(
                    topLeft: Radius.circular(32),
                    topRight: Radius.circular(32),
                  ),
                ),
                child: SingleChildScrollView(
                  padding: const EdgeInsets.all(24),
                  child: Form(
                    key: _formKey,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const SizedBox(height: 8),
                        _buildSection(
                          icon: Icons.title,
                          title: 'Objet du ticket',
                          child: TextFormField(
                            controller: _titleCtrl,
                            decoration: const InputDecoration(
                              hintText: 'Ex: Problème d\'accès',
                            ),
                            validator: (v) =>
                                (v == null || v.trim().isEmpty) ? 'Titre requis' : null,
                          ),
                        ),
                        const SizedBox(height: 24),
                        _buildSection(
                          icon: Icons.description_outlined,
                          title: 'Description',
                          child: TextFormField(
                            controller: _descCtrl,
                            decoration: const InputDecoration(
                              hintText: 'Décrivez votre problème en détail...',
                            ),
                            minLines: 4,
                            maxLines: 6,
                            validator: (v) => (v == null || v.trim().isEmpty)
                                ? 'Description requise'
                                : null,
                          ),
                        ),
                        const SizedBox(height: 24),
                        _buildSection(
                          icon: Icons.flag_outlined,
                          title: 'Priorité',
                          child: _buildPrioritySelector(),
                        ),
                        const SizedBox(height: 32),
                        _buildSubmitButton(context),
                      ],
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildHeader() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(24, 16, 24, 32),
      child: Row(
        children: [
          IconButton(
            icon: const Icon(Icons.arrow_back, color: Colors.white),
            onPressed: () => context.pop(),
          ),
          const SizedBox(width: 16),
          const Expanded(
            child: Text(
              'Nouveau Ticket',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
                color: Colors.white,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSection({
    required IconData icon,
    required String title,
    required Widget child,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Icon(icon, size: 20, color: AppColors.primary),
            const SizedBox(width: 8),
            Text(
              title,
              style: const TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w600,
                color: AppColors.text,
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        child,
      ],
    );
  }

  Widget _buildPrioritySelector() {
    return Row(
      children: [
        _buildPriorityChip(
          label: 'Faible',
          priority: Priority.low,
          color: AppColors.success,
        ),
        const SizedBox(width: 12),
        _buildPriorityChip(
          label: 'Normale',
          priority: Priority.normal,
          color: AppColors.warning,
        ),
        const SizedBox(width: 12),
        _buildPriorityChip(
          label: 'Urgente',
          priority: Priority.high,
          color: AppColors.error,
        ),
      ],
    );
  }

  Widget _buildPriorityChip({
    required String label,
    required Priority priority,
    required Color color,
  }) {
    final isSelected = _priority == priority;
    
    return Expanded(
      child: InkWell(
        onTap: () => setState(() => _priority = priority),
        borderRadius: BorderRadius.circular(12),
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 16),
          decoration: BoxDecoration(
            color: isSelected ? color.withOpacity(0.1) : Colors.white,
            borderRadius: BorderRadius.circular(12),
            border: Border.all(
              color: isSelected ? color : AppColors.borderLight,
              width: isSelected ? 2 : 1,
            ),
          ),
          child: Column(
            children: [
              Icon(
                isSelected ? Icons.check_circle : Icons.circle_outlined,
                color: isSelected ? color : AppColors.textSecondary,
                size: 24,
              ),
              const SizedBox(height: 4),
              Text(
                label,
                textAlign: TextAlign.center,
                style: TextStyle(
                  fontSize: 12,
                  fontWeight: isSelected ? FontWeight.w600 : FontWeight.w500,
                  color: isSelected ? color : AppColors.textSecondary,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildSubmitButton(BuildContext context) {
    return Container(
      width: double.infinity,
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(16),
        gradient: LinearGradient(
          colors: [AppColors.primary, AppColors.primaryDark],
        ),
        boxShadow: [
          BoxShadow(
            color: AppColors.primary.withOpacity(0.3),
            blurRadius: 12,
            offset: const Offset(0, 6),
          ),
        ],
      ),
      child: ElevatedButton.icon(
        onPressed: () => _handleSubmit(context),
        style: ElevatedButton.styleFrom(
          backgroundColor: Colors.transparent,
          shadowColor: Colors.transparent,
          padding: const EdgeInsets.symmetric(vertical: 16),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
        ),
        icon: const Icon(Icons.send, color: Colors.white),
        label: const Text(
          'Envoyer le ticket',
          style: TextStyle(
            fontSize: 17,
            fontWeight: FontWeight.w600,
            color: Colors.white,
          ),
        ),
      ),
    );
  }

  void _handleSubmit(BuildContext context) {
    if (!_formKey.currentState!.validate()) return;

    final req = CreateTicketRequest(
      title: _titleCtrl.text.trim(),
      description: _descCtrl.text.trim(),
      priority: _priority,
    );

    context.read<TicketsBloc>().add(TicketCreated(req));
    context.pop();
  }
}