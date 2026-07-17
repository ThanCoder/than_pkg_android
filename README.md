# than_pkg_android

## Usage

```dart
ThanPkgAndroid.getInstance.[whatever you want!]
```

## Selectors

- [x] ➕ Added [`FileSelector`](#file-selector-example)
- [x] ➕ Added [`MediaSelector`](#mediaselector)

## Managers

- [x] ➕ Added [`BatteryManager`](#battery-manger-example)
- [x] ➕ Added [`NativeDownloadManager`](#nativedownload-manager)
- [x] ➕ Added [`NetworkManager`](#networkmanager)

## Handlers

+ [x] ➕ Updated [`TextureHandler`](#texturehandler)

------

+ [x] ➕ Added [`PathHandler`](#path-handler)
+ [x] ➕ Added [`PdfHandler`](#pdf-handler)
+ [x] ➕ Added [`SafeStorage`](#safestorage-handler)
+ [x] ➕ Added [`FlutterUtils`](#flutter-utils)
+ [x] ➕ Added [`AppSettingHandler`](#appsetting-handler)
+ [x] ➕ Added [`BrightnessHandler`](#brightness-handler)
+ [x] ➕ Added [`CameraHandler`](#camera-handler)
+ [x] ➕ Added [`DeviceSensorHandler`](#devicesensor-handler)
+ [x] ➕ Added [`IntentTransferHandler`](#intenttransfer-handler)
+ [x] ➕ Added [`LaunchHandler`](#launch-handler)
+ [x] ➕ Added [`OrientationHandler`](#orientationhandler)
+ [x] ➕ Added [`OsHandler`](#oshandler)
+ [x] ➕ Added [`PermissionHandler`](#permissionhandler)
+ [x] ➕ Added [`PrivacyHandler`](#privacyhandler)
+ [x] ➕ Added [`SimpleNotificationHandler`](#simplenotificationhandler)
+ [x] ➕ Added [`NotificationHandler`](#notificationhandler)
+ [x] ➕ Added [`SoundHandler`](#soundhandler)
+ [x] ➕ Added [`StoragePermissionHandler`](#storagepermissionhandler)
+ [x] ➕ Added [`UriHandler`](#uri-handler-example)
+ [x] ➕ Added [`VideoHandler`](#videohandler)
+ [x] ➕ Added [`WifiHandler`](#wifihandler)

### AndroidManifest.xml

+ [x]  ➕ Added [`All Permission`](#all-permission)
+ [x]  ➕ Added [`Extra XML Props`](#extra-xml-props)


### WifiHandler
```dart
final handler = ThanPkgAndroid.getInstance.wifiHandler;
          
handler.getWifiDetails()
```

### VideoHandler
```dart
final handler = ThanPkgAndroid.getInstance.videoHandler;
          
handler.getDuration(path)
handler.getThumbnail(path)
handler.saveThumbnail(path, savePath)
```

### TextureHandler

```dart
final handler = ThanPkgAndroid.getInstance.textureHandler;
          
handler.createTexture()
handler.getSurfacePointer(textureId, width: width, height: height)
handler.releaseTexture(textureId)



// ⚠️ DEV WARNING: This invokes raw C++ FFI bindings under the hood. 
// Any mismatch between buffer dimensions and surface constraints will trigger 
// a fatal native segmentation fault (SIGSEGV) or GPU driver crash. 
// IF YOU DO NOT UNDERSTAND THE UNDERLYING MEMORY LIFECYCLE, DO NOT DO IT.
NativeTextureManager.instance.androidUpdateTexture(
      surfacePointer,
      videoBuffer,
      width,
      height,
    );


// test color texture
testTextureColor(textureId!);

```

### StoragePermissionHandler
```dart
final handler = ThanPkgAndroid.getInstance.storagePermissionHandler;
          
handler.isStoragePermissionGranted()
handler.requestStoragePermission()
```

### SoundHandler
```dart
final handler = ThanPkgAndroid.getInstance.soundHandler;

handler.getVolume()
handler.setVolume(volume)
```
 
### NotificationHandler
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
```
```dart
/// Check Runtime Permission
 if (!await ThanPkgAndroid.getInstance.permissionHandler
    .isNotificationPermission()) {
  await ThanPkgAndroid.getInstance.permissionHandler
      .requestNotificationPermission();
}

final handler = ThanPkgAndroid.getInstance.notificationHandler;
          
handler.show(id, title: title, content: content)
handler.updateProgress(id, progress)
handler.finish(id, title: title, content: content)
handler.cancel(id)
handler.getChannelId()
```
 
### SimpleNotificationHandler
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
```
```dart
/// Check Runtime Permission
 if (!await ThanPkgAndroid.getInstance.permissionHandler
    .isNotificationPermission()) {
  await ThanPkgAndroid.getInstance.permissionHandler
      .requestNotificationPermission();
}
final handler = ThanPkgAndroid.getInstance.simpleNotificationHandler;
          
handler.showInfo(title, content)
handler.showProgress(title, content)
handler.updateProgress(progress, speed)
handler.finishProgress(title, content)
handler.dismiss(title, content)
handler.getChannelId()
```

### PrivacyHandler
```dart
final handler = ThanPkgAndroid.getInstance.privacyHandler;
          
handler.disableSecure()
handler.enableSecure()
```

### PermissionHandler
```dart
final handler = ThanPkgAndroid.getInstance.permissionHandler;
          
//**************Check**************/
handler.isAudioRecordPermission()
handler.isBatteryOptimizationIgnored()
handler.isBluetoothPermission()
handler.isCameraPermission()
handler.isContactsPermission()
handler.isLocationPermission()
handler.isMediaPermission()
handler.isNotificationPermission()
handler.isPackageInstallPermission()
handler.isPhonePermission()
handler.isStoragePermission()
//**************Request**************/
handler.requestAudioRecordPermission()
handler.requestBatteryOptimizationPermission()
handler.requestBluetoothPermission()
handler.requestCameraPermission()
handler.requestContactsPermission()
handler.requestLocationPermission()
handler.requestMediaPermission()
handler.requestNotificationPermission()
handler.requestPackageInstallPermission()
handler.requestPhonePermission()
handler.requestStoragePermission()
```

### OsHandler
```dart
final handler = ThanPkgAndroid.getInstance.osHandler;

handler.getOsBuildInfo()
handler.keepScreenOn(enabled)
handler.setBrightness(brightness)
handler.showToast(message)
handler.startScreenRecord()
handler.takeScreenshot()
```

### OrientationHandler
```dart
final handler = ThanPkgAndroid.getInstance.orientationHandler;

handler.getOrientation()
handler.setOrientation(mode)
```

### NetworkManager
```dart
final handler = ThanPkgAndroid.getInstance.networkManager;

handler.checkNetworkStatus()
handler.networkStatusStream
```

### NativeDownload Manager
```dart
final handler = ThanPkgAndroid.getInstance.nativeDownloadManager;

handler.enqueue(url, filename: filename)
handler.resume(downloadId)
handler.pause(downloadId)
handler.cancel(downloadId)
```

### MediaSelector
```dart
final handler = ThanPkgAndroid.getInstance.mediaSelector;

handler.fetchAudio()
handler.fetchImages()
handler.fetchVideos()
handler.onMediaChanged
```
### Launch Handler
```dart
final handler = ThanPkgAndroid.getInstance.launchHandler;

handler.launchFile(filePath)
handler.launchUrl(url)
handler.openCustomTab(url)
handler.openExternalBrowser(url)
```
### IntentTransfer Handler
```xml
 <activity android:name=".MainActivity" ...>

<!-- Add Lines -->
    <intent-filter>
        <action android:name="android.intent.action.SEND" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="text/plain" />
    </intent-filter>

</activity>
```
```dart
final handler = ThanPkgAndroid.getInstance.intentTransferHandler;

handler.getInitialData()
handler.receiveDataStream
```
### DeviceSensor Handler
```dart
final handler = ThanPkgAndroid.getInstance.deviceSensorHandler;

handler.getAccelerometerValues()
handler.accelerometerStream;
```
### Camera Handler
```dart

final handler = ThanPkgAndroid.getInstance.cameraHandler;

handler.hasFlashlight()
handler.takePicture()
handler.toggleTorch()
```

### Brightness Handler
```dart
final handler = ThanPkgAndroid.getInstance.brightnessHandler;

handler.getScreenBrightness(volume)
handler.setScreenBrightness(brightness)
```
### AppSetting Handler
```dart
final handler = ThanPkgAndroid.getInstance.appSettingHandler;

handler.openAboutSettings();
handler.openAccessibilitySettings()
handler.openAirplaneModeSettings()
handler.openAppListSettings()
handler.openAppNotificationSettings()
handler.openAppSettings()
handler.openBatterySettings()
handler.openBiometricSettings()
handler.openBluetoothSettings()
handler.openCastSettings()
handler.openDataUsageSettings()
handler.openDateSettings()
handler.openDeveloperSettings()
handler.openDisplaySettings()
handler.openKeyboardSettings()
handler.openLocaleSettings()
handler.openLocationSettings()
handler.openNfcSettings()
handler.openNotificationSettings()
handler.openPhoneInfo()
handler.openSearchSettings()
handler.openSecuritySettings()
handler.openSoundSettings()
handler.openStorageSettings()
handler.openSyncSettings()
handler.openSystemSettings()
handler.openVpnSettings()
handler.openWifiSettings()
```

### Flutter Utils
```dart
final handler = ThanPkgAndroid.getInstance.flutterUtils;
handler.toggleFullscreen(isFullscreen);
```

### SafeStorage Handler
```dart
final handler = ThanPkgAndroid.getInstance.safeStorage;

handler.checkFolderPermission(treeUri);
handler.requestFolderPermission();
handler.createFolder(treeUri: treeUri, folderName: folderName);
handler.deleteItem(targetUri);
handler.listFiles(treeUri);
handler.writeFileData(parentUri: parentUri, fileName: fileName, bytes: bytes);
```


### Pdf Handler
```dart
final handler = ThanPkgAndroid.getInstance.pdfHandler;

handler.saveToThumbnail(pdfPathOrUri: pdfPathOrUri, targetPath: targetPath);
handler.getPage(pdfPathOrUri: pdfPathOrUri);
handler.getPageCount(pdfPathOrUri);
```

### File Selector Example

```dart
// check storage permission
final per = ThanPkgAndroid.getInstance.storagePermissionHandler;
if (!await per.isStoragePermissionGranted()) {
await per.requestStoragePermission();
}

print('ThanDev: Start');
final selector = ThanPkgAndroid.getInstance.fileSelector;

///result android uris
List<String> files = await selector.pickFiles();

//result:
// [content://com.android.fileexplorer.myprovider/external_files/DCIM/Screenshots/Screenshot_2026-07-03-19-30-03-538_than.app.than_player-edit.jpg]
print('ThanDev: $files');

//you nedd to use `uriHandler`
final uriHandler = ThanPkgAndroid.getInstance.uriHandler;

final outPath = ThanPkgAndroid.getInstance.pathHandler
    .getDeviceStoragePath(); // /storage/emulated/0/

await uriHandler.copyContentToFile(files.first, '$outPath/test.png');
print('ThanDev: $outPath/test.png');

ThanPkgAndroid.getInstance.osHandler.showToast('Copied');
```

### Battery Manger Example

```dart
StreamSubscription<ThanBatteryInfo>
sub = ThanPkgAndroid.getInstance.batteryManager.batteryInfoStream.listen((
event,
) {
print('ThanDev: Stream-> $event');

/// ThanBatteryInfo(batteryLevel: 100, isCharging: true, powerSource: USB, health: GOOD, temperature: 33.400001525878906, voltage: 4403, technology: Li-poly)
});

// need to close
sub.cancel();

// one time
ThanPkgAndroid.getInstance.batteryManager.getBatteryInfo();

/// ThanBatteryInfo(batteryLevel: 100, isCharging: true, powerSource: USB, health: GOOD, temperature: 33.400001525878906, voltage: 4403, technology: Li-poly)

```

### Uri Handler Example

- -> `FileSelector`,`MediaSelector`

### I Will Work-> `[content://]` Kotlin Return URI

```dart
final handler = ThanPkgAndroid.getInstance.uriHandler;

await handler.copyContentToFile(
    '[content://com.android.fileexplorer.myprovider/external_files]',
    '[output]',
);
await handler.moveContentToFile(
    '[content://com.android.fileexplorer.myprovider/external_files]',
    '[output]',
);
```

### Path Handler

```dart
final handler = ThanPkgAndroid.getInstance.pathHandler;

await handler.getCachePath();
await handler.getExternalFilesPath();
await handler.getFilesPath();

handler.getDCIMPath();
handler.getDeviceStoragePath();
handler.getDocumentsPath();
handler.getDownloadPath();
handler.getMoviesPath();
handler.getMusicPath();
handler.getPicturesPath();
```

## All Permission

### You Need To Add Your Permission

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Extra XML Props

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.than_pkg_android">

<queries>
        <!-- Web Links -->
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.BROWSABLE" />
            <data android:scheme="https" />
        </intent>
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.BROWSABLE" />
            <data android:scheme="http" />
        </intent>
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <data android:mimeType="*/*" />
        </intent>
    </queries>


    <application>
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.file_provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
    </application>

</manifest>
```
