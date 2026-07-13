# than_pkg_android

## Usage

```dart
ThanPkgAndroid.getInstance.[whatever you want!]
```

## Selectors

- [x] ➕ Added [`FileSelector`](#file-selector-example)
- [x] ➕ Added `MediaSelector`

## Managers

- [x] ➕ Added [`BatteryManager`](#battery-manger-example)
- [x] ➕ Added `NativeDownloadManager`
- [x] ➕ Added `NetworkManager`

## Handlers

- [x] ➕ Added [`PathHandler`](#path-example)
- [x] ➕ Added `PdfHandler`
- [x] ➕ Added `SafeStorage`
- [x] ➕ Added `AndroidUtils`
- [x] ➕ Added `AppSettingHandler`
- [x] ➕ Added `BrightnessHandler`
- [x] ➕ Added `CameraHandler`
- [x] ➕ Added `DeviceSensorHandler`
- [x] ➕ Added `IntentTransferHandler`
- [x] ➕ Added `LaunchHandler`
- [x] ➕ Added `OrientationHandler`
- [x] ➕ Added `OsHandler`
- [x] ➕ Added `PermissionHandler`
- [x] ➕ Added `PrivacyHandler`
- [x] ➕ Added `SimpleNotificationHandler`
- [x] ➕ Added `NotificationHandler`
- [x] ➕ Added `SoundHandler`
- [x] ➕ Added `StoragePermissionHandler`
- [x] ➕ Added `TextureHandler`
- [x] ➕ Added [`UriHandler`](#uri-handler-example)
- [x] ➕ Added `VideoHandler`
- [x] ➕ Added `WifiHandler`

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

### Path Example

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

## Already Added (You Don't Need To Add)

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

        <!-- Files 🌟 (ဒီနေရာကို ပြင်လိုက်တာပါ Bro) -->
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <!-- <type> အစား အောက်ကအတိုင်း <data> သုံးပေးရပါတယ် -->
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

```

```
