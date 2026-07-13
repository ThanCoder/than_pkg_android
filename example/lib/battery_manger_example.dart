// ignore_for_file: avoid_print

import 'dart:async';

import 'package:than_pkg_android/core/managers/battery_manager.dart';
import 'package:than_pkg_android/than_pkg_android.dart';

void main() async {
  StreamSubscription<ThanBatteryInfo>
  sub = ThanPkgAndroid.getInstance.batteryManager.batteryInfoStream.listen((
    event,
  ) {
    print('ThanDev: Stream-> $event');

    /// ThanBatteryInfo(batteryLevel: 100, isCharging: true, powerSource: USB, health: GOOD, temperature: 33.400001525878906, voltage: 4403, technology: Li-poly)
  });

  // need to close
  sub.cancel();

  // one time
  ThanPkgAndroid.getInstance.batteryManager.getBatteryInfo();

  /// ThanBatteryInfo(batteryLevel: 100, isCharging: true, powerSource: USB, health: GOOD, temperature: 33.400001525878906, voltage: 4403, technology: Li-poly)
}
