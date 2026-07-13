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

  /// ```xml  
  /// <provider
  ///     android:name="androidx.core.content.FileProvider"
  ///     android:authorities="${applicationId}.file_provider"
  ///     android:exported="false"
  ///     android:grantUriPermissions="true">
  ///     <meta-data
  ///         android:name="android.support.FILE_PROVIDER_PATHS"
  ///         android:resource="@xml/file_paths" />
  /// </provider>
  /// ```
  Future<String?> takePicture() async {
    return await _channel.invokeMethod<String>('$_key/takePicture');
  }
}
