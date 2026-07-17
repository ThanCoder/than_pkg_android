import 'package:flutter/services.dart';

class SoundHandler {
  final MethodChannel _channel;
  final String _key;
  const SoundHandler(this._channel, {this._key = 'soundHandler'});

  /// ### Set Android Volumn
  ///
  /// Value Range -> 0.0 - 1.0
  ///
  Future<bool> setVolume(double volume) async {
    return (await _channel.invokeMethod<bool>('$_key/setVolume', {
          'volume': volume,
        })) ??
        false;
  }

  /// ### Android Volume
  ///
  /// if error -> null
  ///
  Future<double?> getVolume() async {
    return await _channel.invokeMethod<double>('$_key/getVolume');
  }
}
