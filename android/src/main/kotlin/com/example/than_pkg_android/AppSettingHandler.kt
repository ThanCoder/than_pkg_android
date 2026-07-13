package com.example.than_pkg_android

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import androidx.core.net.toUri

class AppSettingHandler : PkgHandler() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        val ctx = context ?: run {
            result.error("NO_CONTEXT", "Android context is null", null)
            return
        }

        val currentActivity = activity

        when (method) {
            // --- ပင်မ နှင့် App ဆိုင်ရာ Settings ---
            "openSystemSettings" -> openIntent(Intent(Settings.ACTION_SETTINGS), currentActivity, ctx, result)
            "openAppSettings" -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", ctx.packageName, null)
                }
                openIntent(intent, currentActivity, ctx, result)
            }
            "openPhoneInfo" -> openPhoneInfoPage(currentActivity, ctx, result)

            // --- ဟာ့ဒ်ဝဲ နှင့် စနစ်ပိုင်းဆိုင်ရာ Settings ---
            "openStorageSettings" -> openIntent(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS), currentActivity, ctx, result)
            "openBatterySettings" -> openIntent(Intent(Intent.ACTION_POWER_USAGE_SUMMARY), currentActivity, ctx, result)
            "openAppListSettings" -> openIntent(Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS), currentActivity, ctx, result)
            "openDeveloperSettings" -> openIntent(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS), currentActivity, ctx, result)
            "openDisplaySettings" -> openIntent(Intent(Settings.ACTION_DISPLAY_SETTINGS), currentActivity, ctx, result)
            "openAccessibilitySettings" -> openIntent(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), currentActivity, ctx, result)

            // 🌟 ထပ်မံဖြည့်စွက်ထားသော Android Settings မျာား အားလုံး 🌟

            // --- ကွန်ရက် နှင့် ချိတ်ဆက်မှု ဆိုင်ရာ (Network & Connectivity) ---
            "openWifiSettings" -> openIntent(Intent(Settings.ACTION_WIFI_SETTINGS), currentActivity, ctx, result)
            "openBluetoothSettings" -> openIntent(Intent(Settings.ACTION_BLUETOOTH_SETTINGS), currentActivity, ctx, result)
            "openDataUsageSettings" -> openIntent(Intent(Settings.ACTION_DATA_USAGE_SETTINGS), currentActivity, ctx, result)
            "openAirplaneModeSettings" -> openIntent(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS), currentActivity, ctx, result)
            "openVpnSettings" -> openIntent(Intent(Settings.ACTION_VPN_SETTINGS), currentActivity, ctx, result)
            "openCastSettings" -> openIntent(Intent(Settings.ACTION_CAST_SETTINGS), currentActivity, ctx, result)
            "openNfcSettings" -> openIntent(Intent(Settings.ACTION_NFC_SETTINGS), currentActivity, ctx, result)

            // --- လုံခြုံရေး နှင့် တည်နေရာ ဆိုင်ရာ (Security & Location) ---
            "openLocationSettings" -> openIntent(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), currentActivity, ctx, result)
            "openSecuritySettings" -> openIntent(Intent(Settings.ACTION_SECURITY_SETTINGS), currentActivity, ctx, result)
            "openBiometricSettings" -> {
                // လက်ဗွေ/မျက်နှာ သတ်မှတ်တဲ့ နေရာ (Android 10+)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    openIntent(Intent(Settings.ACTION_BIOMETRIC_ENROLL), currentActivity, ctx, result)
                } else {
                    openIntent(Intent(Settings.ACTION_SECURITY_SETTINGS), currentActivity, ctx, result)
                }
            }

            // --- အသံ နှင့် အသိပေးချက် ဆိုင်ရာ (Sound & Notification) ---
            "openSoundSettings" -> openIntent(Intent(Settings.ACTION_SOUND_SETTINGS), currentActivity, ctx, result)
            "openNotificationSettings" -> openIntent(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS), currentActivity, ctx, result)
            "openAppNotificationSettings" -> {
                // ကိုယ့် App ရဲ့ Notification အပိတ်အဖွင့် Page သို့ တိုက်ရိုက်သွားရန် (Android 8.0+)
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, ctx.packageName)
                }
                openIntent(intent, currentActivity, ctx, result)
            }

            // --- နေ့ရက်၊ အချိန် နှင့် ဘာသာစကား (Date, Time & Language) ---
            "openDateSettings" -> openIntent(Intent(Settings.ACTION_DATE_SETTINGS), currentActivity, ctx, result)
            "openLocaleSettings" -> openIntent(Intent(Settings.ACTION_LOCALE_SETTINGS), currentActivity, ctx, result)
            "openKeyboardSettings" -> openIntent(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), currentActivity, ctx, result)

            // --- အခြား အထွေထွေ Settings (Others) ---
            "openAboutSettings" -> openIntent(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS), currentActivity, ctx, result)
            "openSyncSettings" -> openIntent(Intent(Settings.ACTION_SYNC_SETTINGS), currentActivity, ctx, result)
            "openSearchSettings" -> openIntent(Intent(Settings.ACTION_SEARCH_SETTINGS), currentActivity, ctx, result)

            else -> {
                result.notImplemented()
            }
        }
    }

    private fun openIntent(intent: Intent, currentActivity: android.app.Activity?, ctx: Context, result: MethodChannel.Result) {
        try {
            // 🌟 Activity ရှိရှိမရှိရှိ Intent တိုင်းကို FLAG_ACTIVITY_NEW_TASK အမြဲထည့်ပေးလိုက်မယ် Bro
            // ဒါဆိုရင် Context ပြဿနာကြောင့် Crash ဖြစ်တာ/Error တက်တာ လုံးဝ မရှိတော့ပါဘူး
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            ctx.startActivity(intent)
            result.success(true)
        } catch (e: Exception) {
            result.error("CANNOT_OPEN", e.localizedMessage, null)
        }
    }
    private fun openPhoneInfoPage(currentActivity: android.app.Activity?, ctx: Context, result: MethodChannel.Result) {
        val intentsToTry = listOf(
            Intent(Intent.ACTION_MAIN).apply { component = ComponentName("com.android.settings", "com.android.settings.RadioInfo") },
            Intent("android.intent.action.MAIN").apply { setClassName("com.android.settings", "com.android.settings.TestingSettings") },
            Intent("android.intent.action.MAIN").apply { action = "com.android.settings.settings.TESTING_SETTINGS" },
            Intent().apply { setClassName("com.android.settings", "com.android.settings.Settings\$TestingSettingsActivity") },
            Intent().apply { setClassName("com.android.settings", "com.android.settings.RadioInfoActivity") }
        )

        // ၁။ အရင်အတိုင်း တိုက်ရိုက်ဝင်လို့ရမလား အရင်စမ်းမယ်
        for (intent in intentsToTry) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ctx.startActivity(intent)
                result.success(true)
                return
            } catch (e: Exception) {
                continue
            }
        }

        // ၂။ 🌟 အပေါ်က တိုက်ရိုက်နည်းတွေ အကုန်လုံး ပိတ်ထားရင် ဒီ Ultimate Back-up Plan ကို သုံးမယ် Bro
        try {
            // Dial Pad ကို `*#*#4636#*#` အထိ ရိုက်ပြီးသားကြီး လှမ်းဖွင့်ခိုင်းလိုက်တာပါ
            // (နောက်ဆုံး ကြယ်ပွင့် * လေးကိုတော့ Security အရ User ကိုယ်တိုင် နှိပ်ခိုင်းရပါမယ်)
            val encodedSecretCode = Uri.encode("*#*#4636#*#")
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = "tel:$encodedSecretCode".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            ctx.startActivity(dialIntent)

            // Flutter ဘက်ကို true ပြန်ပေးမယ်၊ ဒါပေမဲ့ User ကို ကြယ်ပွင့်နှိပ်ခိုင်းဖို့ မက်ဆေ့ခ်ျ သီးသန့် ပါးချင်ရင် ပြောင်းလို့ရပါတယ်
            result.success(true)
        } catch (e: Exception) {
            result.error(
                "CANNOT_OPEN_PHONE_INFO",
                "ဒီဖုန်းမှာ ဖုန်းခေါ်တဲ့ နေရာကိုပါ လှမ်းဖွင့်လို့ မရပါဘူး Bro: ${e.localizedMessage}",
                null
            )
        }
    }

