import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:image_picker/image_picker.dart';
import 'package:mobile/features/profile/data/dto/update_profile_request.dart';
import 'package:mobile/features/profile/data/models/user_profile.dart';
import 'package:mobile/features/profile/logic/bloc/profile_bloc.dart';
import 'package:mobile/features/profile/logic/bloc/profile_event.dart';
import 'package:mobile/features/profile/logic/bloc/profile_state.dart';
import 'package:mobile/features/profile/presentation/widgets/organization_card.dart';
import 'package:mobile/features/profile/presentation/widgets/profile_app_bar.dart';
import 'package:mobile/features/profile/presentation/widgets/profile_header.dart';
import 'package:mobile/features/profile/presentation/widgets/profile_info_card.dart';
import 'package:mobile/features/profile/presentation/widgets/profile_info_field.dart';
import 'package:mobile/features/profile/presentation/widgets/profile_info_row.dart';

class ProfilePage extends StatefulWidget {
  const ProfilePage({super.key});

  @override
  State<ProfilePage> createState() => _ProfilePageState();
}

class _ProfilePageState extends State<ProfilePage> {
  bool _isEditing = false;
  final _formKey = GlobalKey<FormState>();

  late TextEditingController _firstNameController;
  late TextEditingController _lastNameController;
  late TextEditingController _emailController;

  @override
  void initState() {
    super.initState();
    _firstNameController = TextEditingController();
    _lastNameController = TextEditingController();
    _emailController = TextEditingController();
    context.read<ProfileBloc>().add(ProfileLoadRequested());
  }

  @override
  void dispose() {
    _firstNameController.dispose();
    _lastNameController.dispose();
    _emailController.dispose();
    super.dispose();
  }

