import 'package:json_annotation/json_annotation.dart';

enum Priority {
  @JsonValue('LOW')
  low,
  @JsonValue('NORMAL')
  normal,
  @JsonValue('HIGH')
  high;

  String getDisplayColor() {
    return switch (this) {
      Priority.low => 'green',
      Priority.normal => 'blue',
      Priority.high => 'red',
    };
  }

  String getDisplayName() {
    return switch (this) {
      Priority.low => 'Basse',
      Priority.normal => 'Normale',
      Priority.high => 'Haute',
    };
  }
}