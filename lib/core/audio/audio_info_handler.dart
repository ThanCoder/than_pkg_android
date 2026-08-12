import 'package:flutter/services.dart';
import 'package:than_pkg_android/than_pkg_android.dart';
// import 'index.dart';

class AudioInfoHandler {
  final MethodChannel _channel;
  final String _key;
  const AudioInfoHandler(this._channel, {this._key = 'AudioHandler'});

  String _getMethod(String methodName) => '$_key/$methodName';

  Future<AudioInfo?> getAudioInfo() async {
    final map = await _channel.invokeMapMethod<String, dynamic>(
      _getMethod('getAudioInfo'),
    );
    if (map == null) return null;
    return AudioInfo.fromMap(map);
  }

  Future<Map<String, dynamic>?> getVolumeInfoRaw() async {
    final map = await _channel.invokeMapMethod<String, dynamic>(
      _getMethod('getVolumeInfo'),
    );
    if (map == null) return null;
    return map;
  }

  Future<List<dynamic>?> getAudioDevicesRaw() async {
    final list = await _channel.invokeListMethod<dynamic>(
      _getMethod('getAudioDevices'),
    );
    if (list == null) return null;
    return list;
  }

  Future<bool?> isMusicActive() async {
    final map = await _channel.invokeMethod<bool>(_getMethod('isMusicActive'));
    if (map == null) return null;
    return map;
  }

  Future<bool?> isSpeakerphoneOn() async {
    final map = await _channel.invokeMethod<bool>(
      _getMethod('isSpeakerphoneOn'),
    );
    if (map == null) return null;
    return map;
  }

  Future<int?> getVolume({required AudioManagerStreamType streamType}) async {
    final map = await _channel.invokeMethod<int>(_getMethod('getVolume'), {
      'stream_type': streamType.value,
    });
    if (map == null) return null;
    return map;
  }

  Future<int?> setVolume(
    int volume, {
    required AudioManagerStreamType streamType,
  }) async {
    final map = await _channel.invokeMethod<int>(_getMethod('setVolume'), {
      'stream_type': streamType.value,
      'volume': volume,
    });
    if (map == null) return null;
    return map;
  }
}

/*


  "setVolume" -> {
      setVolume(ctx, call, result)
  }
 */
