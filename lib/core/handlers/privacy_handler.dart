import 'package:flutter/services.dart';

class PrivacyHandler {
  final MethodChannel _channel;
  final String _key;
  const PrivacyHandler(this._channel, {this._key = 'privacyHandler'});

  Future<bool> enableSecure() async {
    return (await _channel.invokeMethod<bool>('$_key/enableSecure')) ?? false;
  }

  Future<bool> disableSecure() async {
    return (await _channel.invokeMethod<bool>('$_key/disableSecure')) ?? false;
  }
}
