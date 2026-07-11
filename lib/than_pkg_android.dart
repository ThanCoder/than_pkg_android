import 'package:flutter/services.dart';
import 'package:than_pkg_android/core/android_os.dart';
import 'package:than_pkg_android/core/android_safe_storage.dart';
import 'package:than_pkg_android/core/android_path_handler.dart';
import 'package:than_pkg_android/core/android_pdf_handler.dart';
import 'package:than_pkg_android/core/android_utils.dart';
import 'package:than_pkg_android/core/camera_handler.dart';
import 'package:than_pkg_android/core/media/media_selector.dart';
import 'package:than_pkg_android/core/uri_handler.dart';
import 'package:than_pkg_android/core/file_selector.dart';
import 'package:than_pkg_android/core/permission_handler.dart';
import 'package:than_pkg_android/core/storage_permission_handler.dart';
import 'package:than_pkg_android/core/video_handler.dart';
import 'package:than_pkg_android/core/wifi_handler.dart';

export 'core/media/media_file.dart';

class ThanPkgAndroid {
  static ThanPkgAndroid? _instance;
  static ThanPkgAndroid get getInstance {
    _instance ??= ThanPkgAndroid();
    return _instance!;
  }

  final _channel = MethodChannel('than_pkg_android');

  late final os = AndroidOs(_channel);
  late final wifiHandler = WifiHandler(_channel);
  late final cameraHandler = CameraHandler(_channel);
  late final fileSelector = FileSelector(_channel);
  late final uriHandler = UriHandler(_channel);
  late final storagePermissionHandler = StoragePermissionHandler(_channel);
  late final permissionHandler = PermissionHandler(_channel);
  late final pdfHandler = AndroidPdfHandler(_channel);
  late final pathHandler = AndroidPathHandler(_channel);
  late final safeStorage = AndroidSafeStorage(_channel);
  late final videoHandler = VideoHandler(_channel);
  late final mediaSelector = MediaSelector(_channel);
  final utils = AndroidUtils();
}
