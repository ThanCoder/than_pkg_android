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

/**
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"/>
 * <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
 */
class PermissionHandler : PkgHandler() {
    override fun handle(
        method: String,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        if (activity == null) {
            result.error("NO_ACTIVITY", "Activity is not available", null)
            return
        }
        this.pendingResult = result

        when (method) {
            "isCameraPermission" -> {
                try {
                    result.success(isCameraPermission(context!!))
                }catch (e:Error){
                    result.error("isCameraPermission Error",e.message,"")
                }
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    fun isCameraPermission(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED)
    }

    /*
    <uses-feature
    android:name="android.hardware.camera"
    android:required="false" />
    <uses-permission android:name="android.permission.CAMERA"/>
    */
    fun requestCameraPermission(activity: Activity) {
        // Request permissions if not granted
        ActivityCompat.requestPermissions(
            activity, arrayOf(Manifest.permission.CAMERA), 10003
        )
    }

    //location
    fun requestLocationPermission(activity: Activity) {
        // Request permissions if not granted
        ActivityCompat.requestPermissions(
            activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 10002
        )
    }

    fun isLocationPermission(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    @SuppressLint("BatteryLife")
    fun requestBatteryOptimizationPermission(activity: Activity) {
        //<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"/>
        val pm = activity.getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(activity.packageName)) {
            val intent: Intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.setData(("package:${activity.packageName}").toUri())
            activity.startActivity(intent)
        }
    }

    //	storage
    @SuppressLint("NewApi")
    @Throws(Exception::class)
    fun requestStoragePermission(ctx: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 11 and above
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", ctx.packageName, null)
                intent.data = uri
                ctx.startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                ctx.startActivityForResult(intent, 10001)
            }
        } else {
            // Android 6.0 to Android 10 (API 23 to 29)
            ctx.requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 101
            )
        }
    }



    // check && request //<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
    @Throws(Exception::class)
    fun isPackageInstallPermission(ctx: Activity): Boolean {
        var res = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            res = ctx.packageManager.canRequestPackageInstalls()
        }
        return res
    }

    fun requestPackageInstallPermission(ctx: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent =
                Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(("package:" + ctx.packageName).toUri())
            ctx.startActivityForResult(intent, 10000)
            // Request code for later handling
        }
    }

    fun checkCanRequestPackageInstallsPermission(ctx: Activity) {
        if (!isPackageInstallPermission(ctx)) {
            requestPackageInstallPermission(ctx)
        }
    }
}