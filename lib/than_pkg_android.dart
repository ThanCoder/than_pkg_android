import 'package:flutter/services.dart';

import 'core/index.dart';

export 'core/selectors/media/media_file.dart';
export 'core/info/index.dart';
export 'core/audio/models/index.dart';
export 'core/path/enviroment_type.dart';
export 'core/safe_storage/safe_storage_file.dart';

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
  late final infoHandler = InfoHandler(_channel);
  late final audioInfoHandler = AudioInfoHandler(_channel);
  late final audioFileInfoHandler = AudioFileHandler(_channel);

  final flutterUtils = FlutterUtils();
}
