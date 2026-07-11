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
}
