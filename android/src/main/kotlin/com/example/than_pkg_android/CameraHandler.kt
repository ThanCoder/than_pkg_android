package com.example.than_pkg_android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraHandler : PkgHandler(), PluginRegistry.ActivityResultListener {

    private var currentPhotoPath: String? = null

    override fun handle(
        method: String,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val ctx = context ?: run {
            result.error("NO_CONTEXT", "Context is not available", null)
            return
        }

        when (method) {
            "toggleTorch" -> toggleTorch(ctx, call, result)
            "hasFlashlight" -> hasFlashlight(ctx, result)
            "takePicture" -> takePicture(result) // 🔥 ဓာတ်ပုံရိုက်မယ့် Method အသစ်
            else -> result.notImplemented()
        }
    }

    // ၁။ ဓာတ်မီး ဖွင့်/ပိတ်
    private fun toggleTorch(ctx: Context, call: MethodCall, result: MethodChannel.Result) {
        val enable = call.argument<Boolean>("enable") ?: false
        val cameraManager = ctx.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

        if (cameraManager == null) {
            result.error("CAMERA_SERVICE_NOT_AVAILABLE", "Camera service not available", null)
            return
        }

        // 🌟 Hardware Control ဖြစ်လို့ တိတိကျကျ Main Thread ပေါ်မှာပဲ ပတ်မောင်းမယ်
        Handler(Looper.getMainLooper()).post {
            try {
                var lightTriggered = false
                val cameraIds = cameraManager.cameraIdList

                // နည်းလမ်း (၁) - စက်ထဲမှာရှိသမျှ ကင်မရာ ID အကုန်လုံးကို အတင်းလိုက်ဖွင့်ကြည့်မယ်
                // Xiaomi/Redmi ဖုန်းတွေရဲ့ Multi-camera logic ကို ကျော်ဖြတ်ဖို့ ဖြစ်တယ်
                for (cameraId in cameraIds) {
                    try {
                        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                        val hasFlash = characteristics.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false

                        // Flash ပါတာသေချာရင် (သို့မဟုတ်) Main ID "0" ဖြစ်ရင် ဇွတ်ဖွင့်ခိုင်းမယ်
                        if (hasFlash || cameraId == "0") {
                            cameraManager.setTorchMode(cameraId, enable)
                            lightTriggered = true
                        }
                    } catch (e: Exception) {
                        // အရန်ကင်မရာတွေမှာ error တက်ရင် နောက်တစ်လုံးကို ဆက်သွားမယ်
                        continue
                    }
                }

                // နည်းလမ်း (၂) - တကယ်လို့ အပေါ်က loop က မီးမလင်းခဲ့ရင် Fallback အနေနဲ့ ပထမဆုံး ID ကို အတင်း ထပ်မံ ကြိုးစားမယ်
                if (!lightTriggered && cameraIds.isNotEmpty()) {
                    try {
                        cameraManager.setTorchMode(cameraIds[0], enable)
                        lightTriggered = true
                    } catch (e: Exception) {}
                }

                // Flutter ဘက်ကို တကယ့် အခြေအနေအမှန် ပြန်ပို့မယ်
                if (lightTriggered) {
                    result.success(true)
                } else {
                    result.error("TORCH_FAILED", "Could not trigger torch on any camera ID", null)
                }

            } catch (e: Exception) {
                result.error("TORCH_ERROR", e.localizedMessage, null)
            }
        }
    }
    // ၂။ ဓာတ်မီး ပါ/မပါ စစ်ဆေးခြင်း
    private fun hasFlashlight(ctx: Context, result: MethodChannel.Result) {
        val hasFlash = ctx.packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_CAMERA_FLASH)
        result.success(hasFlash)
    }

    // 📸 ၃။ Built-in ကင်မရာ App ကို သုံးပြီး ဓာတ်ပုံရိုက်ခြင်း
    private fun takePicture(result: MethodChannel.Result) {
        val activity = activity ?: run {
            result.error("NO_ACTIVITY", "Activity is not available", null)
            return
        }

        // လက်ရှိ လုပ်ဆောင်ချက်ကို မှတ်ထားမယ် (Result ပြန်ပို့ဖို့)
        pendingResult = result

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // ကင်မရာ App ဆော့ဖ်ဝဲ ရှိမရှိ စစ်တယ်
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            try {
                // ဓာတ်ပုံသိမ်းမယ့် File တစ်ခု ဆောက်မယ်
                val photoFile = createImageFile(activity)
                currentPhotoPath = photoFile.absolutePath

                // Android 7.0 နောက်ပိုင်းအတွက် FileProvider သုံးပြီး Safe Uri ပြောင်းရတယ် Bro
                val photoURI: Uri = androidx.core.content.FileProvider.getUriForFile(
                    activity,
                    "${activity.packageName}.file_provider", // မင်းရဲ့ AndroidManifest ထဲက Provider နဲ့ တူရမယ်
                    photoFile
                )

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                // ကင်မရာပွင့်လာအောင် Request Code 101 နဲ့ လှမ်းခေါ်လိုက်မယ်
                activity.startActivityForResult(takePictureIntent, 101)

            }
            catch (ex: IllegalArgumentException) {
                // 💡 ဒေဝါး... Dev တွေ Provider ထည့်ဖို့ မေ့သွားရင် ဒီ Error တက်လာမှာပါ
                val devMessage = "🚨 [ThanPkgAndroid Error]: FileProvider configuration missing! " +
                        "Please check if you added <provider> in AndroidManifest.xml and created res/xml/file_paths.xml. " +
                        "Original Error: ${ex.localizedMessage}"

                // Logcat မှာလည်း အနီရောင်နဲ့ ထင်ထင်ရှားရှား ပြပေးမယ်
                android.util.Log.e("ThanPkgAndroid", devMessage)

                // Flutter UI ဘက်ကိုလည်း ဘာကြောင့်လဲဆိုတာ ရှင်းရှင်းလင်းလင်း လှမ်းပြောလိုက်မယ်
                result.error("MISSING_FILE_PROVIDER", devMessage, null)
                pendingResult = null
            }
            catch (ex: Exception) {
                result.error("CAMERA_LAUNCH_FAILED", ex.localizedMessage, null)
                pendingResult = null
            }
        } else {
            result.error("NO_CAMERA_APP", "No camera application found", null)
            pendingResult = null
        }
    }

    // ဓာတ်ပုံဖိုင်ဆောက်ပေးတဲ့ Helper (Cache directory ထဲမှာ သိမ်းမှာမို့ ရှင်းရလွယ်တယ်)
    private fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.cacheDir // Cache ထဲ ထည့်ထားမယ်
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    // 🌟 ကင်မရာ App ကနေ ပုံရိုက်ပြီး ပြန်လာရင် ဖမ်းမယ့်နေရာ
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                // ပုံရိုက်တာ အောင်မြင်ရင် File Path ကို Flutter ဆီ လှမ်းပေးလိုက်မယ်!
                pendingResult?.success(currentPhotoPath)
            } else {
                // အကယ်၍ ဓာတ်ပုံမရိုက်ဘဲ Cancel လုပ်ပြီး ပြန်ထွက်လာရင်
                pendingResult?.success(null)
            }
            pendingResult = null
            return true
        }
        return false
    }
}