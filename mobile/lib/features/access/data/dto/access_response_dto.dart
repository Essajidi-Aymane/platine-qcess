class AccessResponseDTO {
  final bool granted;
  final String reason;
  final String zoneName;

  AccessResponseDTO({
    required this.granted,
    required this.reason,
    required this.zoneName,
  });

  factory AccessResponseDTO.fromJson(Map<String, dynamic> json) {
    return AccessResponseDTO(
      granted: json['granted'] as bool,
      reason: json['reason'] as String,
      zoneName: json['zoneName'] as String,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'granted': granted,
      'reason': reason,
      'zoneName': zoneName,
    };
  }
}