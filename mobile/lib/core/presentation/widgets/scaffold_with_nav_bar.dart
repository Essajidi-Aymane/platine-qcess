import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:mobile/core/presentation/widgets/bottom_nav_bar.dart';

class ScaffoldWithNavBar extends StatelessWidget {
  final StatefulNavigationShell navigationShell;

  const ScaffoldWithNavBar({required this.navigationShell, super.key});

  // ScrollController global pour chaque onglet
  static final Map<int, ScrollController> _scrollControllers = {};

  static ScrollController getScrollController(int index) {
    if (!_scrollControllers.containsKey(index)) {
      _scrollControllers[index] = ScrollController();
    }
    return _scrollControllers[index]!;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: navigationShell,
      bottomNavigationBar: BottomNavBarWidget(
        currentIndex: navigationShell.currentIndex,
        onTap: (index) => _onItemTapped(index, context),
      ),
    );
  }

  void _onItemTapped(int index, BuildContext context) {
    if (index == navigationShell.currentIndex) {
      // Remonter en haut de l'onglet actuel
      final controller = _scrollControllers[index];
      if (controller != null && controller.hasClients) {
        controller.animateTo(
          0,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    } else {
      navigationShell.goBranch(
        index,
        initialLocation: index == navigationShell.currentIndex,
      );
    }
  }
}
