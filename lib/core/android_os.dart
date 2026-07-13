import 'package:flutter/services.dart';

class AndroidOs {
  final MethodChannel _channel;
  final String _key;
  const AndroidOs(this._channel, {this._key = 'os'});

  /// ### Android -> `Build.VERSION`
  Future<Map<String, dynamic>?> getOsBuildInfo() async {
    final map = await _channel.invokeMethod<Map>('$_key/getOsBuildInfo');
    if (map == null) return null;
    return Map<String, dynamic>.from(map);
  }

  /// ### Android Native Message
  Future<void> showToast(String message, {bool isLong = false}) async {
    await _channel.invokeMethod('$_key/showToast', {
      'message': message,
      'isLong': isLong,
    });
  }

  /// ### Android Native KeepScreen
  Future<void> keepScreenOn(bool enabled) async {
    await _channel.invokeMethod('$_key/keepScreenOn', {'enabled': enabled});
  }

  /// ### Android Native Brightness
  /// 
  /// Range: `0.0`-`1.0`
  Future<void> setBrightness(double brightness) async {
    await _channel.invokeMethod('$_key/setBrightness', {
      'brightness': brightness,
    });
  }
}
