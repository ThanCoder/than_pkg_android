import 'package:flutter/services.dart';

class CameraHandler {
  final MethodChannel _channel;
  final String _key;
  const CameraHandler(this._channel, {this._key = 'cameraHandler'});

  Future<bool> toggleTorch() async {
    return await _channel.invokeMethod<bool>('$_key/toggleTorch') ?? false;
  }

  Future<bool> hasFlashlight() async {
    return await _channel.invokeMethod<bool>('$_key/hasFlashlight') ?? false;
  }

  Future<String?> takePicture() async {
    return await _channel.invokeMethod<String>('$_key/takePicture');
  }
}
