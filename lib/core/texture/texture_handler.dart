// ignore_for_file: non_constant_identifier_names

import 'package:flutter/services.dart';

class TextureHandler {
  final MethodChannel _channel;
  final String _key;
  const TextureHandler(this._channel, {this._key = 'textureHandler'});

  /// ### Create Texture Id
  Future<int?> createTexture() async {
    final res = await _channel.invokeMethod<int>('$_key/createTexture');
    return res;
  }

  /// ### Release TextureId
  Future<bool> releaseTexture(int textureId) async {
    final res = await _channel.invokeMethod<bool>('$_key/releaseTexture', {
      'textureId': textureId,
    });
    return res ?? false;
  }

  /// ### Get Surface Pointer for Native/FFI
  ///
  /// Return -> `0` is Error
  ///
  Future<int> getSurfacePointer(
    int textureId, {
    required int width,
    required int height,
  }) async {
    final res = await _channel.invokeMethod<int>('$_key/getSurfacePointer', {
      'textureId': textureId,
      'width': width,
      'height': height,
    });
    return res ?? 0;
  }
}
