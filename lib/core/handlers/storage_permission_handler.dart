import 'package:flutter/services.dart';

class StoragePermissionHandler {
  final MethodChannel _channel;
  final String _key;
  const StoragePermissionHandler(
    this._channel, {
    this._key = 'storagePermissionHandler',
  });

  /// ### Request Storage Permission
  ///
  /// Android 11 (API 30)
  ///
  /// Android 6.0 To Android 10
  /// ```xml
  ///<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  ///<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
  ///<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
  ///```
  ///
  Future<bool> requestStoragePermission() async {
    final res = await _channel.invokeMethod<bool>(
      '$_key/requestStoragePermission',
    );
    return res ?? false;
  }

  /// ### Check Storage Permission
  ///
  /// ```xml
  /// <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  /// ```
  Future<bool> isStoragePermissionGranted() async {
    final res = await _channel.invokeMethod<bool>(
      '$_key/isStoragePermissionGranted',
    );
    return res ?? false;
  }
}
