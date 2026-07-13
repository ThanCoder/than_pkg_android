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

class SimpleNotificationHandler : PkgHandler() {

    private val channelId = "simple_notification_channel"
    private val notificationId = 1001
    private var notificationManager: NotificationManager? = null
    private var builder: NotificationCompat.Builder? = null
    private var isChannelCreated = false

    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        // Context မရှိရင် ဘာမှလုပ်လို့မရလို့ safe return အရင်လုပ်မယ်
        if (context == null) {
            result.error("NO_CONTEXT", "Android context is null", null)
            return
        }

        initNotificationManager()

        when (method) {
            "getChannelId" ->{
                result.success(channelId)
            }
            // ၁။ ရိုးရိုး စာသား/အချက်အလက်ပြချင်ရင် (Info Notification)
            "showInfo" -> {
                val title = call.argument<String>("title") ?: "Information"
                val content = call.argument<String>("content") ?: ""
                showInfoNotification(title, content)
                result.success(true)
            }

            // ၂။ ဒေါင်းလုဒ် Progress ဘား စတင်ပြချင်ရင် (Progress Notification)
            "showProgress" -> {
                val title = call.argument<String>("title") ?: "Downloading"
                val content = call.argument<String>("content") ?: "Please wait..."
                showProgressNotification(title, content)
                result.success(true)
            }

            // ၃။ ဒေါင်းလုဒ်ဆွဲနေတုန်း ရာခိုင်နှုန်းလှမ်း update လုပ်ဖို့
            "updateProgress" -> {
                val progress = call.argument<Int>("progress") ?: 0
                val speed = call.argument<String>("speed") ?: ""
                updateProgress(progress, speed)
                result.success(true)
            }

            // ၄။ ဒေါင်းလုဒ်ပြီးသွားလို့ Progress ဘား ဖျောက်ပြီး အောင်မြင်ကြောင်းပြဖို့
            "finishProgress" -> {
                val title = call.argument<String>("title") ?: "Download Complete"
                val content = call.argument<String>("content") ?: "File saved successfully."
                finishProgressNotification(title, content)
                result.success(true)
            }

            // ၅။ လက်ရှိပြနေတဲ့ Noti ကို လုံးဝ ပိတ်ချင်ရင်
            "dismiss" -> {
                notificationManager?.cancel(notificationId)
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
            val name = "App Notifications"
            val descriptionText = "Shows informational alerts and progress status."
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            notificationManager?.createNotificationChannel(channel)
            isChannelCreated = true
        }
    }

    /**
     * အခြေခံတူညီတဲ့ PendingIntent (Click Action) ကို ဆောက်ပေးမယ့် Common Function
     */
    private fun createPendingIntent(ctx: Context): PendingIntent? {
        val intent = ctx.packageManager.getLaunchIntentForPackage(ctx.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(
            ctx, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * [INFO TYPE] ရိုးရိုး စာသားအချက်အလက်ပြသခြင်း (လက်နဲ့ ဆွဲဖျက်လို့ရတယ်)
     */
    private fun showInfoNotification(title: String, content: String) {
        val ctx = context ?: return

        builder = NotificationCompat.Builder(ctx, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(false) // User က လက်နဲ့ ဆွဲဖျက် (Swipe) လို့ရတယ်
            .setContentIntent(createPendingIntent(ctx))
            .setAutoCancel(true) // နှိပ်လိုက်ရင် Noti ဘားထဲက ပျောက်သွားမယ်

        notificationManager?.notify(notificationId, builder!!.build())
    }

    /**
     * [PROGRESS TYPE] ဒေါင်းလုဒ် Progress ဘား စတင်ပြသခြင်း (ဆွဲဖျက်လို့မရအောင် ပိတ်ထားတယ်)
     */
    private fun showProgressNotification(title: String, content: String) {
        val ctx = context ?: return

        builder = NotificationCompat.Builder(ctx, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true) // ဒေါင်းနေတုန်း လက်နဲ့ ဆွဲဖျက်လို့မရအောင် ပိတ်ထားမယ်
            .setContentIntent(createPendingIntent(ctx))
            .setAutoCancel(false)

        builder?.setProgress(100, 0, false)
        notificationManager?.notify(notificationId, builder!!.build())
    }

    /**
     * Progress တန်ဖိုးများကို အမြဲလှမ်း Update ပြုလုပ်ခြင်း
     */
    private fun updateProgress(currentProgress: Int, speedText: String) {
        val mgr = notificationManager ?: return
        val bld = builder ?: return

        bld.setProgress(100, currentProgress, false)
        if (speedText.isNotEmpty()) {
            bld.setContentText("Downloading... $currentProgress% ($speedText)")
        } else {
            bld.setContentText("Downloading... $currentProgress%")
        }

        mgr.notify(notificationId, bld.build())
    }

    /**
     * ဒေါင်းလုဒ်ပြီးဆုံးသွားသောအခါ Progress ဘားဖျောက်၍ ပြောင်းလဲပြသခြင်း
     */
    private fun finishProgressNotification(title: String, content: String) {
        val mgr = notificationManager ?: return
        val bld = builder ?: return

        bld.setContentTitle(title)
            .setContentText(content)
            .setProgress(0, 0, false) // Progress bar ဖျောက်မယ်
            .setOngoing(false)        // User လက်နဲ့ ဆွဲဖျက်လို့ ရသွားစေမယ်
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setAutoCancel(true)

        mgr.notify(notificationId, bld.build())
    }
}