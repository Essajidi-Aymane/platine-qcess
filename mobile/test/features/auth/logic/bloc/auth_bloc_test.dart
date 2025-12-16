import 'package:bloc_test/bloc_test.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';
import 'package:mobile/features/auth/data/models/user_info.dart';
import 'package:mobile/features/auth/data/repositories/i_auth_repository.dart';
import 'package:mobile/features/auth/logic/bloc/auth_bloc.dart';
import 'package:mobile/features/auth/logic/bloc/auth_event.dart';
import 'package:mobile/features/auth/logic/bloc/auth_state.dart';

import 'auth_bloc_test.mocks.dart';

@GenerateMocks([IAuthRepository])
void main() {
  late MockIAuthRepository mockAuthRepository;

  const testUserInfo = UserInfo(
    id: 1,
    email: 'test@test.com',
    fullName: 'Test User',
    role: 'USER',
    organizationId: 1,
  );

  setUp(() {
    mockAuthRepository = MockIAuthRepository();
  });

  group('AuthBloc', () {
    const testToken = 'test_token_123';
    const testUsername = 'test@ex.com';
    const testAccessCode = '12345';

    test('initial state is AuthInitial', () {
      final authBloc = AuthBloc(authRepository: mockAuthRepository);
      expect(authBloc.state, isA<AuthInitial>());
      authBloc.close();
    });

    group('LoginRequested', () {
      blocTest<AuthBloc, AuthState>(
        'emits [AuthLoading, AuthAuthenticated] when login succeeds',
        build: () {
          when(
            mockAuthRepository.login(testUsername, testAccessCode),
          ).thenAnswer((_) async => testToken);
          when(
            mockAuthRepository.getUserInfo(),
          ).thenAnswer((_) async => testUserInfo);
          return AuthBloc(authRepository: mockAuthRepository);
        },
        act: (bloc) => bloc.add(
          LoginRequested(username: testUsername, accessCode: testAccessCode),
        ),
        expect: () => [
          isA<AuthLoading>(),
          isA<AuthAuthenticated>()
              .having((state) => state.token, 'token', testToken)
              .having((state) => state.userInfo, 'userInfo', testUserInfo),
        ],
        verify: (_) {
          verify(
            mockAuthRepository.login(testUsername, testAccessCode),
          ).called(1);
          verify(mockAuthRepository.getUserInfo()).called(1);
        },
      );

      blocTest<AuthBloc, AuthState>(
        'emits [AuthLoading, AuthUnauthenticated] when login fails',
        build: () {
          when(
            mockAuthRepository.login(testUsername, testAccessCode),
          ).thenThrow(Exception('Invalid credentials'));
          return AuthBloc(authRepository: mockAuthRepository);
        },
        act: (bloc) => bloc.add(
          LoginRequested(username: testUsername, accessCode: testAccessCode),
        ),
        expect: () => [
          isA<AuthLoading>(),
          isA<AuthUnauthenticated>().having(
            (state) => state.error,
            'error',
            isNotNull,
          ),
        ],
      );
    });

    group('AppStarted', () {
      blocTest<AuthBloc, AuthState>(
        'emits [AuthLoading, AuthAuthenticated] when valid token exists',
        build: () {
          when(mockAuthRepository.checkToken()).thenAnswer((_) async => true);
          when(
            mockAuthRepository.getToken(),
          ).thenAnswer((_) async => testToken);
          when(
            mockAuthRepository.getUserInfo(),
          ).thenAnswer((_) async => testUserInfo);
          return AuthBloc(authRepository: mockAuthRepository);
        },
        act: (bloc) => bloc.add(AppStarted()),
        expect: () => [
          isA<AuthLoading>(),
          isA<AuthAuthenticated>()
              .having((state) => state.token, 'token', testToken)
              .having((state) => state.userInfo, 'userInfo', testUserInfo),
        ],
        verify: (_) {
          verify(mockAuthRepository.checkToken()).called(1);
          verify(mockAuthRepository.getToken()).called(1);
          verify(mockAuthRepository.getUserInfo()).called(1);
        },
      );

      blocTest<AuthBloc, AuthState>(
        'emits [AuthLoading, AuthUnauthenticated] when no token exists',
        build: () {
          when(mockAuthRepository.checkToken()).thenAnswer((_) async => false);
          return AuthBloc(authRepository: mockAuthRepository);
        },
        act: (bloc) => bloc.add(AppStarted()),
        expect: () => [isA<AuthLoading>(), isA<AuthUnauthenticated>()],
        verify: (_) {
          verify(mockAuthRepository.checkToken()).called(1);
          verifyNever(mockAuthRepository.getToken());
        },
      );

      blocTest<AuthBloc, AuthState>(
        'emits [AuthLoading, AuthUnauthenticated] when token is invalid',
        build: () {
          when(mockAuthRepository.checkToken()).thenAnswer((_) async => true);
          when(mockAuthRepository.getToken()).thenAnswer((_) async => null);
          return AuthBloc(authRepository: mockAuthRepository);
        },
        act: (bloc) => bloc.add(AppStarted()),
        expect: () => [isA<AuthLoading>(), isA<AuthUnauthenticated>()],
      );
    });

    group('LogoutRequested', () {
      blocTest<AuthBloc, AuthState>(
        'emits [AuthLoading, AuthUnauthenticated] when logout succeeds',
        build: () {
          when(
            mockAuthRepository.logout(testToken),
          ).thenAnswer((_) async => {});
          return AuthBloc(authRepository: mockAuthRepository);
        },
        act: (bloc) => bloc.add(LogoutRequested(token: testToken)),
        expect: () => [isA<AuthLoading>(), isA<AuthUnauthenticated>()],
        verify: (_) {
          verify(mockAuthRepository.logout(testToken)).called(1);
        },
      );

      blocTest<AuthBloc, AuthState>(
        'emits [AuthLoading, AuthUnauthenticated] even when logout fails',
        build: () {
          when(
            mockAuthRepository.logout(testToken),
          ).thenThrow(Exception('Logout failed'));
          when(mockAuthRepository.clearLocalData()).thenAnswer((_) async => {});
          return AuthBloc(authRepository: mockAuthRepository);
        },
        act: (bloc) => bloc.add(LogoutRequested(token: testToken)),
        expect: () => [isA<AuthLoading>(), isA<AuthUnauthenticated>()],
        verify: (_) {
          verify(mockAuthRepository.logout(testToken)).called(1);
          verify(mockAuthRepository.clearLocalData()).called(1);
        },
      );
    });
  });
}
