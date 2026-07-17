import 'package:flutter/services.dart';
import 'package:than_pkg_android/core/handlers/device_sensor_handler.dart';
import 'package:than_pkg_android/core/handlers/os_handler.dart';
import 'package:than_pkg_android/core/handlers/android_safe_storage.dart';
import 'package:than_pkg_android/core/handlers/android_path_handler.dart';
import 'package:than_pkg_android/core/handlers/android_pdf_handler.dart';
import 'package:than_pkg_android/core/flutter_utils.dart';
import 'package:than_pkg_android/core/handlers/app_setting_handler.dart';
import 'package:than_pkg_android/core/handlers/brightness_handler.dart';
import 'package:than_pkg_android/core/handlers/camera_handler.dart';
import 'package:than_pkg_android/core/handlers/intent_transfer_handler.dart';
import 'package:than_pkg_android/core/handlers/launch_handler.dart';
import 'package:than_pkg_android/core/handlers/privacy_handler.dart';
import 'package:than_pkg_android/core/handlers/texture_handler.dart';
import 'package:than_pkg_android/core/managers/battery_manager.dart';
import 'package:than_pkg_android/core/managers/network_manager.dart';
import 'package:than_pkg_android/core/selectors/media/media_selector.dart';
import 'package:than_pkg_android/core/handlers/notification_handler.dart';
import 'package:than_pkg_android/core/handlers/simple_notification_handler.dart';
import 'package:than_pkg_android/core/handlers/orientation_handler.dart';
import 'package:than_pkg_android/core/handlers/sound_handler.dart';
import 'package:than_pkg_android/core/handlers/uri_handler.dart';
import 'package:than_pkg_android/core/selectors/file_selector.dart';
import 'package:than_pkg_android/core/handlers/permission_handler.dart';
import 'package:than_pkg_android/core/handlers/storage_permission_handler.dart';
import 'package:than_pkg_android/core/handlers/video_handler.dart';
import 'package:than_pkg_android/core/handlers/wifi_handler.dart';
import 'package:than_pkg_android/core/managers/native_download_manager.dart';

export 'core/selectors/media/media_file.dart';

class ThanPkgAndroid {
  static ThanPkgAndroid? _instance;
  static ThanPkgAndroid get getInstance {
    _instance ??= ThanPkgAndroid();
    return _instance!;
  }

  final _channel = MethodChannel('than_pkg_android');

  late final osHandler = OsHandler(_channel);
  late final privacyHandler = PrivacyHandler(_channel);
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
  late final soundHandler = SoundHandler(_channel);
  late final brightnessHandler = BrightnessHandler(_channel);
  late final orientationHandler = OrientationHandler(_channel);
  late final intentTransferHandler = IntentTransferHandler(_channel);
  late final notificationHandler = NotificationHandler(_channel);
  late final simpleNotificationHandler = SimpleNotificationHandler(_channel);
  late final appSettingHandler = AppSettingHandler(_channel);
  late final deviceSensorHandler = DeviceSensorHandler(_channel);
  late final nativeDownloadManager = NativeDownloadManager(_channel);
  late final launchHandler = LaunchHandler(_channel);
  late final batteryManager = BatteryManager(_channel);
  late final networkManager = NetworkManager(_channel);
  late final textureHandler = TextureHandler(_channel);

  final flutterUtils = FlutterUtils();
}
