import 'package:flutter/services.dart';

class AndroidStorageHandler {
  final MethodChannel _channel;
  final String _key;
  const AndroidStorageHandler(this._channel, {this._key = 'storageHandler'});

  /// Path  - `/data/user/0/com.example.app/cache`
  Future<String?> getCachePath() async {
    return await _channel.invokeMethod<String>('$_key/getCachePath');
  }

  /// Path - `/data/user/0/com.example.app/files`
  Future<String?> getFilesPath() async {
    return await _channel.invokeMethod<String>('$_key/getFilesPath');
  }

  /// Path - `/storage/emulated/0/Android/data/com.example.app/files`
  Future<String?> getExternalFilesPath() async {
    return await _channel.invokeMethod<String>('$_key/getExternalFilesPath');
  }

  /// ### Device Storage Path
  ///
  /// `/storage/emulated/0/`
  String getDeviceStoragePath() {
    return '/storage/emulated/0';
  }

  /// ### Download Path
  String getDownloadPath() {
    return '${getDeviceStoragePath()}/Download';
  }

  /// ### Music Path
  String getMusicPath() {
    return '${getDeviceStoragePath()}/Music';
  }

  /// ### Documents Path
  String getDocumentsPath() {
    return '${getDeviceStoragePath()}/Documents';
  }

  /// ### DCIM Path
  String getDCIMPath() {
    return '${getDeviceStoragePath()}/DCIM';
  }

  /// ### Movies Path
  String getMoviesPath() {
    return '${getDeviceStoragePath()}/Movies';
  }

  /// ### Pictures Path
  String getPicturesPath() {
    return '${getDeviceStoragePath()}/Pictures';
  }
}
