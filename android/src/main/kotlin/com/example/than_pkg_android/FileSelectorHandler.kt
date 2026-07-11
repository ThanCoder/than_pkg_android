package com.example.than_pkg_android

import android.app.Activity
import android.content.Context
import android.content.Intent
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class FileSelectorHandler: PkgHandler() {

    override fun handle(
        method: String,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        if (activity == null) {
            result.error("NO_ACTIVITY", "Activity is not available", null)
            return
        }
        this.pendingResult = result

        when(method){
            "pickFile" -> {
                pickFile()
            }
            "pickFiles" -> {
                pickFiles()
            }
            else -> {
                result.notImplemented()
            }
        }


    }
    fun pickFile(){
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            // ၁။ File Type ကို ကန့်သတ်ခြင်း (Mime Type)
            //type = "image/*" // ဓာတ်ပုံ သီးသန့်ပဲ ပြချင်ရင်
            // type = "video/*" // ဗီဒီယို သီးသန့်ပဲ ပြချင်ရင်
            // type = "application/pdf" // PDF သီးသန့်ပဲ ပြချင်ရင်
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            // ၃။ ပွင့်လာမယ့် File Picker ရဲ့ အပေါ်ဆုံး ခေါင်းစဉ် (Title) ကို ပြောင်းခြင်း
            putExtra(Intent.EXTRA_TITLE, "Select Document")

            // ၄။ Local မှာ ရှိတဲ့ File တွေကိုပဲ ပြဖို့ (Cloud Drive တွေဖြစ်တဲ့ Google Drive လိုဟာမျိုး မပြချင်ရင်)
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)

            // category ကို ဖွင့်ထားဖို့ အကြံပြုပါတယ်။ ဖွင့်လို့မရတဲ့ ကွဲနေတဲ့ ဖိုင်တွေကို စစ်ထုတ်ပေးပါတယ်။
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        activity?.startActivityForResult(intent, 1001)
    }
    fun pickFiles(){
        // File ရွေးဖို့ Intent ခေါ်ခြင်း
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            // ၁။ File Type ကို ကန့်သတ်ခြင်း (Mime Type)
            //type = "image/*" // ဓာတ်ပုံ သီးသန့်ပဲ ပြချင်ရင်
            // type = "video/*" // ဗီဒီယို သီးသန့်ပဲ ပြချင်ရင်
            // type = "application/pdf" // PDF သီးသန့်ပဲ ပြချင်ရင်

            // ၂။ ဖိုင်အများကြီး တစ်ပြိုင်နက် ရွေးခွင့်ပြုခြင်း (Multiple Selection)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            // ၃။ ပွင့်လာမယ့် File Picker ရဲ့ အပေါ်ဆုံး ခေါင်းစဉ် (Title) ကို ပြောင်းခြင်း
            putExtra(Intent.EXTRA_TITLE, "Select Document")

            // ၄။ Local မှာ ရှိတဲ့ File တွေကိုပဲ ပြဖို့ (Cloud Drive တွေဖြစ်တဲ့ Google Drive လိုဟာမျိုး မပြချင်ရင်)
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)

            // category ကို ဖွင့်ထားဖို့ အကြံပြုပါတယ်။ ဖွင့်လို့မရတဲ့ ကွဲနေတဲ့ ဖိုင်တွေကို စစ်ထုတ်ပေးပါတယ်။
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        activity?.startActivityForResult(intent, 1001)
    }

    // တကယ်လို့ Activity ကနေ Result ပြန်လာရင် Flutter ဘက်ကို ဒါနဲ့ လှမ်းပေးရပါမယ် (Main Plugin ကနေ လှမ်းခေါ်ပေးရမယ်)
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            val urisList = mutableListOf<String>()

            if (data != null) {
                // ၁။ ဖိုင်အများကြီး ရွေးခဲ့ရင် ClipData ထဲကနေ ပတ်ဖတ်မယ်
                if (data.clipData != null) {
                    val clipData = data.clipData!!
                    val count = clipData.itemCount
                    for (i in 0 until count) {
                        val fileUri = clipData.getItemAt(i).uri.toString()
                        urisList.add(fileUri)
                    }
                }
                // ၂။ ဖိုင် ၁ ခုတည်းပဲ ရွေးခဲ့ရင်
                else if (data.data != null) {
                    urisList.add(data.data!!.toString())
                }
            }

            // ရလဒ် ပြန်ပို့မည့်အပိုင်းကို ပြင်ဆင်ခြင်း
            if (urisList.isNotEmpty()) {
                if (urisList.size == 1) {
                    // ဖိုင် တစ်ခုတည်းဆိုရင် String အနေနဲ့ပဲ ပို့ပေးမယ်
                    pendingResult?.success(urisList.first())
                } else {
                    // ဖိုင် အများကြီးဆိုရင် List<String> အတိုင်း ပို့ပေးမယ်
                    pendingResult?.success(urisList)
                }
            } else {
                pendingResult?.error("NO_FILES_SELECTED", "No files were selected", null)
            }

            pendingResult = null
            return true
        }
        return false
    }
}