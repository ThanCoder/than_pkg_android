package com.example.than_pkg_android

import android.net.Uri
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread
import androidx.core.net.toUri

class AndroidFileHandler : PkgHandler() {
    override fun handle(
        method: String,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        when (method) {
            "copyToLocation" -> {
                val sourceUriStr = call.argument<String>("sourceUri")
                val targetPathStr = call.argument<String>("targetPath")

                if (sourceUriStr == null || targetPathStr == null) {
                    result.error("INVALID_ARGUMENTS", "Missing sourceUri or targetPath", null)
                    return
                }

                // ဖိုင်အကြီးကြီးတွေ ဖြစ်နိုင်လို့ Background Thread ပေါ်မှာ လုပ်ပေးမယ်
                thread {
                    try {
                        val sourceUri = sourceUriStr.toUri()
                        val targetFile = File(targetPathStr)

                        // Folder မရှိသေးရင် ဆောက်ပေးမယ်
                        targetFile.parentFile?.mkdirs()

                        context?.contentResolver?.openInputStream(sourceUri)?.use { inputStream ->
                            FileOutputStream(targetFile).use { outputStream ->
                                inputStream.copyTo(outputStream) // ဒီတစ်ကြောင်းတည်းနဲ့ ကူးပေးတာပါ
                            }
                        }

                        // အောင်မြင်ရင် True ပြန်ပို့မယ်
                        result.success(true)
                    } catch (e: Exception) {
                        result.error("COPY_FAILED", e.localizedMessage, null)
                    }
                }
            }

            "moveToLocation" -> {
                val sourceUriStr = call.argument<String>("sourceUri")
                val targetPathStr = call.argument<String>("targetPath")

                if (sourceUriStr == null || targetPathStr == null) {
                    result.error("INVALID_ARGUMENTS", "Missing sourceUri or targetPath", null)
                    return
                }

                thread {
                    try {
                        val sourceUri = sourceUriStr.toUri()
                        val targetFile = File(targetPathStr)
                        targetFile.parentFile?.mkdirs()

                        // ၁။ ပထမဆုံး ဖိုင်ကို target နေရာထဲ ကူးထည့်မယ်
                        context?.contentResolver?.openInputStream(sourceUri)?.use { inputStream ->
                            FileOutputStream(targetFile).use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }

                        // ၂။ ကူးပြီးသွားရင် မူရင်း content:// ဖိုင်ကို ဖျက်ပစ်မယ် (Move ဖြစ်သွားအောင်)
                        // သတိပြုရန် - ACTION_GET_CONTENT ဖိုင်ဆိုရင် မူရင်းဖိုင်ကို ဖျက်ခွင့် မရှိနိုင်ပါဘူး (Read Only မို့လို့)
                        // ACTION_OPEN_DOCUMENT နဲ့ ယူထားတဲ့ ဖိုင်ဆိုရင်တော့ ဖျက်ခွင့်ရှိပါတယ်။
                        try {
                            context?.contentResolver?.delete(sourceUri, null, null)
                        } catch (e: Exception) {
                            // မူရင်းဖိုင် ဖျက်မရရင်လည်း Copy ပြီးသွားပြီဖြစ်လို့ လတ်တလော ခွင့်လွှတ်ပေးလိုက်မယ်
                        }

                        result.success(true)
                    } catch (e: Exception) {
                        result.error("MOVE_FAILED", e.localizedMessage, null)
                    }
                }
            }

            else -> result.notImplemented()
        }
    }
}