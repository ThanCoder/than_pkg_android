import 'package:flutter/services.dart';
import 'package:than_pkg_android/core/types/os_build_info.dart';

class OsHandler {
  final MethodChannel _channel;
  final String _key;
  const OsHandler(this._channel, {this._key = 'osHandler'});

  /// App Screen ကို ဓာတ်ပုံရိုက်ပြီး သိမ်းဆည်းထားသော File Path ကို ပြန်ပေးရန်
  Future<String?> takeScreenshot() async {
    return await _channel.invokeMethod<String>('$_key/takeScreenshot');
  }

  /// ဖုန်းရဲ့ မွေးရာပါ ဗီဒီယိုဖမ်းစနစ် (System Screen Recorder) ကို လှမ်းနှိုးရန်
  Future<bool> startScreenRecord() async {
    return (await _channel.invokeMethod<bool>('$_key/startScreenRecord')) ??
        false;
  }

  /// ### Android -> `Build.VERSION`
  Future<OsBuildInfo?> getOsBuildInfo() async {
    final map = await _channel.invokeMethod<Map>('$_key/getOsBuildInfo');
    if (map == null) return null;
    return OsBuildInfo.fromMap(map);
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
