// ignore_for_file: constant_identifier_names

import 'package:flutter/services.dart';

enum OrientationMode {
  SCREEN_ORIENTATION_PORTRAIT(1),
  SCREEN_ORIENTATION_LANDSCAPE(0),
  SCREEN_ORIENTATION_SENSOR_LANDSCAPE(6),
  SCREEN_ORIENTATION_UNSPECIFIED(-1),
  SCREEN_ORIENTATION_UNSET(-2),
  SCREEN_ORIENTATION_USER(2),
  SCREEN_ORIENTATION_BEHIND(3),
  SCREEN_ORIENTATION_SENSOR(4),
  SCREEN_ORIENTATION_NOSENSOR(5),
  SCREEN_ORIENTATION_SENSOR_PORTRAIT(7),
  SCREEN_ORIENTATION_REVERSE_LANDSCAPE(8),
  SCREEN_ORIENTATION_REVERSE_PORTRAIT(9),
  SCREEN_ORIENTATION_FULL_SENSOR(10),
  SCREEN_ORIENTATION_USER_LANDSCAPE(11),
  SCREEN_ORIENTATION_USER_PORTRAIT(12),
  SCREEN_ORIENTATION_FULL_USER(13),
  SCREEN_ORIENTATION_LOCKED(14);

  final int value;
  const OrientationMode(this.value);

  static OrientationMode fromValue(int val) {
    return OrientationMode.values.firstWhere(
      (element) => element.value == val,
      orElse: () => OrientationMode.SCREEN_ORIENTATION_UNSPECIFIED,
    );
  }
}

extension OrientationModeExt on OrientationMode {
  bool get isLandscape =>
      this == OrientationMode.SCREEN_ORIENTATION_LANDSCAPE ||
      this == OrientationMode.SCREEN_ORIENTATION_SENSOR_LANDSCAPE ||
      this == OrientationMode.SCREEN_ORIENTATION_REVERSE_LANDSCAPE ||
      this == OrientationMode.SCREEN_ORIENTATION_USER_LANDSCAPE;

  bool get isPortrait =>
      this == OrientationMode.SCREEN_ORIENTATION_PORTRAIT ||
      this == OrientationMode.SCREEN_ORIENTATION_SENSOR_PORTRAIT ||
      this == OrientationMode.SCREEN_ORIENTATION_REVERSE_PORTRAIT ||
      this == OrientationMode.SCREEN_ORIENTATION_USER_PORTRAIT;
}

class OrientationHandler {
  final MethodChannel _channel;
  final String _key;
  const OrientationHandler(this._channel, {this._key = 'orientationHandler'});

  /// ### Android Orientation
  ///
  Future<bool> setOrientation(OrientationMode mode) async {
    return (await _channel.invokeMethod<bool>('$_key/setOrientation', {
          'mode': mode.value,
        })) ??
        false;
  }

  /// ### Android Orientation
  ///
  Future<OrientationMode> getOrientation() async {
    final res = await _channel.invokeMethod<int>('$_key/getOrientation');
    return OrientationMode.fromValue(res ?? -1);
  }
}
