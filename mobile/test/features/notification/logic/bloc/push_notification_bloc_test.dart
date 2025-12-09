import 'package:bloc_test/bloc_test.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mobile/features/notification/data/dto/notification_dto.dart';
import 'package:mobile/features/notification/data/repositories/i_device_token_repository.dart';
import 'package:mobile/features/notification/data/repositories/i_notification_repository.dart';
import 'package:mobile/features/notification/logic/bloc/push_notification_bloc.dart';
import 'package:mocktail/mocktail.dart';

class MockDeviceTokenRepository extends Mock implements IDeviceTokenRepository {}
class MockNotificationRepository extends Mock implements INotificationRepository {}

void main() {
  late MockDeviceTokenRepository mockDeviceTokenRepository;
  late MockNotificationRepository mockNotificationRepository;

  setUp(() {
    mockDeviceTokenRepository = MockDeviceTokenRepository();
    mockNotificationRepository = MockNotificationRepository();
  });

  group('PushNotificationBloc', () {
    test('initial state is correct', () {
      final bloc = PushNotificationBloc(
        repository: mockDeviceTokenRepository,
        notificationRepository: mockNotificationRepository,
      );

      expect(bloc.state.status, PushNotificationStatus.initial);
      expect(bloc.state.notificationsStatus, NotificationsStatus.initial);
      expect(bloc.state.notifications, []);
      expect(bloc.state.unreadCount, 0);
      expect(bloc.state.hasReachedMax, false);
      expect(bloc.state.currentPage, 0);

      bloc.close();
    });

    // Skip Firebase-dependent tests as they require Firebase initialization
    // These tests should be run as integration tests with proper Firebase setup

    group('PushNotificationTokenRefreshed', () {
      blocTest<PushNotificationBloc, PushNotificationState>(
        'emits [registered] with new token when refresh succeeds',
        setUp: () {
          when(() => mockDeviceTokenRepository.registerToken(any()))
              .thenAnswer((_) async => {});
        },
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        act: (bloc) => bloc.add(const PushNotificationTokenRefreshed('new-fcm-token')),
        expect: () => [
          isA<PushNotificationState>()
              .having((s) => s.status, 'status', PushNotificationStatus.registered)
              .having((s) => s.fcmToken, 'fcmToken', 'new-fcm-token'),
        ],
        verify: (_) {
          verify(() => mockDeviceTokenRepository.registerToken('new-fcm-token')).called(1);
        },
      );

      blocTest<PushNotificationBloc, PushNotificationState>(
        'emits [error] when token refresh registration fails',
        setUp: () {
          when(() => mockDeviceTokenRepository.registerToken(any()))
              .thenThrow(Exception('Network error'));
        },
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        act: (bloc) => bloc.add(const PushNotificationTokenRefreshed('new-token')),
        expect: () => [
          isA<PushNotificationState>()
              .having((s) => s.status, 'status', PushNotificationStatus.error)
              .having((s) => s.errorMessage, 'errorMessage', 'Failed to re-register token: Exception: Network error'),
        ],
      );
    });

    group('NotificationsRequested', () {
      final mockNotifications = [
        NotificationDto(
          id: 1,
          title: 'Test 1',
          body: 'Body 1',
          type: 'INFO',
          read: false,
          createdAt: DateTime(2025, 1, 1),
        ),
        NotificationDto(
          id: 2,
          title: 'Test 2',
          body: 'Body 2',
          type: 'WARNING',
          read: true,
          createdAt: DateTime(2025, 1, 2),
          readAt: DateTime(2025, 1, 3),
        ),
      ];

      blocTest<PushNotificationBloc, PushNotificationState>(
        'emits [loading, loaded] with notifications when request succeeds',
        setUp: () {
          when(() => mockNotificationRepository.getNotifications(page: 0, size: 20))
              .thenAnswer((_) async => mockNotifications);
          when(() => mockNotificationRepository.getUnreadCount())
              .thenAnswer((_) async => 5);
        },
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        act: (bloc) => bloc.add(const NotificationsRequested()),
        expect: () => [
          isA<PushNotificationState>()
              .having((s) => s.notificationsStatus, 'status', NotificationsStatus.loading),
          isA<PushNotificationState>()
              .having((s) => s.notificationsStatus, 'status', NotificationsStatus.loaded)
              .having((s) => s.notifications.length, 'notifications length', 2)
              .having((s) => s.unreadCount, 'unreadCount', 5)
              .having((s) => s.hasReachedMax, 'hasReachedMax', true)
              .having((s) => s.currentPage, 'currentPage', 0),
        ],
        verify: (_) {
          verify(() => mockNotificationRepository.getNotifications(page: 0, size: 20)).called(1);
          verify(() => mockNotificationRepository.getUnreadCount()).called(1);
        },
      );

      blocTest<PushNotificationBloc, PushNotificationState>(
        'sets hasReachedMax to false when 20 notifications are returned',
        setUp: () {
          final fullPageNotifications = List.generate(
            20,
            (index) => NotificationDto(
              id: index,
              title: 'Test $index',
              body: 'Body $index',
              type: 'INFO',
              read: false,
              createdAt: DateTime(2025, 1, index + 1),
            ),
          );
          when(() => mockNotificationRepository.getNotifications(page: 0, size: 20))
              .thenAnswer((_) async => fullPageNotifications);
          when(() => mockNotificationRepository.getUnreadCount())
              .thenAnswer((_) async => 20);
        },
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        act: (bloc) => bloc.add(const NotificationsRequested()),
        expect: () => [
          isA<PushNotificationState>()
              .having((s) => s.notificationsStatus, 'status', NotificationsStatus.loading),
          isA<PushNotificationState>()
              .having((s) => s.notificationsStatus, 'status', NotificationsStatus.loaded)
              .having((s) => s.notifications.length, 'notifications length', 20)
              .having((s) => s.hasReachedMax, 'hasReachedMax', false)
              .having((s) => s.unreadCount, 'unreadCount', 20),
        ],
      );

      blocTest<PushNotificationBloc, PushNotificationState>(
        'emits [loading, failure] when request fails',
        setUp: () {
          when(() => mockNotificationRepository.getNotifications(page: 0, size: 20))
              .thenThrow(Exception('Network error'));
        },
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        act: (bloc) => bloc.add(const NotificationsRequested()),
        expect: () => [
          isA<PushNotificationState>()
              .having((s) => s.notificationsStatus, 'status', NotificationsStatus.loading),
          isA<PushNotificationState>()
              .having((s) => s.notificationsStatus, 'status', NotificationsStatus.failure)
              .having((s) => s.errorMessage, 'errorMessage', 'Exception: Network error'),
        ],
      );
    });

    group('NotificationsLoadMore', () {
      final initialNotifications = [
        NotificationDto(
          id: 1,
          title: 'Test 1',
          body: 'Body 1',
          type: 'INFO',
          read: false,
          createdAt: DateTime(2025, 1, 1),
        ),
      ];

      final additionalNotifications = [
        NotificationDto(
          id: 2,
          title: 'Test 2',
          body: 'Body 2',
          type: 'WARNING',
          read: false,
          createdAt: DateTime(2025, 1, 2),
        ),
      ];

      blocTest<PushNotificationBloc, PushNotificationState>(
        'emits [loadingMore, loaded] with appended notifications',
        setUp: () {
          when(() => mockNotificationRepository.getNotifications(page: 1, size: 20))
              .thenAnswer((_) async => additionalNotifications);
        },
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        seed: () => PushNotificationState(
          notificationsStatus: NotificationsStatus.loaded,
          notifications: initialNotifications,
          currentPage: 0,
          hasReachedMax: false,
        ),
        act: (bloc) => bloc.add(const NotificationsLoadMore()),
        expect: () => [
          isA<PushNotificationState>()
              .having((s) => s.notificationsStatus, 'status', NotificationsStatus.loadingMore)
              .having((s) => s.currentPage, 'currentPage', 0),
          isA<PushNotificationState>()
              .having((s) => s.notificationsStatus, 'status', NotificationsStatus.loaded)
              .having((s) => s.notifications.length, 'notifications length', 2)
              .having((s) => s.currentPage, 'currentPage', 1)
              .having((s) => s.hasReachedMax, 'hasReachedMax', true),
        ],
      );

      blocTest<PushNotificationBloc, PushNotificationState>(
        'does not emit when hasReachedMax is true',
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        seed: () => PushNotificationState(
          notificationsStatus: NotificationsStatus.loaded,
          notifications: initialNotifications,
          hasReachedMax: true,
        ),
        act: (bloc) => bloc.add(const NotificationsLoadMore()),
        expect: () => [],
      );

      blocTest<PushNotificationBloc, PushNotificationState>(
        'does not emit when already loading more',
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        seed: () => PushNotificationState(
          notificationsStatus: NotificationsStatus.loadingMore,
          notifications: initialNotifications,
        ),
        act: (bloc) => bloc.add(const NotificationsLoadMore()),
        expect: () => [],
      );

      blocTest<PushNotificationBloc, PushNotificationState>(
        'emits [loadingMore, failure] when load more fails',
        setUp: () {
          when(() => mockNotificationRepository.getNotifications(page: 1, size: 20))
              .thenThrow(Exception('Network error'));
        },
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        seed: () => PushNotificationState(
          notificationsStatus: NotificationsStatus.loaded,
          notifications: initialNotifications,
          currentPage: 0,
          hasReachedMax: false,
        ),
        act: (bloc) => bloc.add(const NotificationsLoadMore()),
        expect: () => [
          isA<PushNotificationState>()
              .having((s) => s.notificationsStatus, 'status', NotificationsStatus.loadingMore),
          isA<PushNotificationState>()
              .having((s) => s.notificationsStatus, 'status', NotificationsStatus.failure)
              .having((s) => s.errorMessage, 'errorMessage', 'Exception: Network error'),
        ],
      );
    });

    group('NotificationMarkAsRead', () {
      final unreadNotification = NotificationDto(
        id: 1,
        title: 'Test',
        body: 'Body',
        type: 'INFO',
        read: false,
        createdAt: DateTime(2025, 1, 1),
      );

      final readNotification = NotificationDto(
        id: 1,
        title: 'Test',
        body: 'Body',
        type: 'INFO',
        read: true,
        createdAt: DateTime(2025, 1, 1),
        readAt: DateTime(2025, 1, 2),
      );

      blocTest<PushNotificationBloc, PushNotificationState>(
        'updates notification to read and decrements unread count',
        setUp: () {
          when(() => mockNotificationRepository.markAsRead(1))
              .thenAnswer((_) async => readNotification);
        },
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        seed: () => PushNotificationState(
          notificationsStatus: NotificationsStatus.loaded,
          notifications: [unreadNotification],
          unreadCount: 5,
        ),
        act: (bloc) => bloc.add(const NotificationMarkAsRead(1)),
        expect: () => [
          isA<PushNotificationState>()
              .having((s) => s.notifications.first.read, 'notification.read', true)
              .having((s) => s.unreadCount, 'unreadCount', 4),
        ],
        verify: (_) {
          verify(() => mockNotificationRepository.markAsRead(1)).called(1);
        },
      );

      blocTest<PushNotificationBloc, PushNotificationState>(
        'does not decrement unread count below zero',
        setUp: () {
          when(() => mockNotificationRepository.markAsRead(1))
              .thenAnswer((_) async => readNotification);
        },
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        seed: () => PushNotificationState(
          notificationsStatus: NotificationsStatus.loaded,
          notifications: [unreadNotification],
          unreadCount: 0,
        ),
        act: (bloc) => bloc.add(const NotificationMarkAsRead(1)),
        expect: () => [
          isA<PushNotificationState>()
              .having((s) => s.notifications.first.read, 'notification.read', true)
              .having((s) => s.unreadCount, 'unreadCount', 0),
        ],
      );

      blocTest<PushNotificationBloc, PushNotificationState>(
        'emits error message when mark as read fails',
        setUp: () {
          when(() => mockNotificationRepository.markAsRead(1))
              .thenThrow(Exception('Failed to mark as read'));
        },
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        seed: () => PushNotificationState(
          notificationsStatus: NotificationsStatus.loaded,
          notifications: [unreadNotification],
          unreadCount: 5,
        ),
        act: (bloc) => bloc.add(const NotificationMarkAsRead(1)),
        expect: () => [
          isA<PushNotificationState>()
              .having((s) => s.errorMessage, 'errorMessage', 'Exception: Failed to mark as read'),
        ],
      );
    });

    group('NotificationsMarkAllAsRead', () {
      final notifications = [
        NotificationDto(
          id: 1,
          title: 'Test 1',
          body: 'Body 1',
          type: 'INFO',
          read: false,
          createdAt: DateTime(2025, 1, 1),
        ),
        NotificationDto(
          id: 2,
          title: 'Test 2',
          body: 'Body 2',
          type: 'WARNING',
          read: false,
          createdAt: DateTime(2025, 1, 2),
        ),
      ];

      blocTest<PushNotificationBloc, PushNotificationState>(
        'marks all notifications as read and sets unread count to 0',
        setUp: () {
          when(() => mockNotificationRepository.markAllAsRead())
              .thenAnswer((_) async => {});
        },
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        seed: () => PushNotificationState(
          notificationsStatus: NotificationsStatus.loaded,
          notifications: notifications,
          unreadCount: 2,
        ),
        act: (bloc) => bloc.add(const NotificationsMarkAllAsRead()),
        expect: () => [
          isA<PushNotificationState>()
              .having((s) => s.notificationsStatus, 'status', NotificationsStatus.loaded)
              .having((s) => s.notifications.every((n) => n.read), 'all read', true)
              .having((s) => s.unreadCount, 'unreadCount', 0),
        ],
        verify: (_) {
          verify(() => mockNotificationRepository.markAllAsRead()).called(1);
        },
      );

      blocTest<PushNotificationBloc, PushNotificationState>(
        'emits error message when mark all as read fails',
        setUp: () {
          when(() => mockNotificationRepository.markAllAsRead())
              .thenThrow(Exception('Network error'));
        },
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        seed: () => PushNotificationState(
          notificationsStatus: NotificationsStatus.loaded,
          notifications: notifications,
          unreadCount: 2,
        ),
        act: (bloc) => bloc.add(const NotificationsMarkAllAsRead()),
        expect: () => [
          isA<PushNotificationState>()
              .having((s) => s.errorMessage, 'errorMessage', 'Exception: Network error'),
        ],
      );
    });

    group('NotificationsUnreadCountRequested', () {
      blocTest<PushNotificationBloc, PushNotificationState>(
        'updates unread count when request succeeds',
        setUp: () {
          when(() => mockNotificationRepository.getUnreadCount())
              .thenAnswer((_) async => 10);
        },
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        act: (bloc) => bloc.add(const NotificationsUnreadCountRequested()),
        expect: () => [
          isA<PushNotificationState>()
              .having((s) => s.unreadCount, 'unreadCount', 10),
        ],
      );

      blocTest<PushNotificationBloc, PushNotificationState>(
        'silently fails when request fails (no state change)',
        setUp: () {
          when(() => mockNotificationRepository.getUnreadCount())
              .thenThrow(Exception('Network error'));
        },
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        act: (bloc) => bloc.add(const NotificationsUnreadCountRequested()),
        expect: () => [],
      );
    });

    group('PushNotificationReceived', () {
      blocTest<PushNotificationBloc, PushNotificationState>(
        'updates last notification and triggers notifications refresh',
        setUp: () {
          when(() => mockNotificationRepository.getNotifications(page: 0, size: 20))
              .thenAnswer((_) async => []);
          when(() => mockNotificationRepository.getUnreadCount())
              .thenAnswer((_) async => 1);
        },
        build: () => PushNotificationBloc(
          repository: mockDeviceTokenRepository,
          notificationRepository: mockNotificationRepository,
        ),
        act: (bloc) => bloc.add(const PushNotificationReceived(
          title: 'New Message',
          body: 'You have a new notification',
          data: {'type': 'message'},
        )),
        expect: () => [
          isA<PushNotificationState>()
              .having((s) => s.lastNotification?.title, 'lastNotification.title', 'New Message'),
          isA<PushNotificationState>()
              .having((s) => s.notificationsStatus, 'status', NotificationsStatus.loading)
              .having((s) => s.lastNotification?.title, 'lastNotification.title', 'New Message'),
          isA<PushNotificationState>()
              .having((s) => s.notificationsStatus, 'status', NotificationsStatus.loaded)
              .having((s) => s.unreadCount, 'unreadCount', 1)
              .having((s) => s.hasReachedMax, 'hasReachedMax', true)
              .having((s) => s.lastNotification?.title, 'lastNotification.title', 'New Message'),
        ],
      );
    });
  });
}
