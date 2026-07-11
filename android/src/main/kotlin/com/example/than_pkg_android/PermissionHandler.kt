package com.example.than_pkg_android

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.File

class PermissionHandler : PkgHandler() {

    // Request Codes စုံလင်စွာ သတ်မှတ်ခြင်း
    private val REQ_STORAGE_SDK_29 = 101
    private val REQ_STORAGE_SDK_30 = 10001
    private val REQ_LOCATION = 10002
    private val REQ_CAMERA = 10003
    private val REQ_INSTALL_PACKAGES = 10000
    private val REQ_NOTIFICATION = 10004
    private val REQ_AUDIO_RECORD = 10005
    private val REQ_CONTACTS = 10006
    private val REQ_PHONE = 10007
    private val REQ_BLUETOOTH = 10008
    private val REQ_MEDIA_VISUAL = 10009

    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        val act = activity ?: run {
            result.error("NO_ACTIVITY", "Activity is not available", null)
            return
        }
        val ctx = context ?: act

        this.pendingResult = result

        try {
            when (method) {
                // --- Camera ---
                "isCameraPermission" -> result.success(isPermissionGranted(ctx, Manifest.permission.CAMERA))
                "requestCameraPermission" -> requestSinglePermission(act, Manifest.permission.CAMERA, REQ_CAMERA)

                // --- Location ---
                "isLocationPermission" -> result.success(isPermissionGranted(ctx, Manifest.permission.ACCESS_FINE_LOCATION))
                "requestLocationPermission" -> requestSinglePermission(act, Manifest.permission.ACCESS_FINE_LOCATION, REQ_LOCATION)

                // --- Microphone / Audio Record ---
                "isAudioRecordPermission" -> result.success(isPermissionGranted(ctx, Manifest.permission.RECORD_AUDIO))
                "requestAudioRecordPermission" -> requestSinglePermission(act, Manifest.permission.RECORD_AUDIO, REQ_AUDIO_RECORD)

                // --- Contacts ---
                "isContactsPermission" -> result.success(isPermissionGranted(ctx, Manifest.permission.READ_CONTACTS))
                "requestContactsPermission" -> requestSinglePermission(act, Manifest.permission.READ_CONTACTS, REQ_CONTACTS)

                // --- Phone ---
                "isPhonePermission" -> result.success(isPermissionGranted(ctx, Manifest.permission.CALL_PHONE))
                "requestPhonePermission" -> requestSinglePermission(act, Manifest.permission.CALL_PHONE, REQ_PHONE)

                // --- Notification (Android 13+) ---
                "isNotificationPermission" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        result.success(isPermissionGranted(ctx, Manifest.permission.POST_NOTIFICATIONS))
                    } else {
                        result.success(true)
                    }
                }
                "requestNotificationPermission" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestSinglePermission(act, Manifest.permission.POST_NOTIFICATIONS, REQ_NOTIFICATION)
                    } else {
                        result.success(true)
                    }
                }

                // --- Bluetooth (Android 12+ Connect & Scan) ---
                "isBluetoothPermission" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val hasConnect = isPermissionGranted(ctx, Manifest.permission.BLUETOOTH_CONNECT)
                        val hasScan = isPermissionGranted(ctx, Manifest.permission.BLUETOOTH_SCAN)
                        result.success(hasConnect && hasScan)
                    } else {
                        result.success(true)
                    }
                }
                "requestBluetoothPermission" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ActivityCompat.requestPermissions(
                            act,
                            arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                            REQ_BLUETOOTH
                        )
                    } else {
                        result.success(true)
                    }
                }

                // --- Granular Media Permissions (Android 13+ ရုပ်ပုံ/ဗီဒီယို သီးသန့်စီတောင်းရန်) ---
                "isMediaPermission" -> result.success(checkMediaPermission(ctx))
                "requestMediaPermission" -> requestMediaPermission(act)

                // --- Battery Optimization ---
                "isBatteryOptimizationIgnored" -> {
                    val pm = ctx.getSystemService(POWER_SERVICE) as PowerManager
                    result.success(pm.isIgnoringBatteryOptimizations(ctx.packageName))
                }
                "requestBatteryOptimizationPermission" -> {
                    requestBatteryOptimizationPermission(act)
                    result.success(null)
                }

                // --- Full Storage (MANAGE_EXTERNAL_STORAGE) ---
                "isStoragePermission" -> result.success(isStoragePermission(ctx))
                "requestStoragePermission" -> {
                    if (isStoragePermission(ctx)) {
                        result.success(true)
                    } else {
                        requestStoragePermission(act)
                    }
                }

                // --- Package Install ---
                "isPackageInstallPermission" -> result.success(isPackageInstallPermission(act))
                "requestPackageInstallPermission" -> {
                    if (isPackageInstallPermission(act)) {
                        result.success(true)
                    } else {
                        requestPackageInstallPermission(act)
                    }
                }

                else -> result.notImplemented()
            }
        } catch (e: Exception) {
            result.error("${method}_ERROR", e.message, null)
            pendingResult = null
        }
    }

    // --- Core Helper Logics ---

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSinglePermission(activity: Activity, permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            pendingResult?.success(true)
            pendingResult = null
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
        }
    }

    private fun isStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            isPermissionGranted(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun requestStoragePermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.fromParts("package", activity.packageName, null)
                }
                activity.startActivity(intent)
                pendingResult?.success(Environment.isExternalStorageManager())
                pendingResult = null
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                activity.startActivityForResult(intent, REQ_STORAGE_SDK_30)
            }
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQ_STORAGE_SDK_29)
        }
    }

    private fun checkMediaPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isPermissionGranted(context, Manifest.permission.READ_MEDIA_IMAGES) &&
                    isPermissionGranted(context, Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            isPermissionGranted(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun requestMediaPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO),
                REQ_MEDIA_VISUAL
            )
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQ_MEDIA_VISUAL)
        }
    }

    @SuppressLint("BatteryLife")
    private fun requestBatteryOptimizationPermission(activity: Activity) {
        val pm = activity.getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(activity.packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = "package:${activity.packageName}".toUri()
            }
            activity.startActivity(intent)
        }
    }

    private fun isPackageInstallPermission(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    private fun requestPackageInstallPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = "package:${activity.packageName}".toUri()
            }
            activity.startActivityForResult(intent, REQ_INSTALL_PACKAGES)
        }
    }

    // --- Callback တုံ့ပြန်မှုများ ---

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray): Boolean {
        if (pendingResult == null) return false

        // Bluetooth သို့မဟုတ် Media ခွင့်ပြုချက်ဆိုလျှင် parameter အများကြီးပါ၍ loop ပတ်စစ်ဆေးမည်
        val isAllGranted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }

        when (requestCode) {
            REQ_CAMERA, REQ_LOCATION, REQ_STORAGE_SDK_29, REQ_AUDIO_RECORD, REQ_CONTACTS, REQ_PHONE, REQ_NOTIFICATION, REQ_BLUETOOTH, REQ_MEDIA_VISUAL -> {
                pendingResult?.success(isAllGranted)
                pendingResult = null
                return true
            }
        }
        return false
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (pendingResult == null) return false

        when (requestCode) {
            REQ_STORAGE_SDK_30 -> {
                pendingResult?.success(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Environment.isExternalStorageManager() else true)
                pendingResult = null
                return true
            }
            REQ_INSTALL_PACKAGES -> {
                pendingResult?.success(isPackageInstallPermission(activity!!))
                pendingResult = null
                return true
            }
        }
        return false
    }
}