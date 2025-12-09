import 'package:equatable/equatable.dart';
import 'package:mobile/features/profile/data/models/user_profile.dart';

abstract class ProfileState extends Equatable {}

class ProfileInitial extends ProfileState {
  @override
  List<Object?> get props => [];
}

class ProfileLoading extends ProfileState {
  @override
  List<Object?> get props => [];
}

class ProfileLoaded extends ProfileState {
  final UserProfile profile;

  ProfileLoaded({required this.profile});

  @override
  List<Object?> get props => [profile];
}

class ProfileUpdating extends ProfileState {
  final UserProfile profile;

  ProfileUpdating({required this.profile});

  @override
  List<Object?> get props => [profile];
}

class ProfileUpdateSuccess extends ProfileState {
  final UserProfile profile;
  final String message;

  ProfileUpdateSuccess({required this.profile, required this.message});

  @override
  List<Object?> get props => [profile, message];
}

class ProfileError extends ProfileState {
  final String message;
  final UserProfile? previousProfile;

  ProfileError({required this.message, this.previousProfile});

  @override
  List<Object?> get props => [message, previousProfile];
}
