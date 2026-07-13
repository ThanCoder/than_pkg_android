package com.example.than_pkg_android

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.File

class LaunchHandler: PkgHandler() {
    override fun handle(
        method: String,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        // Context ရှိမရှိ ကြိုစစ်ထားမယ် Bro (Null Pointer Exception မဖြစ်အောင်လို့ပါ)
        val ctx = context ?: run {
            result.error("NO_CONTEXT", "Android context is null", null)
            return
        }

        when(method) {
            // 🌟 ၁။ ဖုန်းရဲ့ ပြင်ပ Browser (သို့မဟုတ်) Website Link များကို ဖွင့်ခြင်း
            "openExternalBrowser", "launchUrl" -> {
                val url = call.argument<String>("url") ?: ""
                if (url.isEmpty()) {
                    result.error("INVALID_URL", "URL is empty", null)
                } else {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        ctx.startActivity(intent)
                        result.success(true)
                    } catch (e: Exception) {
                        result.error("CANNOT_OPEN_BROWSER", e.localizedMessage, null)
                    }
                }
            }

            // 🌟 ၂။ App ထဲမှာတင် Chrome Custom Tab စနစ်သုံးပြီး လင့်မြန်မြန်ဖွင့်ခြင်း
            "openCustomTab" -> {
                val url = call.argument<String>("url") ?: ""
                if (url.isEmpty()) {
                    result.error("INVALID_URL", "URL is empty", null)
                } else {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            putExtra("android.support.customtabs.extra.SESSION", null as String?)
                        }
                        ctx.startActivity(intent)
                        result.success(true)
                    } catch (e: Exception) {
                        result.error("CANNOT_OPEN_CUSTOM_TAB", e.localizedMessage, null)
                    }
                }
            }

            // 🌟 ၃။ ဒေါင်းလုဒ်လုပ်ထားသော File (PDF, APK, Image, etc.) ကို သက်ဆိုင်ရာ App ဖြင့် လှမ်းဖွင့်ခြင်း
            "launchFile" -> {
                val filePath = call.argument<String>("filePath") ?: ""
                if (filePath.isEmpty()) {
                    result.error("INVALID_PATH", "File path is empty", null)
                    return
                }

                try {
                    val file = File(filePath)
                    if (!file.exists()) {
                        result.error("FILE_NOT_FOUND", "File does not exist at: $filePath", null)
                        return
                    }

                    // ဖိုင်အမျိုးအစားအလိုက် MimeType ကို အော်တိုရှာခိုင်းခြင်း (ဥပမာ - application/pdf)
                    val mimeType = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(file.extension.lowercase()) ?: "*/*"

                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Android 11+ အတွက် Permission ပေးခြင်း

                        // Android 7.0 (API 24) အထက်အတွက် Safe FileProvider URI သုံးမယ် Bro
                        val fileUri = FileProvider.getUriForFile(
                            ctx,
                            "${ctx.packageName}.file_provider",
                            file
                        )
                        setDataAndType(fileUri, mimeType)
                    }

                    ctx.startActivity(intent)
                    result.success(true)
                } catch (e: Exception) {
                    result.error("CANNOT_LAUNCH_FILE", e.localizedMessage, null)
                }
            }

            else -> result.notImplemented()
        }
    }
}