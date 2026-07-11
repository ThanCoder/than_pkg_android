package com.example.than_pkg_android

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class StoragePermissionHandler: PkgHandler() {
    companion object {
        const val RC_MANAGE_STORAGE = 10001
        const val RC_WRITE_STORAGE = 101
    }

    override fun handle(
        method: String,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val act = activity ?: run {
            result.error("NO_ACTIVITY", "Activity is not available", null)
            return
        }
        this.pendingResult = result
        when(method){
            "isStoragePermissionGranted"->{
                try {
                    result.success(isStoragePermissionGranted(act))
                }catch (e: Error){
                    result.error("[StoragePermissionHandler:isStoragePermissionGranted]",e.message,null)
                }
            }
            "requestStoragePermission" ->{
                // --- Android 11 (API 30) နှင့် အထက် ---
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // အကယ်၍ permission ရပြီးသားဆိုရင် တန်းပြီး true ပြန်ပေးမယ်
                    if (android.os.Environment.isExternalStorageManager()) {
                        pendingResult?.success(true)
                        pendingResult = null
                        return
                    }

                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                            data = Uri.fromParts("package", act.packageName, null)
                        }
                        // startActivityForResult နဲ့ ခေါ်မှ onActivityResult ထဲ ပြန်ဝင်မှာ ဖြစ်ပါတယ်
                        act.startActivityForResult(intent, RC_MANAGE_STORAGE)
                    } catch (e: Exception) {
                        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        act.startActivityForResult(intent, RC_MANAGE_STORAGE)
                    }
                }
                // --- Android 6.0 မှ Android 10 အထိ ---
                else {
                    // အကယ်၍ permission ရပြီးသားဆိုရင် တန်းပြီး true ပြန်ပေးမယ်
                    if (act.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        pendingResult?.success(true)
                        pendingResult = null
                        return
                    }

                    act.requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        RC_WRITE_STORAGE
                    )
                }
            }
            else ->{
                result.notImplemented()
            }
        }
    }

    //check permission
    @SuppressLint("NewApi")
    fun isStoragePermissionGranted(ctx: Activity): Boolean {
        var granted = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 11 and above
            if (Environment.isExternalStorageManager()) {
                granted = true
            }
        } else {
            // Android 6.0 to Android 10 (API 23 to 29)
            if (ctx.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                granted = true
            }
        }
        return granted
    }


    // Android 11+ က Setting ထဲက ပြန်ထွက်လာရင် ဒီကိုရောက်မယ်
    @RequiresApi(Build.VERSION_CODES.R)
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == RC_MANAGE_STORAGE) {
            // Setting က ပြန်လာရင် တကယ် ပေးခဲ့လားဆိုတာ ပြန်စစ်ရမယ်
            val isGranted = android.os.Environment.isExternalStorageManager()
            pendingResult?.success(isGranted)
            pendingResult = null
            return true
        }
        return false
    }

    // Android 6.0 ~ 10 က Dialog မှာ အိုကေ/မအိုကေ နှိပ်ရင် ဒီကိုရောက်မယ်
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String?>, grantResults: IntArray): Boolean {
        if (requestCode == RC_WRITE_STORAGE) {
            val isGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            pendingResult?.success(isGranted)
            pendingResult = null
            return true
        }
        return false
    }
}