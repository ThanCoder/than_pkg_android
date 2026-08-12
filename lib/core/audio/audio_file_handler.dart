import 'package:flutter/services.dart';
import 'package:than_pkg_android/core/audio/models/index.dart';
// import 'index.dart';

class AudioFileHandler {
  final MethodChannel _channel;
  final String _key;
  const AudioFileHandler(this._channel, {this._key = 'AudioFileInfoHandler'});

  String _getMethod(String methodName) => '$_key/$methodName';

  Future<AudioFileInfo?> getAudioFileInfo(String path) async {
    final map = await _channel.invokeMapMethod<String, dynamic>(
      _getMethod('getAudioFileInfo'),
      {'path': path},
    );
    if (map == null) return null;
    return AudioFileInfo.fromMap(map);
  }

  /// Return -> `coverFile.absolutePath`
  ///
  /// val coverName ="${audioFile.nameWithoutExtension}.jpg"
  ///
  ///val coverFile =File(cacheDir,coverName)
  ///
  Future<String?> getAudioCoverArt(String path, String cacheDirPath) async {
    final map = await _channel.invokeMethod<String>(
      _getMethod('getAudioCoverArt'),
      {'path': path, 'cachePath': cacheDirPath},
    );
    if (map == null) return null;
    return map;
  }
}