  void _initializeControllers(UserProfile profile) {
    _firstNameController.text = profile.firstName ?? '';
    _lastNameController.text = profile.lastName ?? '';
    _emailController.text = profile.email;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: BlocConsumer<ProfileBloc, ProfileState>(
        listener: _onStateChanged,
        builder: (context, state) {
          if (state is ProfileLoading) {
            return const Center(child: CircularProgressIndicator());
          }
          if (state is ProfileError && state.previousProfile == null) {
            return _buildErrorState(state.message);
          }
          final profile = _extractProfile(state);
          if (profile == null) {
            return const Center(child: CircularProgressIndicator());
          }
          return _buildProfileContent(profile, state is ProfileUpdating);
        },
      ),
    );
  }

  void _onStateChanged(BuildContext context, ProfileState state) {
    if (state is ProfileUpdateSuccess) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(state.message),
          backgroundColor: Colors.green,
          behavior: SnackBarBehavior.floating,
        ),
      );
      setState(() => _isEditing = false);
    } else if (state is ProfileError) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(state.message),
          backgroundColor: Colors.red,
          behavior: SnackBarBehavior.floating,
        ),
      );
    } else if (state is ProfileLoaded && !_isEditing) {
      _initializeControllers(state.profile);
    }
  }

  UserProfile? _extractProfile(ProfileState state) {
    if (state is ProfileLoaded) return state.profile;
    if (state is ProfileUpdating) return state.profile;
    if (state is ProfileUpdateSuccess) return state.profile;
    if (state is ProfileError) return state.previousProfile;
    return null;
  }

  Widget _buildErrorState(String message) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.error_outline, size: 64, color: Colors.red.withValues(alpha: 0.7)),
          const SizedBox(height: 16),
          Text(message, style: Theme.of(context).textTheme.titleMedium, textAlign: TextAlign.center),
          const SizedBox(height: 24),
          FilledButton.icon(
            onPressed: () => context.read<ProfileBloc>().add(ProfileLoadRequested()),
            icon: const Icon(Icons.refresh),
            label: const Text('Réessayer'),
          ),
        ],
      ),
    );
  }

  Widget _buildProfileContent(UserProfile profile, bool isUpdating) {
    return CustomScrollView(
      slivers: [
        ProfileAppBar(
          imageUrl: profile.profilePictureUrl,
          initials: profile.initials,
          isEditing: _isEditing,
          onBackPressed: () {
            if (GoRouter.of(context).canPop()) {
              context.pop();
            } else {
              context.go('/home');
            }
          },
          onEditToggle: () => setState(() {
            _isEditing = !_isEditing;
            if (!_isEditing) _initializeControllers(profile);
          }),
          onChangePhoto: _onChangePhoto,
        ),
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Form(
              key: _formKey,
              child: Column(
                children: [
                  ProfileHeader(displayName: profile.displayName, email: profile.email),
                  const SizedBox(height: 32),
                  _buildInfoSection(isUpdating),
                  const SizedBox(height: 24),
                  if (profile.organizationName != null)
                    OrganizationCard(organizationName: profile.organizationName!),
                  const SizedBox(height: 24),
                  _buildAccountInfoCard(profile),
                  const SizedBox(height: 100),
                ],
              ),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildInfoSection(bool isUpdating) {
    return ProfileInfoCard(
      title: 'Informations personnelles',
      trailing: _buildSaveButton(isUpdating),
      children: [
        ProfileInfoField(icon: Icons.person_outline, label: 'Prénom', controller: _firstNameController, enabled: _isEditing),
        const SizedBox(height: 16),
        ProfileInfoField(icon: Icons.person_outline, label: 'Nom', controller: _lastNameController, enabled: _isEditing),
        const SizedBox(height: 16),
        ProfileInfoField(icon: Icons.email_outlined, label: 'Email', controller: _emailController, enabled: _isEditing, keyboardType: TextInputType.emailAddress),
      ],
    );
  }

  Widget? _buildSaveButton(bool isUpdating) {
    if (isUpdating) return const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2));
    if (_isEditing) {
      return FilledButton(
        onPressed: _onSaveProfile,
        style: FilledButton.styleFrom(padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8)),
        child: const Text('Enregistrer'),
      );
    }
    return null;
  }

  Widget _buildAccountInfoCard(UserProfile profile) {
    return ProfileInfoCard(
      title: 'Informations du compte',
      children: [
        ProfileInfoRow(icon: Icons.badge_outlined, label: 'Identifiant', value: '#${profile.id}'),
        if (profile.createdAt != null) ...[
          const SizedBox(height: 12),
          ProfileInfoRow(icon: Icons.calendar_today_outlined, label: 'Membre depuis', value: _formatDate(profile.createdAt!)),
        ],
      ],
    );
  }

  String _formatDate(DateTime date) {
    const months = ['janvier', 'février', 'mars', 'avril', 'mai', 'juin', 'juillet', 'août', 'septembre', 'octobre', 'novembre', 'décembre'];
    return '${date.day} ${months[date.month - 1]} ${date.year}';
  }

  void _onChangePhoto() {
    _pickAndUploadProfilePhoto();
  }

  Future<void> _pickAndUploadProfilePhoto() async {
    final picker = ImagePicker();
    try {
      final XFile? file = await picker.pickImage(
        source: ImageSource.gallery,
        imageQuality: 85,
        maxWidth: 1920,
        maxHeight: 1920,
      );

      if (file == null) return;

      context.read<ProfileBloc>().add(
            ProfilePictureUpdateRequested(imagePath: file.path),
          );

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Téléchargement de la photo...'),
          behavior: SnackBarBehavior.floating,
        ),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Échec de la sélection de la photo: $e'),
          behavior: SnackBarBehavior.floating,
        ),
      );
    }
  }

  void _onSaveProfile() {
    if (_formKey.currentState?.validate() ?? false) {
      context.read<ProfileBloc>().add(ProfileUpdateRequested(
        request: UpdateProfileRequest(
          firstName: _firstNameController.text.trim().isNotEmpty ? _firstNameController.text.trim() : null,
          lastName: _lastNameController.text.trim().isNotEmpty ? _lastNameController.text.trim() : null,
          email: _emailController.text.trim().isNotEmpty ? _emailController.text.trim() : null,
        ),
      ));
    }
  }
}