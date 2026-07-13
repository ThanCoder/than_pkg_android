package com.example.than_pkg_android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class OrientationHandler : PkgHandler() {
    @SuppressLint("WrongConstant")
    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        val currentActivity = activity
        if (currentActivity == null) {
            result.error("NO_ACTIVITY", "Activity is not available", null)
            return
        }

        when (method) {
            "setOrientation" -> {
                // Int လို့ တိုက်ရိုက်မယူဘဲ Number အဖြစ် ယူပြီးမှ toInt() ပြောင်းတာ ပိုစိတ်ချရပါတယ်
                val mode = (call.argument<Any>("mode") as? Number)?.toInt() ?: -1

                currentActivity.runOnUiThread {
                    try {
                        currentActivity.requestedOrientation = mode
                        result.success(true)
                    }catch (e: Error){
                        result.error("ORIENTATION_ERROR", e.localizedMessage, null)
                    }

                }
            }
            "getOrientation" -> {
                // လက်ရှိ Activity ရဲ့ orientation state ကို ပြန်ပေးတာ
                val currentOrientation = currentActivity.requestedOrientation
                result.success(currentOrientation)
            }
            else -> result.notImplemented()
        }
    }
}