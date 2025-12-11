class AccessLogDTO {
  final int? id;
  final int userId;
  final int? zoneId;
  final String? zoneName;
  final int organizationId;
  final DateTime timestamp;
  final bool accessGranted;
  final String? reason;

  AccessLogDTO({
    this.id,
    required this.userId,
    this.zoneId,
    this.zoneName,
    required this.organizationId,
    required this.timestamp,
    required this.accessGranted,
    this.reason,
  });

  factory AccessLogDTO.fromJson(Map<String, dynamic> json) {
    final timestampStr = json['timestamp'] as String;
    final timestamp = DateTime.parse(timestampStr).toLocal();
    
    return AccessLogDTO(
      id: json['id'] as int?,
      userId: json['userId'] as int,
      zoneId: json['zoneId'] as int?,
      zoneName: json['zoneName'] as String?,
      organizationId: json['organizationId'] as int,
      timestamp: timestamp,
      accessGranted: json['accessGranted'] as bool,
      reason: json['reason'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'zoneId': zoneId,
      'zoneName': zoneName,
      'organizationId': organizationId,
      'timestamp': timestamp.toIso8601String(),
      'accessGranted': accessGranted,
      'reason': reason,
    };
  }
}
