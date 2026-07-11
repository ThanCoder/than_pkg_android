import 'package:flutter/services.dart';

class PermissionHandler {
  final MethodChannel _channel;
  final String _key;

  const PermissionHandler(this._channel, {this._key = 'permissionHandler'});

  /// ### Camera Permission
  /// `AndroidManifest.xml` တွင် ထည့်ရန်:
  /// ```xml
  /// <uses-permission android:name="android.permission.CAMERA" />
  /// ```
  Future<bool> isCameraPermission() async =>
      await _channel.invokeMethod<bool>('$_key/isCameraPermission') ?? false;
  Future<bool> requestCameraPermission() async =>
      await _channel.invokeMethod<bool>('$_key/requestCameraPermission') ??
      false;

  /// ### Location Permission
  /// `AndroidManifest.xml` တွင် ထည့်ရန်:
  /// ```xml
  /// <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
  /// <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
  /// ```
  Future<bool> isLocationPermission() async =>
      await _channel.invokeMethod<bool>('$_key/isLocationPermission') ?? false;
  Future<bool> requestLocationPermission() async =>
      await _channel.invokeMethod<bool>('$_key/requestLocationPermission') ??
      false;

  /// ### Microphone / Audio Record Permission
  /// `AndroidManifest.xml` တွင် ထည့်ရန်:
  /// ```xml
  /// <uses-permission android:name="android.permission.RECORD_AUDIO" />
  /// ```
  Future<bool> isAudioRecordPermission() async =>
      await _channel.invokeMethod<bool>('$_key/isAudioRecordPermission') ??
      false;
  Future<bool> requestAudioRecordPermission() async =>
      await _channel.invokeMethod<bool>('$_key/requestAudioRecordPermission') ??
      false;

  /// ### Contacts Permission
  /// `AndroidManifest.xml` တွင် ထည့်ရန်:
  /// ```xml
  /// <uses-permission android:name="android.permission.READ_CONTACTS" />
  /// <uses-permission android:name="android.permission.WRITE_CONTACTS" /> <!-- လိုအပ်မှ ထည့်ရန် -->
  /// ```
  Future<bool> isContactsPermission() async =>
      await _channel.invokeMethod<bool>('$_key/isContactsPermission') ?? false;
  Future<bool> requestContactsPermission() async =>
      await _channel.invokeMethod<bool>('$_key/requestContactsPermission') ??
      false;

  /// ### Phone Call Permission
  /// `AndroidManifest.xml` တွင် ထည့်ရန်:
  /// ```xml
  /// <uses-permission android:name="android.permission.CALL_PHONE" />
  /// ```
  Future<bool> isPhonePermission() async =>
      await _channel.invokeMethod<bool>('$_key/isPhonePermission') ?? false;
  Future<bool> requestPhonePermission() async =>
      await _channel.invokeMethod<bool>('$_key/requestPhonePermission') ??
      false;

  /// ### Notification Permission (Android 13+)
  /// `AndroidManifest.xml` တွင် ထည့်ရန်:
  /// ```xml
  /// <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
  /// ```
  Future<bool> isNotificationPermission() async =>
      await _channel.invokeMethod<bool>('$_key/isNotificationPermission') ??
      false;
  Future<bool> requestNotificationPermission() async =>
      await _channel.invokeMethod<bool>(
        '$_key/requestNotificationPermission',
      ) ??
      false;

  /// ### Bluetooth Permission (Android 12+ Connect & Scan)
  /// `AndroidManifest.xml` တွင် ထည့်ရန်:
  /// ```xml
  /// <!-- Android 12 နှင့် အထက်အတွက် -->
  /// <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
  /// <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
  /// <!-- Android 11 နှင့် အောက်အတွက် (လိုအပ်ပါက) -->
  /// <uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
  /// <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
  /// ```
  Future<bool> isBluetoothPermission() async =>
      await _channel.invokeMethod<bool>('$_key/isBluetoothPermission') ?? false;
  Future<bool> requestBluetoothPermission() async =>
      await _channel.invokeMethod<bool>('$_key/requestBluetoothPermission') ??
      false;

  /// ### Media Visual Permission (Android 13+ Photos & Videos)
  /// `AndroidManifest.xml` တွင် ထည့်ရန်:
  /// ```xml
  /// <!-- Android 13 နှင့် အထက်အတွက် -->
  /// <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
  /// <uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
  /// <!-- Android 12 နှင့် အောက်အတွက် -->
  /// <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
  /// ```
  Future<bool> isMediaPermission() async =>
      await _channel.invokeMethod<bool>('$_key/isMediaPermission') ?? false;
  Future<bool> requestMediaPermission() async =>
      await _channel.invokeMethod<bool>('$_key/requestMediaPermission') ??
      false;

  /// ### Battery Optimization Permission
  /// `AndroidManifest.xml` တွင် ထည့်ရန်:
  /// ```xml
  /// <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
  /// ```
  Future<bool> isBatteryOptimizationIgnored() async =>
      await _channel.invokeMethod<bool>('$_key/isBatteryOptimizationIgnored') ??
      false;
  Future<void> requestBatteryOptimizationPermission() async => await _channel
      .invokeMethod<void>('$_key/requestBatteryOptimizationPermission');

  /// ### Full Storage Manager Permission (MANAGE_EXTERNAL_STORAGE)
  /// `AndroidManifest.xml` တွင် ထည့်ရန်:
  /// ```xml
  /// <!-- Android 11 နှင့် အထက်အတွက် (အရေးကြီး: Play Store တင်လျှင် အငြင်းခံရနိုင်သည်) -->
  /// <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
  /// <!-- Android 10 နှင့် အောက်အတွက် -->
  /// <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="29" />
  /// ```
  Future<bool> isStoragePermission() async =>
      await _channel.invokeMethod<bool>('$_key/isStoragePermission') ?? false;
  Future<bool> requestStoragePermission() async =>
      await _channel.invokeMethod<bool>('$_key/requestStoragePermission') ??
      false;

  /// ### Package Install Permission (Unknown Sources / APK Install)
  /// `AndroidManifest.xml` တွင် ထည့်ရန်:
  /// ```xml
  /// <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
  /// ```
  Future<bool> isPackageInstallPermission() async =>
      await _channel.invokeMethod<bool>('$_key/isPackageInstallPermission') ??
      false;
  Future<bool> requestPackageInstallPermission() async =>
      await _channel.invokeMethod<bool>(
        '$_key/requestPackageInstallPermission',
      ) ??
      false;
}
