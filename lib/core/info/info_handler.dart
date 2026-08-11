import 'package:flutter/services.dart';
import 'package:than_pkg_android/than_pkg_android.dart';
import 'index.dart';

class InfoHandler {
  final MethodChannel _channel;
  final String _key;
  const InfoHandler(this._channel, {this._key = 'infoHandler'});

  String _getMethod(String methodName) => '$_key/$methodName';

  /// AndroidInfo → OS information
  ///
  /// `version`
  /// `sdkInt`
  /// `securityPatch`
  /// `incremental`
  /// `releaseOrCodename`
  Future<AndroidInfo?> getAndroidInfo() async {
    final map = await _channel.invokeMapMethod<String, dynamic>(
      _getMethod('getAndroidInfo'),
    );
    if (map == null) return null;
    return AndroidInfo.fromMap(map);
  }

  ///AppInfo → app information
  ///
  ///`packageName`
  ///`appName`
  ///`versionCode`
  ///`firstInstallTime`
  ///`lastUpdateTime`
  ///`sourceDir`
  ///`publicSourceDir`
  ///`dataDir`
  ///`nativeLibraryDir`
  ///`installer`
  Future<AppInfo?> getAppInfo() async {
    final map = await _channel.invokeMapMethod<String, dynamic>(
      _getMethod('getAppInfo'),
    );
    if (map == null) return null;
    return AppInfo.fromMap(map);
  }

  ///DeviceInfo → hardware/build information
  ///
  ///`manufacturer`
  ///`model`
  ///`brand`
  ///`device`
  ///`product`
  ///`hardware`
  ///`board`
  ///`bootloader`
  ///`display`
  ///`host`
  ///`buildId`
  ///`tags`
  ///`type`
  ///`user`
  ///`supportedAbis`
  ///`supported32BitAbis`
  ///`supported64BitAbis`
  Future<DeviceInfo?> getDeviceInfo() async {
    final map = await _channel.invokeMapMethod<String, dynamic>(
      _getMethod('getDeviceInfo'),
    );
    if (map == null) return null;
    return DeviceInfo.fromMap(map);
  }

  /// DisplayInfo → screen information
  ///
  /// `width`
  /// `height`
  /// `density`
  /// `densityDpi`
  /// `scaledDensity`
  /// `xdpi`
  /// `ydpi`
  Future<DisplayInfo?> getDisplayInfo() async {
    final map = await _channel.invokeMapMethod<String, dynamic>(
      _getMethod('getDisplayInfo'),
    );
    if (map == null) return null;
    return DisplayInfo.fromMap(map);
  }

  ///LocaleInfo → language/region
  ///
  ///`language`
  ///`country`
  ///`locale`
  ///`displayLanguage`
  ///`displayCountry`
  Future<LocaleInfo?> getLocaleInfo() async {
    final map = await _channel.invokeMapMethod<String, dynamic>(
      _getMethod('getLocaleInfo'),
    );
    if (map == null) return null;
    return LocaleInfo.fromMap(map);
  }

  ///BatteryInfo → battery
  ///
  ///`level`
  ///`isCharging`
  Future<BatteryInfo?> getBatteryInfo() async {
    final map = await _channel.invokeMapMethod<String, dynamic>(
      _getMethod('getBatteryInfo'),
    );
    if (map == null) return null;
    return BatteryInfo.fromMap(map);
  }

  /// MemoryInfo → RAM
  ///
  ///`totalBytes`
  ///`availableBytes`
  ///`lowMemory`
  ///`thresholdBytes`
  Future<MemoryInfo?> getMemoryInfo() async {
    final map = await _channel.invokeMapMethod<String, dynamic>(
      _getMethod('getMemoryInfo'),
    );
    if (map == null) return null;
    return MemoryInfo.fromMap(map);
  }

  ///StorageInfo → storage
  ///
  ///`totalBytes`
  ///`availableBytes`
  ///`freeBytes`
  Future<StorageInfo?> getStorageInfo() async {
    final map = await _channel.invokeMapMethod<String, dynamic>(
      _getMethod('getStorageInfo'),
    );
    if (map == null) return null;
    return StorageInfo.fromMap(map);
  }

  ///FeatureInfo → hardware capabilities
  ///
  /// `camera`
  /// `cameraFront`
  /// `bluetooth`
  /// `bluetoothLe`
  /// `nfc`
  /// `gps`
  /// `wifi`
  /// `telephony`
  /// `fingerprint`
  /// `usbHost`
  /// `usbAccessory`
  /// `sensorAccelerometer`
  /// `sensorGyroscope`
  Future<FeatureInfo?> getFeatureInfo() async {
    final map = await _channel.invokeMapMethod<String, dynamic>(
      _getMethod('getFeatureInfo'),
    );
    if (map == null) return null;
    return FeatureInfo.fromMap(map);
  }
}
