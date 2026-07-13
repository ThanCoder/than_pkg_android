package com.example.than_pkg_android

import android.os.Build
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class OsHandler : PkgHandler() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun handle(
        method: String, call: MethodCall, result: MethodChannel.Result
    ) {
        when (method) {
            "getOsBuildInfo" -> {
                getOsBuildInfo(call, result)
            }
            // ၁။ Toast Message ပြသခြင်း
            "showToast" -> {
                val message = call.argument<String>("message") ?: ""
                val isLong = call.argument<Boolean>("isLong") ?: false
                showToast(message, isLong, result)
            }

            // ၂။ Screen ကို မပိတ်ဘဲ အမြဲလင်းနေအောင် ထိန်းခြင်း (Keep Screen On)
            "keepScreenOn" -> {
                val enabled = call.argument<Boolean>("enabled") ?: true
                keepScreenOn(enabled, result)
            }

            // ၃။ App ရဲ့ Screen Brightness (အလင်းအမှောင်) ကို Native ဘက်က ထိန်းချုပ်ခြင်း
            "setBrightness" -> {
                val brightness = call.argument<Double>("brightness") ?: -1.0
                setBrightness(brightness, result)
            }

            else -> {
                result.notImplemented()
            }

        }
    }

    /**
     * ၁။ Native Toast ပြသပေးမည့် Function
     */
    private fun showToast(message: String, isLong: Boolean, result: MethodChannel.Result) {
        val ctx = context ?: run {
            result.error("NO_CONTEXT", "Android context is null", null)
            return
        }

        val duration = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT

        // Toast က UI ဖြတ်ပိုင်းဖြစ်လို့ UI Thread ပေါ်မှာ တင်ပေးရပါတယ် Bro
        activity?.runOnUiThread {
            Toast.makeText(ctx, message, duration).show()
            result.success(true)
        } ?: run {
            result.error(
                "NO_ACTIVITY", "Activity is null. Toast requires an active activity.", null
            )
        }
    }

    /**
     * ၂။ Screen အမြဲလင်းနေအောင် လုပ်ပေးမည့် Function (True = အမြဲလင်း၊ False = ပုံမှန်အတိုင်းပြန်ဖြစ်)
     */
    private fun keepScreenOn(enabled: Boolean, result: MethodChannel.Result) {
        val act = activity ?: run {
            result.error("NO_ACTIVITY", "Activity is null. Cannot change screen flags.", null)
            return
        }

        act.runOnUiThread {
            if (enabled) {
                act.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                act.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            result.success(true)
        }
    }

    /**
     * ၃။ App အတွင်း Brightness ကို ချိန်ညှိပေးမည့် Function
     * brightness value: 0.0 (မှောင်ဆုံး) မှ 1.0 (လင်းဆုံး)။ -1.0 ကတော့ System default ပါ။
     */
    private fun setBrightness(brightness: Double, result: MethodChannel.Result) {
        val act = activity ?: run {
            result.error("NO_ACTIVITY", "Activity is null. Cannot change brightness.", null)
            return
        }

        act.runOnUiThread {
            val layoutParams = act.window.attributes
            layoutParams.screenBrightness = brightness.toFloat()
            act.window.attributes = layoutParams
            result.success(true)
        }
    }

    private fun getOsBuildInfo(
        call: MethodCall, result: MethodChannel.Result
    ) {
        val info = mutableMapOf<String, Any?>()

        // 🟢 ၁။ အခြေခံ OS Version အချက်အလက်များ (Android ဗားရှင်းတိုင်းမှာ ရနိုင်သည်)
        info["RELEASE"] = Build.VERSION.RELEASE
        info["SDK_INT"] = Build.VERSION.SDK_INT
        info["INCREMENTAL"] = Build.VERSION.INCREMENTAL
        info["CODENAME"] = Build.VERSION.CODENAME

        // 🟢 ၂။ Device Hardware အချက်အလက်များ (Flutter ဘက်က Logic ခွဲရင် အရမ်းအသုံးဝင်သည်)
        info["BRAND"] = Build.BRAND                 // ဥပမာ- xiaomi, samsung
        info["MANUFACTURER"] = Build.MANUFACTURER   // ဥပမာ- Xiaomi
        info["MODEL"] = Build.MODEL                 // ဥပမာ- Redmi Note 14 5G
        info["PRODUCT"] = Build.PRODUCT             // Product နာမည်အစစ်
        info["HARDWARE"] = Build.HARDWARE           // Chipset ကောင် (ဥပမာ- qcom, mt6833)
        info["DEVICE"] = Build.DEVICE               // Board code နာမည်
        info["DISPLAY"] = Build.DISPLAY             // Build ID / Display ROM version
        info["FINGERPRINT"] = Build.FINGERPRINT     // Unique build fingerprint ID

        // 🟢 ၃။ Android 6.0 (M) နှင့် အထက်အတွက်သာ
        info["BASE_OS"] = Build.VERSION.BASE_OS
        info["SECURITY_PATCH"] = Build.VERSION.SECURITY_PATCH

        // 🟢 ၄။ Android 12 (S) နှင့် အထက်အတွက်သာ
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            info["MEDIA_PERFORMANCE_CLASS"] = Build.VERSION.MEDIA_PERFORMANCE_CLASS
        } else {
            info["MEDIA_PERFORMANCE_CLASS"] = 0
        }

        // 🟢 ၅။ Android 13 (Tiramisu) နှင့် အထက်အတွက်သာ
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            info["RELEASE_OR_CODENAME"] = Build.VERSION.RELEASE_OR_CODENAME
            info["RELEASE_OR_PREVIEW_DISPLAY"] = Build.VERSION.RELEASE_OR_PREVIEW_DISPLAY
        } else {
            info["RELEASE_OR_CODENAME"] = Build.VERSION.RELEASE
            info["RELEASE_OR_PREVIEW_DISPLAY"] = Build.VERSION.RELEASE
        }

        // 🟢 ၆။ Android 14 (U) နှင့် အထက်အတွက်သာ (မင်းထည့်ထားတဲ့ SDK_INT_FULL ကြောင့် App မကွဲအောင် ကာကွယ်ခြင်း)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            info["SDK_INT_FULL"] = Build.VERSION.SDK_INT_FULL
        } else {
            info["SDK_INT_FULL"] =
                Build.VERSION.SDK_INT * 100000 // Fallback value အဖြစ် အကြမ်းဖျင်းတွက်ပေးထားခြင်း
        }

        // 🚨 မည်သည့် Android ဗားရှင်းပဲဖြစ်ဖြစ် Map ဒေတာကို သေချာပေါက် Return ပြန်ပေးမည်
        result.success(info)
    }
}