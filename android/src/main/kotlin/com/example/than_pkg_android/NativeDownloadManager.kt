package com.example.than_pkg_android

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import androidx.core.net.toUri
import java.util.concurrent.Executors

class NativeDownloadManager : PkgHandler() {

    // Background မှာ အလုပ်လုပ်ဖို့ Thread Pool တစ်ခု ဆောက်ထားလိုက်မယ်
    private val executor = Executors.newSingleThreadExecutor()
    // Flutter ကို result ပြန်ရင် Main Thread ပေါ်ကပြန်ဖို့ Handler ဆောက်မယ်
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        // Background Thread ပေါ်မှာ အလုပ်လုပ်ခိုင်းလိုက်မယ် Bro
        executor.execute {
            when (method) {
                "enqueue" -> {
                    val url = call.argument<String>("url") ?: ""
                    val fileName = call.argument<String>("fileName") ?: "downloaded_file"
                    val title = call.argument<String>("title") ?: "Downloading file"
                    val description = call.argument<String>("description") ?: "Please wait..."

                    enqueueDownload(url, fileName, title, description, result)
                }

                "cancel" -> {
                    val downloadId = call.argument<Long>("downloadId") ?: -1L
                    cancelDownload(downloadId, result)
                }

                // 🌟 အသစ်တိုးလာမယ့် ကောင်များ
                "pause" -> {
                    val downloadId = call.argument<Long>("downloadId") ?: -1L
                    pauseDownload(downloadId, result)
                }
                "resume" -> {
                    val downloadId = call.argument<Long>("downloadId") ?: -1L
                    resumeDownload(downloadId, result)
                }

                else -> {
                    // MethodChannel ရဲ့ result တွေကို Main Thread ပေါ်ကပဲ ပြန်ပေးရပါမယ်
                    mainHandler.post { result.notImplemented() }
                }
            }
        }
    }

    private fun enqueueDownload(
        url: String,
        fileName: String,
        title: String,
        description: String,
        result: MethodChannel.Result
    ) {
        val ctx = context ?: run {
            mainHandler.post { result.error("NO_CONTEXT", "Android context is null", null) }
            return
        }

        if (url.isEmpty()) {
            mainHandler.post { result.error("INVALID_URL", "Download URL cannot be empty", null) }
            return
        }

        try {
            val downloadManager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri = url.toUri()

            val request = DownloadManager.Request(downloadUri).apply {
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setTitle(title)
                setDescription(description)
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
                    @Suppress("DEPRECATION") allowScanningByMediaScanner()
                }
            }

            val downloadId = downloadManager.enqueue(request)

            // 🌟 ရလဒ်ကို Main Thread ကနေ Flutter ဆီ ပြန်ပို့ပေးမယ်
            mainHandler.post { result.success(downloadId) }
        } catch (e: Exception) {
            mainHandler.post { result.error("DOWNLOAD_FAILED", e.localizedMessage, null) }
        }
    }

    private fun cancelDownload(downloadId: Long, result: MethodChannel.Result) {
        val ctx = context ?: run {
            mainHandler.post { result.error("NO_CONTEXT", "Android context is null", null) }
            return
        }

        if (downloadId == -1L) {
            mainHandler.post { result.error("INVALID_ID", "Invalid download ID", null) }
            return
        }

        val downloadManager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val removedRows = downloadManager.remove(downloadId)

        // 🌟 ရလဒ်ကို Main Thread ကနေ Flutter ဆီ ပြန်ပို့ပေးမယ်
        mainHandler.post { result.success(removedRows > 0) }
    }

    /**
     * ဒေါင်းလုဒ်ကို ခေတ္တရပ်ဆိုင်းခြင်း (Pause)
     */
    private fun pauseDownload(downloadId: Long, result: MethodChannel.Result) {
        val ctx = context ?: run {
            mainHandler.post { result.error("NO_CONTEXT", "Android context is null", null) }
            return
        }

        if (downloadId == -1L) {
            mainHandler.post { result.error("INVALID_ID", "Invalid download ID", null) }
            return
        }

        try {
            val downloadManager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            // Android OS ရဲ့ Hidden ဖြစ်နေတဲ့ pauseDownload(id) API ကို Reflection သုံးပြီး လှမ်းခေါ်တာပါ Bro
            val pauseMethod = downloadManager.javaClass.getMethod("pauseDownload", Long::class.javaPrimitiveType)
            val updatedRows = pauseMethod.invoke(downloadManager, downloadId) as Int

            mainHandler.post { result.success(updatedRows > 0) }
        } catch (e: Exception) {
            // အကယ်၍ Reflection က အလုပ်မလုပ်ရင် Network Type ကို 0 ပြောင်းတဲ့ Back-up Plan သုံးမယ်
            try {
                // ဒီနည်းလမ်းက Android 11 နဲ့ အောက်မှာ အလုပ်လုပ်ပါတယ်
                mainHandler.post { result.error("PAUSE_FAILED", "Pause non-supported or failed: ${e.localizedMessage}", null) }
            } catch (ex: Exception) {
                mainHandler.post { result.error("PAUSE_FAILED", ex.localizedMessage, null) }
            }
        }
    }

    /**
     * ဒေါင်းလုဒ်ကို ပြန်လည်စတင်ခြင်း (Resume)
     */
    private fun resumeDownload(downloadId: Long, result: MethodChannel.Result) {
        val ctx = context ?: run {
            mainHandler.post { result.error("NO_CONTEXT", "Android context is null", null) }
            return
        }

        if (downloadId == -1L) {
            mainHandler.post { result.error("INVALID_ID", "Invalid download ID", null) }
            return
        }

        try {
            val downloadManager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            // Android OS ရဲ့ Hidden ဖြစ်နေတဲ့ resumeDownload(id) API ကို Reflection သုံးပြီး ခေါ်တာပါ
            val resumeMethod = downloadManager.javaClass.getMethod("resumeDownload", Long::class.javaPrimitiveType)
            val updatedRows = resumeMethod.invoke(downloadManager, downloadId) as Int

            mainHandler.post { result.success(updatedRows > 0) }
        } catch (e: Exception) {
            mainHandler.post { result.error("RESUME_FAILED", e.localizedMessage, null) }
        }
    }

}