package com.example.than_pkg_android

import android.app.Activity
import android.view.WindowManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class BrightnessHandler : PkgHandler() {
    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        val currentActivity = activity
        if (currentActivity == null) {
            result.error("NO_ACTIVITY", "Activity is not available", null)
            return
        }

        when (method) {
            "setScreenBrightness" -> {
                // Flutter ဘက်ကနေ 0.0 ကနေ 1.0 ကြား တန်ဖိုးတစ်ခု ပို့ပေးရပါမယ်
                val brightness = call.argument<Double>("brightness")?.toFloat()
                if (brightness != null && brightness in 0.0f..1.0f) {
                    currentActivity.runOnUiThread {
                        val lp = currentActivity.window.attributes
                        lp.screenBrightness = brightness
                        currentActivity.window.attributes = lp
                        result.success(true)
                    }
                } else {
                    result.error("INVALID_ARGUMENT", "Brightness must be between 0.0 and 1.0", null)
                }
            }
            "getScreenBrightness" -> {
                currentActivity.runOnUiThread {
                    val lp = currentActivity.window.attributes
                    var brightness = lp.screenBrightness

                    // အကယ်၍ custom မပြင်ရသေးရင် system default အတိုင်း -1.0 ဖြစ်နေတတ်လို့ လက်ရှိ brightness ကို ပြန်ယူပေးတာပါ
                    if (brightness < 0) {
                        try {
                            val systemBrightness = android.provider.Settings.System.getInt(
                                currentActivity.contentResolver,
                                android.provider.Settings.System.SCREEN_BRIGHTNESS
                            )
                            brightness = systemBrightness / 255.0f
                        } catch (e: Exception) {
                            brightness = 0.5f // Fallback
                        }
                    }
                    result.success(brightness.toDouble())
                }
            }
            else -> result.notImplemented()
        }
    }
}