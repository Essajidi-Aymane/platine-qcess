import 'package:equatable/equatable.dart';
import 'package:mobile/features/auth/data/models/user_info.dart';

abstract class AuthState extends Equatable {
  final UserInfo? userInfo;

  const AuthState({this.userInfo});

  @override
  List<Object?> get props => [userInfo];
}

class AuthInitial extends AuthState {
  const AuthInitial() : super(userInfo: null);
}

class AuthLoading extends AuthState {
  const AuthLoading() : super(userInfo: null);
}

class AuthAuthenticated extends AuthState {
  final String token;

  const AuthAuthenticated({
    required this.token,
    required UserInfo userInfo,
  }) : super(userInfo: userInfo);

  @override
  List<Object?> get props => [token, userInfo];
}

class AuthUnauthenticated extends AuthState {
  final String? error;

  const AuthUnauthenticated({this.error}) : super(userInfo: null);

  @override
  List<Object?> get props => [error];
}