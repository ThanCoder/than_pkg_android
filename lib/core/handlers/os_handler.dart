import 'package:flutter/services.dart';
import 'package:than_pkg_android/core/types/os_build_info.dart';

class OsHandler {
  final MethodChannel _channel;
  final String _key;
  const OsHandler(this._channel, {this._key = 'osHandler'});

  String _getMethod(String methodName) => '$_key/$methodName';

  /// App Screen ကို ဓာတ်ပုံရိုက်ပြီး သိမ်းဆည်းထားသော File Path ကို ပြန်ပေးရန်
  Future<String?> takeScreenshot() async {
    return await _channel.invokeMethod<String>(_getMethod('takeScreenshot'));
  }

  /// ဖုန်းရဲ့ မွေးရာပါ ဗီဒီယိုဖမ်းစနစ် (System Screen Recorder) ကို လှမ်းနှိုးရန်
  Future<bool> startScreenRecord() async {
    return (await _channel.invokeMethod<bool>(
          _getMethod('startScreenRecord'),
        ) ??
        false);
  }

  /// ### Android -> `Build.VERSION`
  Future<OsBuildInfo?> getOsBuildInfo() async {
    final map = await _channel.invokeMethod<Map>(_getMethod('getOsBuildInfo'));
    if (map == null) return null;
    return OsBuildInfo.fromMap(map);
  }

  /// ### Android Native Message
  Future<void> showToast(String message, {bool isLong = false}) async {
    await _channel.invokeMethod(_getMethod('showToast'), {
      'message': message,
      'isLong': isLong,
    });
  }

  /// ### Android Native KeepScreen
  Future<void> keepScreenOn(bool enabled) async {
    await _channel.invokeMethod(_getMethod('keepScreenOn'), {
      'enabled': enabled,
    });
  }

  /// ### Android Native Brightness
  ///
  /// Range: `0.0`-`1.0`
  Future<void> setBrightness(double brightness) async {
    await _channel.invokeMethod(_getMethod('setBrightness'), {
      'brightness': brightness,
    });
  }
}
