package com.example.than_pkg_android

import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import android.os.Build
import android.os.StatFs
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.util.Locale

class InfoHandler : PkgHandler() {

    override fun handle(
        method: String,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val context = context

        if (context == null) {
            result.error(
                "CONTEXT_ERROR",
                "Android context is null",
                null
            )
            return
        }

        when (method) {

            // Android OS
            "getAndroidInfo" -> {
                getAndroidInfo(result)
            }

            // Application
            "getAppInfo" -> {
                getAppInfo(context, result)
            }

            // Device hardware
            "getDeviceInfo" -> {
                getDeviceInfo(result)
            }

            // Display
            "getDisplayInfo" -> {
                getDisplayInfo(context, result)
            }

            // Locale / language
            "getLocaleInfo" -> {
                getLocaleInfo(context, result)
            }

            // Battery
            "getBatteryInfo" -> {
                getBatteryInfo(context, result)
            }

            // RAM
            "getMemoryInfo" -> {
                getMemoryInfo(context, result)
            }

            // Storage
            "getStorageInfo" -> {
                getStorageInfo(context, result)
            }

            // Hardware / system features
            "getFeatureInfo" -> {
                getFeatureInfo(context, result)
            }

            else -> {
                result.notImplemented()
            }
        }
    }
}


/* -------------------------------------------------------------------------- */
/* Android Info                                                               */
/* -------------------------------------------------------------------------- */

fun getAndroidInfo(result: MethodChannel.Result) {

    val info = mutableMapOf<String, Any?>(
        "version" to Build.VERSION.RELEASE,
        "sdkInt" to Build.VERSION.SDK_INT,

        "securityPatch" to Build.VERSION.SECURITY_PATCH,
        "codename" to Build.VERSION.CODENAME,
        "incremental" to Build.VERSION.INCREMENTAL,
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        info["releaseOrCodename"] =
            Build.VERSION.RELEASE_OR_CODENAME
    }

    result.success(info)
}


/* -------------------------------------------------------------------------- */
/* App Info                                                                   */
/* -------------------------------------------------------------------------- */

fun getAppInfo(
    context: Context,
    result: MethodChannel.Result
) {
    try {
        val packageManager = context.packageManager
        val packageName = context.packageName

        val info = packageManager.getPackageInfo(
            packageName,
            0
        )

        val appInfo = packageManager.getApplicationInfo(
            packageName,
            0
        )

        val versionCode = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        ) {
            info.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            info.versionCode.toLong()
        }

        val installer = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        ) {
            packageManager
                .getInstallSourceInfo(packageName)
                .installingPackageName
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstallerPackageName(packageName)
        }

        result.success(
            mapOf(
                "packageName" to packageName,

                "appName" to packageManager
                    .getApplicationLabel(appInfo)
                    .toString(),

                "versionName" to info.versionName,
                "versionCode" to versionCode,

                "firstInstallTime" to info.firstInstallTime,
                "lastUpdateTime" to info.lastUpdateTime,

                "sourceDir" to appInfo.sourceDir,
                "publicSourceDir" to appInfo.publicSourceDir,
                "dataDir" to appInfo.dataDir,
                "nativeLibraryDir" to appInfo.nativeLibraryDir,

                "installer" to installer,
            )
        )
    } catch (e: Exception) {
        result.error(
            "APP_INFO_ERROR",
            e.message,
            null
        )
    }
}


/* -------------------------------------------------------------------------- */
/* Device Info                                                                */
/* -------------------------------------------------------------------------- */

fun getDeviceInfo(
    result: MethodChannel.Result
) {

    result.success(
        mapOf(
            "manufacturer" to Build.MANUFACTURER,
            "model" to Build.MODEL,
            "brand" to Build.BRAND,
            "device" to Build.DEVICE,
            "product" to Build.PRODUCT,

            "hardware" to Build.HARDWARE,
            "board" to Build.BOARD,
            "bootloader" to Build.BOOTLOADER,

            "display" to Build.DISPLAY,
            "host" to Build.HOST,
            "buildId" to Build.ID,
            "tags" to Build.TAGS,
            "type" to Build.TYPE,
            "user" to Build.USER,

            "supportedAbis" to Build.SUPPORTED_ABIS.toList(),
            "supported32BitAbis" to
                    Build.SUPPORTED_32_BIT_ABIS.toList(),
            "supported64BitAbis" to
                    Build.SUPPORTED_64_BIT_ABIS.toList(),
        )
    )
}


/* -------------------------------------------------------------------------- */
/* Display Info                                                               */
/* -------------------------------------------------------------------------- */

fun getDisplayInfo(
    context: Context,
    result: MethodChannel.Result
) {
    val metrics = context.resources.displayMetrics

    result.success(
        mapOf(
            "width" to metrics.widthPixels,
            "height" to metrics.heightPixels,

            "density" to metrics.density,
            "densityDpi" to metrics.densityDpi,

            "scaledDensity" to metrics.scaledDensity,

            "xdpi" to metrics.xdpi,
            "ydpi" to metrics.ydpi,
        )
    )
}


