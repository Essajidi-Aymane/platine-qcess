class AccessLogDTO {
  final int? id;
  final String? userName;
  final String? zoneName;
  final DateTime timestamp;
  final bool accessGranted;
  final String? reason;

  AccessLogDTO({
    this.id,
    required this.userName,
    this.zoneName,
    required this.timestamp,
    required this.accessGranted,
    this.reason,
  });

  factory AccessLogDTO.fromJson(Map<String, dynamic> json) {
    final timestampStr = json['timestamp'] as String;
    final timestamp = DateTime.parse(timestampStr).toLocal();
    
    return AccessLogDTO(
      id: json['id'] as int?,
      userName: json['userName'] as String?,
      zoneName: json['zoneName'] as String?,
      timestamp: timestamp,
      accessGranted: json['accessGranted'] as bool,
      reason: json['reason'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userName': userName,
      'zoneName': zoneName,
      'timestamp': timestamp.toIso8601String(),
      'accessGranted': accessGranted,
      'reason': reason,
    };
  }
}
