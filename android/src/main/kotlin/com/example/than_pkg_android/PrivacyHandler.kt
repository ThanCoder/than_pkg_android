package com.example.than_pkg_android

import android.view.WindowManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class PrivacyHandler : PkgHandler() {

    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        val currentActivity = activity
        if (currentActivity == null) {
            result.error("NO_ACTIVITY", "Activity သို့ မချိတ်ဆက်ထားပါ သို့မဟုတ် null ဖြစ်နေပါသည်", null)
            return
        }

        when (method) {
            "enableSecure" -> {
                // WindowManager သုံးပြီး Screenshot နဲ့ Screen Record ဖမ်းတာကို ပိတ်မယ်
                currentActivity.runOnUiThread {
                    currentActivity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    result.success(true)
                }
            }
            "disableSecure" -> {
                // Flag ကို ပြန်ဖြုတ်ပြီး ပုံမှန်အတိုင်း Screenshot ရိုက်လို့ရအောင် ပြန်လုပ်မယ်
                currentActivity.runOnUiThread {
                    currentActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    result.success(true)
                }
            }
            else -> {
                result.notImplemented()
            }
        }
    }
}