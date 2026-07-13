import 'package:flutter/services.dart';

class BrightnessHandler {
  final MethodChannel _channel;
  final String _key;
  const BrightnessHandler(this._channel, {this._key = 'brightnessHandler'});

  /// ### Android Screen Brightness
  /// Range 0.0 - 1.0
  ///
  Future<bool> setScreenBrightness(double brightness) async {
    return (await _channel.invokeMethod<bool>('$_key/setScreenBrightness', {
          'brightness': brightness,
        })) ??
        false;
  }

  /// ### Android Current Brightness
  ///
  /// if error -> null
  ///
  Future<double?> getScreenBrightness(double volume) async {
    return await _channel.invokeMethod<double>('$_key/getScreenBrightness');
  }
}
