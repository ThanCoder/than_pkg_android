import 'package:flutter/services.dart';

class VideoHandler {
  final MethodChannel _channel;
  final String _key;
  const VideoHandler(this._channel, {this._key = 'videoHandler'});

  ///### Get Video Duration -> miliseconds
  Future<int?> getDuration(String path) async {
    return await _channel.invokeMethod<int>('$_key/getDuration', {
      'path': path,
    });
  }

  /// Return -> `absolutePath`
  ///
  /// bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream)
  ///
  /// မပါလာရင် Default အနေနဲ့ 0 microsecond, width=0, height=0 လို့ သတ်မှတ်မယ်
  Future<String?> saveThumbnail(
    String path,
    String savePath, {
    int width = 0,
    int height = 0,
    Duration time = const Duration(seconds: 1),
  }) async {
    return await _channel.invokeMethod<String>('$_key/saveThumbnail', {
      'path': path,
      'savePath': savePath,
      'width': width,
      'height': height,
      'time': time.inMilliseconds,
    });
  }

  /// bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream)
  ///
  Future<Uint8List?> getThumbnail(
    String path, {
    int width = 0,
    int height = 0,
    Duration time = const Duration(seconds: 1),
  }) async {
    return await _channel.invokeMethod<Uint8List>('$_key/getThumbnail', {
      'path': path,
      'width': width,
      'height': height,
      'time': time.inMilliseconds,
    });
  }
}
