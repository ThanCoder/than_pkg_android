package com.example.than_pkg_android

import android.content.Context
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class StorageHandler : PkgHandler() {
    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        val ctx = context ?: run {
            result.error("NO_CONTEXT", "Context is not available", null)
            return
        }

        when (method) {
            // App ရဲ့ Cache Folder Path ကို ယူမယ် (ယာယီဖိုင်တွေ သိမ်းဖို့ အကောင်းဆုံး)
            // Path ပုံစံ - /data/user/0/com.example.app/cache
            "getCachePath" -> {
                result.success(ctx.cacheDir.absolutePath)
            }

            // App ရဲ့ Files Folder Path ကို ယူမယ် (ဖိုင်တွေကို အမြဲတမ်း သိမ်းထားဖို့)
            // Path ပုံစံ - /data/user/0/com.example.app/files
            "getFilesPath" -> {
                result.success(ctx.filesDir.absolutePath)
            }

            // External Storage (SD Card သို့မဟုတ် ဖုန်း internal storage ထဲက App area)
            // Path ပုံစံ - /storage/emulated/0/Android/data/com.example.app/files
            "getExternalFilesPath" -> {
                val type = call.argument<String>("type") // ဥပမာ- "Pictures", "Downloads"
                val file = ctx.getExternalFilesDir(type)
                if (file != null) {
                    result.success(file.absolutePath)
                } else {
                    result.error("STORAGE_UNAVAILABLE", "External storage is not available", null)
                }
            }

            else -> result.notImplemented()
        }
    }
}