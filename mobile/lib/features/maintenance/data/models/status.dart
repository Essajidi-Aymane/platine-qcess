import 'package:json_annotation/json_annotation.dart';

enum Status {
  @JsonValue('OPEN')
  open,
  @JsonValue('IN_PROGRESS')
  inProgress,
  @JsonValue('RESOLVED')
  resolved,
  @JsonValue('REJECTED')
  rejected,
  @JsonValue('CANCELLED')
  cancelled;

  String getDisplayName() {
    return switch (this) {
      Status.open => 'Ouvert',
      Status.inProgress => 'En cours',
      Status.resolved => 'Résolu',
      Status.rejected => 'Rejeté',
      Status.cancelled => 'Annulé',
    };
  }

  bool isTerminal() {
    return this == Status.resolved ||
        this == Status.rejected ||
        this == Status.cancelled;
  }

  bool canTransitionTo(Status newStatus) {
    if (isTerminal()) {
      return false;
    }

    if (newStatus == Status.cancelled) {
      return this == Status.open || this == Status.inProgress;
    }

    return true;
  }

  bool isCancellable() {
    return this == Status.open || this == Status.inProgress;
  }
}