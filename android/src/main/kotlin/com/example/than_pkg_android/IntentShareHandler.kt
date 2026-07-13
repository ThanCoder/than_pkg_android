package com.example.than_pkg_android

import android.app.Activity
import android.content.Intent
import android.net.Uri
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class IntentTransferHandler : PkgHandler(), EventChannel.StreamHandler {

    private var eventSink: EventChannel.EventSink? = null

    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        val currentActivity = activity ?: run {
            result.error("NO_ACTIVITY", "Activity is not available", null)
            return
        }

        when (method) {
            "sendText" -> {
                val text = call.argument<String>("text")
                if (text != null) {
                    sendTextToOtherApp(currentActivity, text)
                    result.success(true)
                } else {
                    result.error("INVALID_ARGUMENT", "Text cannot be null", null)
                }
            }
            // App အသစ်စဖွင့်ချင်း (Cold Start) ဒေတာ ရှိရင် ယူဖို့
            "getInitialData" -> {
                val initialData = handleIntent(currentActivity.intent)
                result.success(initialData)
            }
            else -> result.notImplemented()
        }
    }

    // 🌟 App ပွင့်နေတုန်း အပြင်ကနေ ဒေတာ လှမ်း Share ရင် ဒီကို တန်းရောက်လာမယ် (Stream ဘက်ကို တွန်းပေးမယ်)
    override fun onNewIntent(intent: Intent) {
        val receivedData = handleIntent(intent)
        if (receivedData != null) {
            // Flutter ဘက်က Stream စောင့်နေတဲ့သူဆီ ဒေတာ ချက်ချင်း ပို့ပေးလိုက်တာ Bro
            eventSink?.success(receivedData)
        }
    }

    // --- Intent ထဲက ဒေတာကို စစ်ထုတ်ဖတ်ယူတဲ့ ဘုံ Helper Function ---
    private fun handleIntent(intent: Intent?): Map<String, Any?>? {
        val action = intent?.action
        val type = intent?.type

        if (Intent.ACTION_SEND == action && type != null) {
            if ("text/plain" == type) {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (sharedText != null) {
                    return mapOf("type" to "text", "data" to sharedText)
                }
            } else if (type.startsWith("image/")) {
                val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                if (imageUri != null) {
                    return mapOf("type" to "image", "data" to imageUri.toString())
                }
            }
        }
        return null
    }

    private fun sendTextToOtherApp(activity: Activity, text: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share text via")
        activity.startActivity(shareIntent)
    }

    // --- EventChannel.StreamHandler ရဲ့ မဖြစ်မနေ ပါရမယ့် လုပ်ဆောင်ချက်များ ---
    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        this.eventSink = events
    }

    override fun onCancel(arguments: Any?) {
        this.eventSink = null
    }
}