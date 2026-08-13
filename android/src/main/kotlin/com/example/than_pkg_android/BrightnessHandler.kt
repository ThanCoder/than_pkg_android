package com.example.than_pkg_android

import android.provider.Settings
import android.view.WindowManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class BrightnessHandler : PkgHandler() {

    override fun handle(
        method: String,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val currentActivity = activity

        if (currentActivity == null) {
            result.error(
                "NO_ACTIVITY",
                "Activity is not available",
                null
            )
            return
        }

        when (method) {

            // ---------------------------------------------------------
            // Set window brightness
            // ---------------------------------------------------------
            "setScreenBrightness" -> {
                val brightness = call
                    .argument<Double>("brightness")
                    ?.toFloat()

                if (brightness == null || brightness !in 0.0f..1.0f) {
                    result.error(
                        "INVALID_ARGUMENT",
                        "Brightness must be between 0.0 and 1.0",
                        null
                    )
                    return
                }

                currentActivity.runOnUiThread {
                    val lp = currentActivity.window.attributes

                    lp.screenBrightness = brightness

                    currentActivity.window.attributes = lp

                    result.success(true)
                }
            }

            // ---------------------------------------------------------
            // Get current window brightness
            // ---------------------------------------------------------
            "getScreenBrightness" -> {
                currentActivity.runOnUiThread {

                    val brightness =
                        currentActivity.window.attributes.screenBrightness

                    if (brightness >= 0f) {
                        // Window မှာ custom brightness သတ်မှတ်ထားတယ်
                        result.success(brightness.toDouble())
                    } else {
                        // Window က system brightness ကိုသုံးနေတယ်
                        try {
                            val systemBrightness = Settings.System.getInt(
                                currentActivity.contentResolver,
                                Settings.System.SCREEN_BRIGHTNESS
                            )

                            result.success(
                                (systemBrightness / 255.0).coerceIn(0.0, 1.0)
                            )
                        } catch (e: Exception) {
                            result.error(
                                "BRIGHTNESS_ERROR",
                                e.message,
                                null
                            )
                        }
                    }
                }
            }

            // ---------------------------------------------------------
            // Restore system brightness
            // ---------------------------------------------------------
            "restoreScreenBrightness" -> {
                currentActivity.runOnUiThread {

                    val lp = currentActivity.window.attributes

                    lp.screenBrightness =
                        WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE

                    currentActivity.window.attributes = lp

                    result.success(true)
                }
            }

            // ---------------------------------------------------------
            // Check whether window has custom brightness
            // ---------------------------------------------------------
            "isScreenBrightnessOverridden" -> {
                currentActivity.runOnUiThread {

                    val brightness =
                        currentActivity.window.attributes.screenBrightness

                    result.success(brightness >= 0f)
                }
            }

            else -> result.notImplemented()
        }
    }
}