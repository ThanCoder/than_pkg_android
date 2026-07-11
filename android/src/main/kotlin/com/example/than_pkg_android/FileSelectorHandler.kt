package com.example.than_pkg_android

import android.app.Activity
import android.content.Intent
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class FileSelectorHandler : PkgHandler() {

    private val REQ_FILE_PICKER = 1001

    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        if (activity == null) {
            result.error("NO_ACTIVITY", "Activity is not available", null)
            return
        }
        this.pendingResult = result

        when (method) {
            "pickFile" -> {
                // Flutter ဘက်က ပါလာမယ့် Arguments များကို ဖတ်ခြင်း
                val isMultiple = call.argument<Boolean>("multiple") ?: false
                val title = call.argument<String>("title") ?: "Select Document"
                val mimeType = call.argument<String>("mimeType") ?: "*/*"
                val localOnly = call.argument<Boolean>("localOnly") ?: true

                openFilePicker(isMultiple, title, mimeType, localOnly)
            }
            else -> {
                result.notImplemented()
                pendingResult = null
            }
        }
    }

    private fun openFilePicker(isMultiple: Boolean, title: String, mimeType: String, localOnly: Boolean) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = mimeType

            // Flutter ဘက်က multiple: true ပို့ရင် အများကြီး ရွေးခွင့်ပြုမယ်
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, isMultiple)

            // Flutter ဘက်က ပို့လိုက်တဲ့ Custom Title
            putExtra(Intent.EXTRA_TITLE, title)

            // Local file ပဲ ပြမလား၊ Cloud ပါ ပြမလား ထိန်းချုပ်ခြင်း
            putExtra(Intent.EXTRA_LOCAL_ONLY, localOnly)

            addCategory(Intent.CATEGORY_OPENABLE)
        }

        // Picker ခေါင်းစဉ် (Title) ပေါ်အောင် Intent.createChooser နဲ့ ပတ်ပေးတာ ပိုစိတ်ချရပါတယ်
        val chooserIntent = Intent.createChooser(intent, title)
        activity?.startActivityForResult(chooserIntent, REQ_FILE_PICKER)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == REQ_FILE_PICKER) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val urisList = mutableListOf<String>()

                // ၁။ ဖိုင်အများကြီး ရွေးခဲ့ရင် ClipData ထဲကနေ Loop ပတ်ဖတ်မယ်
                if (data.clipData != null) {
                    val clipData = data.clipData!!
                    val count = clipData.itemCount
                    for (i in 0 until count) {
                        urisList.add(clipData.getItemAt(i).uri.toString())
                    }
                }
                // ၂။ ဖိုင် ၁ ခုတည်းပဲ ရွေးခဲ့ရင်
                else if (data.data != null) {
                    urisList.add(data.data!!.toString())
                }

                // Flutter ဘက်ကို ရလဒ် ပြန်ပို့ခြင်း (အမြဲတမ်း List အနေနဲ့ပဲ ပြန်ပေးတာက Dart ဘက်မှာ သုံးရတာ ပိုရှင်းပါတယ်)
                if (urisList.isNotEmpty()) {
                    pendingResult?.success(urisList)
                } else {
                    pendingResult?.error("NO_FILES_SELECTED", "No files were selected", null)
                }
            } else {
                // User က ဖျက်သိမ်းလိုက်ရင် (Canceled)
                pendingResult?.error("PICK_CANCELED", "User canceled file picking", null)
            }
            pendingResult = null
            return true
        }
        return false
    }
}