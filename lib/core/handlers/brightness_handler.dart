import 'package:flutter/services.dart';

class BrightnessHandler {
  final MethodChannel _channel;
  final String _key;

  const BrightnessHandler(this._channel, {this._key = 'brightnessHandler'});

  String _getMethod(String methodName) => '$_key/$methodName';

  /// Sets the screen brightness for the current Android window.
  ///
  /// The value must be between `0.0` and `1.0`.
  /// This only overrides the brightness of the current window
  /// and does not modify the system brightness setting.
  Future<bool> setScreenBrightness(double brightness) async {
    return (await _channel.invokeMethod<bool>(
          _getMethod('setScreenBrightness'),
          {'brightness': brightness},
        )) ??
        false;
  }

  /// Returns the current screen brightness.
  ///
  /// If a custom window brightness is set, its value is returned.
  /// Otherwise, the current system brightness is returned.
  ///
  /// Returns `null` if the brightness cannot be determined.
  Future<double?> getScreenBrightness() async {
    return await _channel.invokeMethod<double>(
      _getMethod('getScreenBrightness'),
    );
  }

  /// Restores the window brightness to the system brightness.
  ///
  /// This removes the custom brightness override from the current
  /// Android window without changing the system brightness setting.
  Future<bool> restoreScreenBrightness() async {
    return (await _channel.invokeMethod<bool>(
          _getMethod('restoreScreenBrightness'),
        )) ??
        false;
  }

  /// Returns whether the current Android window has a custom
  /// brightness override.
  ///
  /// Returns `true` when a custom brightness is applied, otherwise `false`.
  Future<bool> isScreenBrightnessOverridden() async {
    return (await _channel.invokeMethod<bool>(
          _getMethod('isScreenBrightnessOverridden'),
        )) ??
        false;
  }
}