//    private fun openPhoneInfoPage(currentActivity: android.app.Activity?, ctx: Context, result: MethodChannel.Result) {
//        // စမ်းသပ်မယ့် Intent နည်းလမ်းတွေကို List ထဲ အစဉ်လိုက် ထည့်ထားမယ် Bro
//        val intentsToTry = listOf(
//            // နည်းလမ်း ၁: Modern Android အများစုအတွက် (RadioInfo)
//            Intent(Intent.ACTION_MAIN).apply {
//                component = ComponentName("com.android.settings", "com.android.settings.RadioInfo")
//            },
//            // နည်းလမ်း ၂: Android ဗားရှင်းဟောင်းအချို့နှင့် အချို့သော OS များအတွက် (TestingSettings)
//            Intent("android.intent.action.MAIN").apply {
//                setClassName("com.android.settings", "com.android.settings.TestingSettings")
//            },
//            // နည်းလမ်း ၃: 4636 အတွက် တိုက်ရိုက် Action သုံးပြီး ခေါ်ကြည့်ခြင်း (အချို့ဖုန်းတွေမှာ ဒီ Action ကို ဖွင့်ပေးထားတတ်ပါတယ်)
//            Intent("android.intent.action.MAIN").apply {
//                action = "com.android.settings.settings.TESTING_SETTINGS"
//            },
//            // နည်းလမ်း ၄: Motorola, Pixel သို့မဟုတ် အချို့သော Pure Android ဖုန်းတွေအတွက် နောက်ထပ် Name တစ်ခု
//            Intent().apply {
//                setClassName("com.android.settings", "com.android.settings.Settings\$TestingSettingsActivity")
//            },
//            // နည်းလမ်း ၅: Xiaomi / MIUI / HyperOS ဖုန်းတွေအတွက် သီးသန့် Class
//            Intent().apply {
//                setClassName("com.android.settings", "com.android.settings.RadioInfoActivity")
//            }
//        )
//
//        // တစ်ခုချင်းစီကို ပတ်ပြီး အလုပ်လုပ်တဲ့ကောင် တွေ့တဲ့အထိ ခေါ်ကြည့်မယ်
//        for (intent in intentsToTry) {
//            try {
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                ctx.startActivity(intent)
//                result.success(true)
//                return // ပွင့်သွားပြီဆိုရင် function ထဲက တန်းထွက်မယ်
//            } catch (e: Exception) {
//                // Error တက်ရင် ဘာမှမလုပ်ဘဲ နောက် Intent တစ်ခုကို ဆက်စမ်းမယ်
//                continue
//            }
//        }
//
//        // ဘာနည်းလမ်းမှ အလုပ်မလုပ်တော့ဘူးဆိုမှပဲ ခေါ်လို့မရတဲ့အကြောင်း Flutter ကို အကြောင်းကြားမယ်
//        result.error(
//            "CANNOT_OPEN_PHONE_INFO",
//            "ဒီဖုန်းရဲ့ OS က Phone Info (4636) မျက်နှာပြင်ကို တိုက်ရိုက်ဝင်ခွင့် လုံးဝ ပိတ်ထားပါတယ် Bro။",
//            null
//        )
//    }
}