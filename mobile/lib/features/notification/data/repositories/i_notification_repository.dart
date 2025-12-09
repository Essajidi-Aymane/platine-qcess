import 'package:mobile/features/notification/data/dto/notification_dto.dart';

abstract class INotificationRepository {
  Future<List<NotificationDto>> getNotifications({int page = 0, int size = 20});
  
  Future<List<NotificationDto>> getUnreadNotifications({int page = 0, int size = 20});
  
  Future<int> getUnreadCount();
  
  Future<NotificationDto> markAsRead(int notificationId);
  
  Future<void> markAllAsRead();
}
