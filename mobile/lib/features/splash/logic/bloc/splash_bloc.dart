import 'dart:async';

import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mobile/features/splash/logic/bloc/splash_event.dart';
import 'package:mobile/features/splash/logic/bloc/splash_state.dart';

class SplashBloc extends Bloc<SplashEvent, SplashState> {
  static const _splashDuration = Duration(seconds: 2);

  SplashBloc() : super(const SplashInitial()) {
    on<StartSplashAnimation>(_onStartSplash);
  }

  Future<void> _onStartSplash(
    StartSplashAnimation event,
    Emitter<SplashState> emit,
  ) async {
    emit(const SplashAnimating());
    
    await Future.delayed(_splashDuration);
    
    emit(const SplashCompleted());
  }
}