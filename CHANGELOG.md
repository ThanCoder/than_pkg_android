# 0.6.1 
* Fixed `InfoHandler` -> `versionName(int)` to `versionName(String)`
* Added `BrightnessHandler` -> `restoreScreenBrightness`, `isScreenBrightnessOverridden`
# 0.5.0 
* Added `InfoHandler` -> `versionName`
# 0.4.0
* Added `AudioInfoHandler`
* Added `AudioFileHandler`
* Changed `AndroidPathHandler`
* Changed `AndroidSafeStorage` -> `Dart Sources`,      `Kotlin Sources`
* Some Changed `AndroidPathHandler`

## 0.3.0

* Fixed Kotlin -> `LaunchHandler`
* Added `InfoHandler`  
    - AndroidInfo → OS information `version`  `sdkInt`  `securityPatch`  `incremental`  `releaseOrCodename` 

    - AppInfo → app information `packageName` `appName` `versionCode` `firstInstallTime` `lastUpdateTime` `sourceDir` `publicSourceDir` `dataDir` `nativeLibraryDir` `installer` 
    - DeviceInfo → hardware/build information`manufacturer` `model` `brand` `device` `product` `hardware` `board` `bootloader` `display` `host` `buildId` `tags` `type` `user` `supportedAbis` `supported32BitAbis` `supported64BitAbis` 

    - DisplayInfo → screen information `width` `height` `density` `densityDpi` `scaledDensity` `xdpi` `ydpi` 

    - LocaleInfo → language/region `language` `country` `locale` `displayLanguage` `displayCountry` 

    - BatteryInfo → battery `level` `isCharging` 

    - MemoryInfo → RAM `totalBytes` `availableBytes` `lowMemory` `thresholdBytes` 

    - StorageInfo → storage `totalBytes` `availableBytes` `freeBytes` 

    - FeatureInfo → hardware capabilities `camera` `cameraFront` `bluetooth` `bluetoothLe` `nfc` `gps` `wifi` `telephony` `fingerprint` `usbHost` `usbAccessory` `sensorAccelerometer` `sensorGyroscope` 

## 0.2.1

* Fixed `TextureHandler`
* Added `Texture Test Func` -> Go `README.md` Example

## 0.1.0

### Manager

* Added `BatteryManager`
* Added `NativeDownloadManager`
* Added `NetworkManager`

### Handlers

* Added `AppSettingHandler`
* Added `DeviceSensorHandler`
* Added `LaunchHandler`
* Added `OsHandler`
* Added `PrivacyHandler`

## 0.0.1

* `os = AndroidOs(_channel);`
* `wifiHandler = WifiHandler(_channel);`
* `cameraHandler = CameraHandler(_channel);`
* `fileSelector = FileSelector(_channel);`
* `uriHandler = UriHandler(_channel);`
* `storagePermissionHandler = StoragePermissionHandler(_channel);`
* `permissionHandler = PermissionHandler(_channel);`
* `pdfHandler = AndroidPdfHandler(_channel);`
* `pathHandler = AndroidPathHandler(_channel);`
* `safeStorage = AndroidSafeStorage(_channel);`
* `videoHandler = VideoHandler(_channel);`
* `mediaSelector = MediaSelector(_channel);`
* `final flutterUtils = FlutterUtils();`
