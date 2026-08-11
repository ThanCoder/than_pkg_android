import 'package:flutter/services.dart';

class CameraHandler {
  final MethodChannel _channel;
  final String _key;
  const CameraHandler(this._channel, {this._key = 'cameraHandler'});

  String _getMethod(String methodName) => '$_key/$methodName';

  /// ### Example
  /// 
  /// ```dart
  /// final per = ThanPkgAndroid.getInstance.permissionHandler;
  /// if (!await per.isCameraPermission()) {
  ///   await per.requestCameraPermission();
  /// }
  ///
  /// final pkg = ThanPkgAndroid.getInstance.cameraHandler;
  /// final hasFlashlight = await pkg.hasFlashlight();
  ///
  /// print('Dev: hasFlashlight: $hasFlashlight');
  ///
  /// if (hasFlashlight) {
  ///   await pkg.toggleTorch(enable: false);
  /// } else {
  ///   await pkg.toggleTorch(enable: true);
  /// }
  /// ```
  Future<bool> toggleTorch({required bool enable}) async {
    return await _channel.invokeMethod<bool>(_getMethod('toggleTorch'), {
          'enable': enable,
        }) ??
        false;
  }

  Future<bool> hasFlashlight() async {
    return await _channel.invokeMethod<bool>(_getMethod('hasFlashlight')) ??
        false;
  }

  /// ### Open Camera
  ///
  /// Return -> Uri
  /// `/data/user/0/com.example.than_pkg_android_example/cache/JPEG_20260812_000214_2219569971034127165.jpg`
  ///
  ///### Uri -> Path
  ///```dart
  ///final handler = ThanPkgAndroid.getInstance.uriHandler;
  ///
  ///await handler.copyContentToFile(
  ///  '[content://com.android.fileexplorer.myprovider/external_files]',
  ///  '[output]',
  ///);
  ///await handler.moveContentToFile(
  ///  '[content://com.android.fileexplorer.myprovider/external_files]',
  ///  '[output]',
  ///);
  ///```
  Future<String?> takePicture() async {
    return await _channel.invokeMethod<String>(_getMethod('takePicture'));
  }
}
