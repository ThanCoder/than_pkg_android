package com.example.than_pkg_android

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.File

class DownloadHandler : PkgHandler() {

    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        when (method) {
            // ၁။ ဒေါင်းလုဒ် စတင်ရန် ခေါ်ဆိုခြင်း
            "enqueue" -> {
                val url = call.argument<String>("url") ?: ""
                val fileName = call.argument<String>("fileName") ?: "downloaded_file"
                val title = call.argument<String>("title") ?: "Downloading file"
                val description = call.argument<String>("description") ?: "Please wait..."

                enqueueDownload(url, fileName, title, description, result)
            }

            // ၂။ ဒေါင်းလုဒ်လုပ်ထားတဲ့ ဖိုင်တစ်ခုကို ပြန်ဖျက်ချင်ရင် (Optional)
            "cancel" -> {
                val downloadId = call.argument<Long>("downloadId") ?: -1L
                cancelDownload(downloadId, result)
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    /**
     * Android Native DownloadManager သို့ ဒေါင်းလုဒ်လုပ်ငန်းစဉ် အပ်နှံခြင်း
     */
    private fun enqueueDownload(
        url: String,
        fileName: String,
        title: String,
        description: String,
        result: MethodChannel.Result
    ) {
        val ctx = context ?: run {
            result.error("NO_CONTEXT", "Android context is null", null)
            return
        }

        if (url.isEmpty()) {
            result.error("INVALID_URL", "Download URL cannot be empty", null)
            return
        }

        try {
            // Android OS ရဲ့ မွေးရာပါ Native Download Service ကို နှိုးခြင်း
            val downloadManager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri = Uri.parse(url)

            val request = DownloadManager.Request(downloadUri).apply {
                // ဖုန်းရဲ့ Public "Download" folder ထဲမှာ စနစ်တကျ သွားသိမ်းပေးမယ်
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

                // Noti မှာ ဒေါင်းနေတုန်းရော၊ ပြီးသွားရင်ပါ (Notification ဧရိယာမှာ) အမြဲပြထားပေးဖို့
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                // Noti ဘားပေါ်မှာ ပြမယ့် ခေါင်းစဉ်နဲ့ အကြောင်းအရာ
                setTitle(title)
                setDescription(description)

                // Mobile Data ရော Wi-Fi ပါ နှစ်မျိုးလုံးနဲ့ ဒေါင်းခွင့်ပြုမယ်
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)

                // Media Scanner ကို ဒီဖိုင်အကြောင်း အသိပေးမယ် (ဥပမာ- သီချင်းဆိုရင် Music player ထဲ တန်းပေါ်အောင်)
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
                    @Suppress("DEPRECATION") allowScanningByMediaScanner()
                }
            }

            // Task ကို တန်းစီဇယား (Queue) ထဲ ထည့်လိုက်ပြီ (Multi-download ပြိုင်တူ ပစ်ထည့်လို့ရတယ်)
            // သူက ID တစ်ခု ပြန်ပေးလိမ့်မယ် (အဲဒီ ID နဲ့ ပြန်ဖျက်တာမျိုး လုပ်လို့ရတယ်)
            val downloadId = downloadManager.enqueue(request)

            result.success(downloadId) // Flutter ဘက်ကို downloadId ပြန်ပေးလိုက်မယ်
        } catch (e: Exception) {
            result.error("DOWNLOAD_FAILED", e.localizedMessage, null)
        }
    }

    /**
     * ဒေါင်းလုဒ်လုပ်ငန်းစဉ်ကို ID အသုံးပြုပြီး ပြန်လည်ပယ်ဖျက်ခြင်း
     */
    private fun cancelDownload(downloadId: Long, result: MethodChannel.Result) {
        val ctx = context ?: run {
            result.error("NO_CONTEXT", "Android context is null", null)
            return
        }

        if (downloadId == -1L) {
            result.error("INVALID_ID", "Invalid download ID", null)
            return
        }

        val downloadManager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        // cancel ခေါ်လိုက်ရင် ဒေါင်းနေတာ ရပ်သွားပြီး ဖိုင်အကြွင်းအကျန်ကိုပါ တစ်ခါတည်း ရှင်းပေးတယ်
        val removedRows = downloadManager.remove(downloadId)

        result.success(removedRows > 0)
    }
}