/* -------------------------------------------------------------------------- */
/* Locale Info                                                                */
/* -------------------------------------------------------------------------- */

fun getLocaleInfo(
    context: Context,
    result: MethodChannel.Result
) {
    val configuration = context.resources.configuration

    val locale: Locale

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        locale = configuration.locales[0]
    } else {
        @Suppress("DEPRECATION")
        locale = configuration.locale
    }

    result.success(
        mapOf(
            "language" to locale.language,
            "country" to locale.country,
            "region" to locale.country,
            "locale" to locale.toLanguageTag(),
            "displayLanguage" to locale.displayLanguage,
            "displayCountry" to locale.displayCountry,
        )
    )
}


/* -------------------------------------------------------------------------- */
/* Battery Info                                                               */
/* -------------------------------------------------------------------------- */

fun getBatteryInfo(
    context: Context,
    result: MethodChannel.Result
) {
    try {
        val batteryManager =
            context.getSystemService(
                Context.BATTERY_SERVICE
            ) as BatteryManager

        val level = batteryManager.getIntProperty(
            BatteryManager.BATTERY_PROPERTY_CAPACITY
        )

        val charging = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        ) {
            batteryManager.isCharging
        } else {
            false
        }

        result.success(
            mapOf(
                "level" to level,
                "isCharging" to charging,
            )
        )
    } catch (e: Exception) {
        result.error(
            "BATTERY_INFO_ERROR",
            e.message,
            null
        )
    }
}


/* -------------------------------------------------------------------------- */
/* Memory Info                                                                */
/* -------------------------------------------------------------------------- */

fun getMemoryInfo(
    context: Context,
    result: MethodChannel.Result
) {
    try {
        val activityManager =
            context.getSystemService(
                Context.ACTIVITY_SERVICE
            ) as ActivityManager

        val memoryInfo = ActivityManager.MemoryInfo()

        activityManager.getMemoryInfo(memoryInfo)

        val totalMemory = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
        ) {
            memoryInfo.totalMem
        } else {
            0L
        }

        result.success(
            mapOf(
                "totalBytes" to totalMemory,
                "availableBytes" to memoryInfo.availMem,
                "lowMemory" to memoryInfo.lowMemory,
                "thresholdBytes" to memoryInfo.threshold,
            )
        )
    } catch (e: Exception) {
        result.error(
            "MEMORY_INFO_ERROR",
            e.message,
            null
        )
    }
}


/* -------------------------------------------------------------------------- */
/* Storage Info                                                               */
/* -------------------------------------------------------------------------- */

fun getStorageInfo(
    context: Context,
    result: MethodChannel.Result
) {
    try {
        val path = context.filesDir.path
        val statFs = StatFs(path)

        val blockSize = statFs.blockSizeLong
        val totalBlocks = statFs.blockCountLong
        val availableBlocks = statFs.availableBlocksLong
        val freeBlocks = statFs.freeBlocksLong

        result.success(
            mapOf(
                "totalBytes" to
                        totalBlocks * blockSize,

                "availableBytes" to
                        availableBlocks * blockSize,

                "freeBytes" to
                        freeBlocks * blockSize,
            )
        )
    } catch (e: Exception) {
        result.error(
            "STORAGE_INFO_ERROR",
            e.message,
            null
        )
    }
}


/* -------------------------------------------------------------------------- */
/* Feature Info                                                               */
/* -------------------------------------------------------------------------- */

fun getFeatureInfo(
    context: Context,
    result: MethodChannel.Result
) {
    val pm = context.packageManager

    result.success(
        mapOf(
            "camera" to pm.hasSystemFeature(
                "android.hardware.camera"
            ),

            "cameraFront" to pm.hasSystemFeature(
                "android.hardware.camera.front"
            ),

            "bluetooth" to pm.hasSystemFeature(
                "android.hardware.bluetooth"
            ),

            "bluetoothLe" to pm.hasSystemFeature(
                "android.hardware.bluetooth_le"
            ),

            "nfc" to pm.hasSystemFeature(
                "android.hardware.nfc"
            ),

            "gps" to pm.hasSystemFeature(
                "android.hardware.location.gps"
            ),

            "wifi" to pm.hasSystemFeature(
                "android.hardware.wifi"
            ),

            "telephony" to pm.hasSystemFeature(
                "android.hardware.telephony"
            ),

            "fingerprint" to pm.hasSystemFeature(
                "android.hardware.fingerprint"
            ),

            "usbHost" to pm.hasSystemFeature(
                "android.hardware.usb.host"
            ),

            "usbAccessory" to pm.hasSystemFeature(
                "android.hardware.usb.accessory"
            ),

            "sensorAccelerometer" to pm.hasSystemFeature(
                "android.hardware.sensor.accelerometer"
            ),

            "sensorGyroscope" to pm.hasSystemFeature(
                "android.hardware.sensor.gyroscope"
            ),
        )
    )
}