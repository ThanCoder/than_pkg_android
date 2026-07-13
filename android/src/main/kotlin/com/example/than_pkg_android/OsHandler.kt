package com.example.than_pkg_android

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.createBitmap

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
            // 🌟 ၄။ Screenshot (App UI ကို ဓာတ်ပုံရိုက်ပြီး File Path ပြန်ပေးခြင်း)
            "takeScreenshot" -> {
                takeScreenshotNative(activity!!, result)
            }

            // 🌟 ၅။ System Screen Recorder (ဗီဒီယိုဖမ်းမယ့် Tool) ကို လှမ်းနှိုးခြင်း
            "startScreenRecord" -> {
                try {
                    // Android 11+ မှာ ပါဝင်တဲ့ မွေးရာပါ Screen Record Settings/Tile မျိုးကို Intent နဲ့ နှိုးဖို့ ကြိုးစားတာပါ Bro
                    val intent = Intent("android.settings.SCREEN_RECORD_SELECT")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context!!.startActivity(intent)
                    result.success(true)
                } catch (e: Exception) {
                    // အပေါ်က Intent မပွင့်ရင် Standard Quick Settings Panel ကို ဆွဲချပေးလိုက်တာပါ Bro
                    try {
                        val statusBarIntent = Intent("android.intent.action.MAIN").apply {
                            setClassName(
                                "com.android.systemui",
                                "com.android.systemui.statusbar.phone.StatusBar"
                            )
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context!!.startActivity(statusBarIntent)
                        result.success(true)
                    } catch (ex: Exception) {
                        result.error(
                            "CANNOT_START_RECORDER",
                            "ဖုန်းရဲ့ Screen Recorder ကို လှမ်းနှိုးလို့ မရပါဘူး Bro: ${e.localizedMessage}",
                            null
                        )
                    }
                }
            }

            else -> {
                result.notImplemented()
            }

        }
    }

    // 🌟 Screenshot ရိုက်ပေးမယ့် Native Helper Function
    @RequiresApi(Build.VERSION_CODES.O)
    private fun takeScreenshotNative(activity: Activity, result: MethodChannel.Result) {
        val window: Window = activity.window
        val view = window.decorView

        // မျက်နှာပြင် အရွယ်အစားအတိုင်း Bitmap အလွတ်တစ်ခု ကြိုဆောက်တယ် Bro
        val bitmap = createBitmap(view.width, view.height)

        // Android 8.0+ ရဲ့ PixelCopy က UI Freeze မဖြစ်ဘဲ နောက်ကွယ်ကနေ ဓာတ်ပုံ အမြန်လှမ်းကူးပေးပါတယ်
        val locationOfViewInWindow = IntArray(2)
        view.getLocationInWindow(locationOfViewInWindow)

        try {
            PixelCopy.request(
                window,
                Rect(
                    locationOfViewInWindow[0],
                    locationOfViewInWindow[1],
                    locationOfViewInWindow[0] + view.width,
                    locationOfViewInWindow[1] + view.height
                ),
                bitmap,
                { copyResult ->
                    if (copyResult == PixelCopy.SUCCESS) {
                        // ဓာတ်ပုံရိုက်လို့ အောင်မြင်ရင် App ရဲ့ Cache Folder ထဲမှာ သွားသိမ်းမယ် Bro
                        val cacheDir = activity.cacheDir
                        val screenshotFile =
                            File(cacheDir, "screenshot_${System.currentTimeMillis()}.png")

                        FileOutputStream(screenshotFile).use { out ->
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                            out.flush()
                        }

                        // Flutter ဘက်ကို ရလာတဲ့ ပုံရဲ့ နေရာ (File Path) လှမ်းပြန်ပေးလိုက်တာပါ Bro
                        activity.runOnUiThread {
                            result.success(screenshotFile.absolutePath)
                        }
                    } else {
                        activity.runOnUiThread {
                            result.error(
                                "SCREENSHOT_FAILED",
                                "PixelCopy failed to copy screen",
                                null
                            )
                        }
                    }
                },
                Handler(Looper.getMainLooper())
            )
        } catch (e: Exception) {
            result.error("SCREENSHOT_ERROR", e.localizedMessage, null)
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