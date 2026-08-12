import 'package:flutter/services.dart';
import 'package:than_pkg_android/than_pkg_android.dart';

class AndroidPathHandler {
  final MethodChannel _channel;
  final String _key;
  const AndroidPathHandler(this._channel, {this._key = 'storageHandler'});

  String _getMethod(String methodName) => '$_key/$methodName';

  /// Returns:
  ///The path of the directory holding application cache files.
  ///
  ///`public abstract java.io.File getCacheDir()`
  ///
  /// Path  - `/data/user/0/[com.example.app]/cache`
  Future<String?> getCachePath() async {
    return await _channel.invokeMethod<String>(_getMethod('getCachePath'));
  }

  /// Returns the absolute path to the directory on the filesystem where files created with openFileOutput are stored.
  ///
  /// The returned path may change over time if the calling app is moved to an adopted storage device, so only relative paths should be persisted.
  /// No additional permissions are required for the calling app to read or write files under the returned path.
  ///
  /// Returns:
  /// The path of the directory holding application files.
  ///
  /// Path - `/data/user/0/[com.example.app]/files`
  Future<String?> getFilesPath() async {
    return await _channel.invokeMethod<String>(_getMethod('getFilesPath'));
  }

  ///
  /// Path - `/storage/emulated/0/Android/data/[com.example.app]/files`
  ///
  /// `DIRECTORY_MUSIC`
  /// `DIRECTORY_PODCASTS`
  /// `DIRECTORY_RINGTONES`
  /// `DIRECTORY_ALARMS`
  /// `DIRECTORY_NOTIFICATIONS`
  /// `DIRECTORY_PICTURES`
  /// `DIRECTORY_MOVIES`
  /// `DIRECTORY_DOWNLOADS`
  /// `DIRECTORY_DCIM`
  /// `DIRECTORY_DOCUMENTS`
  /// `DIRECTORY_SCREENSHOTS`
  /// `DIRECTORY_AUDIOBOOKS`
  /// `DIRECTORY_RECORDINGS`
  Future<String?> getExternalFilesPath({EnviromentType? envType}) async {
    return await _channel.invokeMethod<String>(
      _getMethod('getExternalFilesPath'),
      envType != null ? {'type': envType.value} : {},
    );
  }

  /// ### Device Storage Path
  ///
  /// Path -> `/storage/emulated/0`
  String getDeviceStoragePath() {
    return '/storage/emulated/0';
  }

  /// ### Download Path
  /// Path -> `/storage/emulated/0/Download`
  String getDownloadPath() {
    return '${getDeviceStoragePath()}/Download';
  }

  /// ### Music Path
  /// Path -> `/storage/emulated/0/Music`
  String getMusicPath() {
    return '${getDeviceStoragePath()}/Music';
  }

  /// ### Documents Path
  /// Path -> `/storage/emulated/0/Documents`
  String getDocumentsPath() {
    return '${getDeviceStoragePath()}/Documents';
  }

  /// ### DCIM Path
  /// Path -> `/storage/emulated/0/DCIM`
  String getDCIMPath() {
    return '${getDeviceStoragePath()}/DCIM';
  }

  /// ### Movies Path
  /// Path -> `/storage/emulated/0/Movies`
  String getMoviesPath() {
    return '${getDeviceStoragePath()}/Movies';
  }

  /// ### Pictures Path
  /// Path -> `/storage/emulated/0/Pictures`
  String getPicturesPath() {
    return '${getDeviceStoragePath()}/Pictures';
  }
}
