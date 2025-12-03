import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mobile/features/settings/logic/bloc/settings_event.dart';
import 'package:mobile/features/settings/logic/bloc/settings_state.dart';

class SettingsBloc extends Bloc<SettingsEvent, SettingsState> {
  SettingsBloc() : super(const SettingsState()) {
  }
}
