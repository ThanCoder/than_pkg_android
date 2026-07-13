// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'package:flutter/services.dart';

class ThanBatteryInfo {
  final int batteryLevel;
  final bool isCharging;
  final String powerSource; // AC, USB, WIRELESS, BATTERY
  final String health; // GOOD, OVERHEAT, DEAD, etc.
  final double temperature; // e.g., 36.5 C
  final int voltage; // mV
  final String technology; // Li-ion
  ThanBatteryInfo({
    required this.batteryLevel,
    required this.isCharging,
    required this.powerSource,
    required this.health,
    required this.temperature,
    required this.voltage,
    required this.technology,
  });

  ThanBatteryInfo.fromMap(Map<dynamic, dynamic> map)
    : batteryLevel = map['batteryLevel'] ?? -1,
      isCharging = map['isCharging'] ?? false,
      powerSource = map['powerSource'] ?? 'UNKNOWN',
      health = map['health'] ?? 'UNKNOWN',
      temperature = (map['temperature'] as num?)?.toDouble() ?? 0.0,
      voltage = map['voltage'] ?? 0,
      technology = map['technology'] ?? 'UNKNOWN';
  @override
  String toString() {
    return 'ThanBatteryInfo(batteryLevel: $batteryLevel, isCharging: $isCharging, powerSource: $powerSource, health: $health, temperature: $temperature, voltage: $voltage, technology: $technology)';
  }
}

class BatteryManager {
  final MethodChannel _channel;
  final String _key;
  BatteryManager(this._channel, {this._key = 'batteryHandler'});

  final EventChannel _streamChannel = EventChannel(
    'than_pkg_android_battery_stream',
  );

  Future<ThanBatteryInfo> getBatteryInfo() async {
    final Map<dynamic, dynamic> res = await _channel.invokeMethod(
      '$_key/getBatteryInfo',
    );
    return ThanBatteryInfo.fromMap(res);
  }

  Stream<ThanBatteryInfo> get batteryInfoStream {
    return _streamChannel.receiveBroadcastStream().map(
      (event) => ThanBatteryInfo.fromMap(event as Map<dynamic, dynamic>),
    );
  }
}
