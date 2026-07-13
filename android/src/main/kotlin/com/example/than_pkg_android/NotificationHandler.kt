package com.example.than_pkg_android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.util.concurrent.ConcurrentHashMap

class NotificationHandler : PkgHandler() {

    private val channelId = "than_pkg_download_channel"
    private var notificationManager: NotificationManager? = null

    // 🌟 Multi-download တွေရဲ့ Noti Builder တွေကို ID အလိုက် မှတ်ထားဖို့ Map သုံးမယ်
    // Thread-safe ဖြစ်အောင် ConcurrentHashMap ကို သုံးထားပါတယ် Bro
    private val activeNotifications = ConcurrentHashMap<Int, NotificationCompat.Builder>()

    private var isChannelCreated = false

    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        initNotificationManager()

        // Multi-download အတွက် Flutter ဘက်ကနေ ID တစ်ခု မဖြစ်မနေ ပေးခိုင်းရပါမယ်
        val id = call.argument<Int>("id")
        if (id == null) {
            result.error("INVALID_ARGUMENT", "Notification 'id' is required for multi-download support.", null)
            return
        }

        when (method) {
            "getChannelId" ->{
                result.success(channelId)
            }
            // ၁။ ဒေါင်းလုဒ်အသစ်တစ်ခုအတွက် Noti အသစ်ဆောက်မယ်
            "show" -> {
                val title = call.argument<String>("title") ?: "Downloading..."
                val content = call.argument<String>("content") ?: "Please wait..."
                showDownloadNotification(id, title, content)
                result.success(true)
            }

            // ၂။ သက်ဆိုင်ရာ ID အလိုက် Progress ကို လှမ်း Update လုပ်မယ်
            "updateProgress" -> {
                val progress = call.argument<Int>("progress") ?: 0
                val speed = call.argument<String>("speed") ?: ""
                updateProgress(id, progress, speed)
                result.success(true)
            }

            // ၃။ ဒေါင်းလုဒ်တစ်ခု ပြီးသွားရင် အဲဒီ ID Noti ကို ပိတ်/အမှန်ခြစ်ပြမယ်
            "finish" -> {
                val title = call.argument<String>("title") ?: "Download Complete"
                val content = call.argument<String>("content") ?: "File saved."
                finishNotification(id, title, content)
                result.success(true)
            }

            // ၄။ အကယ်၍ ဒေါင်းလုဒ်ကို ဖျက်လိုက်ရင် Noti ပါ တစ်ခါတည်း သတ်ပစ်ဖို့ (Cancel)
            "cancel" -> {
                cancelNotification(id)
                result.success(true)
            }
            else -> result.notImplemented()
        }
    }

    private fun initNotificationManager() {
        val ctx = context ?: return
        if (notificationManager == null) {
            notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        if (!isChannelCreated && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Download Management"
            val descriptionText = "Shows active background download progress bars."
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            notificationManager?.createNotificationChannel(channel)
            isChannelCreated = true
        }
    }

    private fun showDownloadNotification(id: Int, title: String, content: String) {
        val ctx = context ?: return

        val intent = ctx.packageManager.getLaunchIntentForPackage(ctx.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            ctx,
            id, // 🌟 RequestCode ကို ID ပေးလိုက်မှ Noti တစ်ခုချင်းစီ ခွဲပွင့်မှာပါ
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(ctx, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)

        builder.setProgress(100, 0, false)

        // 🌟 Map ထဲမှာ ID အလိုက် ဒီ Builder ကြီးကို သိမ်းထားလိုက်မယ်
        activeNotifications[id] = builder

        notificationManager?.notify(id, builder.build())
    }

    private fun updateProgress(id: Int, currentProgress: Int, speedText: String) {
        val mgr = notificationManager ?: return
        // 🌟 Map ထဲကနေ သက်ဆိုင်ရာ ID ရဲ့ Builder ကို ဆွဲထုတ်ပြီး ပြင်တယ်
        val bld = activeNotifications[id] ?: return

        bld.setProgress(100, currentProgress, false)
        if (speedText.isNotEmpty()) {
            bld.setContentText("$currentProgress% completed ($speedText)")
        } else {
            bld.setContentText("$currentProgress% completed")
        }

        mgr.notify(id, bld.build())
    }

    private fun finishNotification(id: Int, title: String, content: String) {
        val mgr = notificationManager ?: return
        val bld = activeNotifications[id] ?: return

        bld.setContentTitle(title)
            .setContentText(content)
            .setProgress(0, 0, false)
            .setOngoing(false)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setAutoCancel(true)

        mgr.notify(id, bld.build())

        // 🌟 အလုပ်ပြီးသွားပြီမို့လို့ Memory ထဲက Map ထဲကနေ ပြန်ဖျက်ထုတ်ပစ်မယ်
        activeNotifications.remove(id)
    }

    private fun cancelNotification(id: Int) {
        // Noti တန်းဖျက်ပစ်မယ်
        notificationManager?.cancel(id)
        activeNotifications.remove(id)
    }
}