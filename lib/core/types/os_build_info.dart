class OsBuildInfo {}

/* 

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
*/
