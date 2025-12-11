class AccessRequestDTO {
  final int zoneId;

  AccessRequestDTO({required this.zoneId});

  Map<String, dynamic> toJson() {
    return {'zoneId': zoneId};
  }

  factory AccessRequestDTO.fromJson(Map<String, dynamic> json) {
    return AccessRequestDTO(zoneId: json['zoneId'] as int);
  }
}
