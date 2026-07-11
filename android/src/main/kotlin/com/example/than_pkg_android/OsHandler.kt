package com.example.than_pkg_android

import android.os.Build
import androidx.annotation.RequiresApi
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class OsHandler : PkgHandler() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun handle(
        method: String,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        when(method){
            "getOsBuildInfo" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.success(mapOf(
                        "RELEASE" to Build.VERSION.RELEASE,
                        "SDK" to Build.VERSION.SDK,
                        "RELEASE_OR_CODENAME" to Build.VERSION.RELEASE_OR_CODENAME,
                        "BASE_OS" to Build.VERSION.BASE_OS,
                        "CODENAME" to Build.VERSION.CODENAME,
                        "INCREMENTAL" to Build.VERSION.INCREMENTAL,
                        "RELEASE_OR_PREVIEW_DISPLAY" to Build.VERSION.RELEASE_OR_PREVIEW_DISPLAY,
                        "SECURITY_PATCH" to Build.VERSION.SECURITY_PATCH,
                        "SDK_INT" to Build.VERSION.SDK_INT,
                        "MEDIA_PERFORMANCE_CLASS" to Build.VERSION.MEDIA_PERFORMANCE_CLASS,
                        "SDK_INT_FULL" to Build.VERSION.SDK_INT_FULL,
                    ))
                }
            }
             else -> {
                result.notImplemented()
            }

        }
    }

}