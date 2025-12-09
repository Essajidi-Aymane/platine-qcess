import 'package:mobile/core/network/base_api_repository.dart';
import 'package:mobile/features/notification/data/dto/notification_dto.dart';
import 'package:mobile/features/notification/data/repositories/i_notification_repository.dart';

class NotificationRepository extends BaseApiRepository implements INotificationRepository {
  NotificationRepository(super.dio);

  static const String _basePath = '/api/notifications';

  @override
  Future<List<NotificationDto>> getNotifications({int page = 0, int size = 20}) {
    return get<List<NotificationDto>>(
      _basePath,
      queryParameters: {
        'page': page,
        'size': size,
      },
      fromJson: (data) {
        final list = data as List<dynamic>;
        return list
            .map((e) => NotificationDto.fromJson(e as Map<String, dynamic>))
            .toList();
      },
    );
  }

  @override
  Future<List<NotificationDto>> getUnreadNotifications({int page = 0, int size = 20}) {
    return get<List<NotificationDto>>(
      '$_basePath/unread',
      queryParameters: {
        'page': page,
        'size': size,
      },
      fromJson: (data) {
        final list = data as List<dynamic>;
        return list
            .map((e) => NotificationDto.fromJson(e as Map<String, dynamic>))
            .toList();
      },
    );
  }

  @override
  Future<int> getUnreadCount() {
    return get<int>(
      '$_basePath/unread/count',
      fromJson: (data) => data as int,
    );
  }

  @override
  Future<NotificationDto> markAsRead(int notificationId) {
    return put<NotificationDto>(
      '$_basePath/$notificationId/read',
      fromJson: (data) => NotificationDto.fromJson(data as Map<String, dynamic>),
    );
  }

  @override
  Future<void> markAllAsRead() async {
    await put<void>(
      '$_basePath/read-all',
      fromJson: (_) {},
    );
  }
}
