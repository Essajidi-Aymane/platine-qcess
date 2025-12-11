import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mobile/features/access/logic/bloc/access_bloc.dart';
import 'package:mobile/features/access/logic/bloc/access_event.dart';
import 'package:mobile_scanner/mobile_scanner.dart';

class ScannerWidget extends StatelessWidget {
  const ScannerWidget({super.key});

  @override
  Widget build(BuildContext context) {
    return MobileScanner(
      onDetect: (BarcodeCapture capture) {
        for (final barcode in capture.barcodes) {
          final code = barcode.rawValue;
          if (code != null) {
            try {
              final data = jsonDecode(code);
              final zoneId = data['zoneId'];
              if (zoneId is int) {
                context.read<AccessBloc>().add(ScanQrCodeEvent(zoneId));
                break;
              }
            } catch (_) {
              // QR code non valide, ignorer
            }
          }
        }
      },
    );
  }
}