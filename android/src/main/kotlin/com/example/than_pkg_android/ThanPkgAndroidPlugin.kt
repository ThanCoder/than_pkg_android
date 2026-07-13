package com.example.than_pkg_android

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel // 🌟 ဒါလေး import ထည့်ပါ
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

/** ThanPkgAndroidPlugin */
class ThanPkgAndroidPlugin :
    FlutterPlugin,
    MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener, PluginRegistry.NewIntentListener {

    private lateinit var channel: MethodChannel
    private lateinit var streamChannel: EventChannel // 🌟 Stream အတွက် ကြေညာလိုက်တာပါ

    private var activityBinding: ActivityPluginBinding? = null
    private var latestContext: android.content.Context? = null
    private var latestActivity: Activity? = null

    private lateinit var handlers: Map<String, PkgHandler>

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        latestContext = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "than_pkg_android")
        channel.setMethodCallHandler(this)

        // 🌟 ၁။ IntentTransferHandler ကို အရင် object ဆောက်လိုက်မယ် (ဒါမှ StreamHandler ရော Handler ရော တစ်ခုတည်း ဖြစ်မှာပါ)
        val intentTransferHandler = IntentTransferHandler()

        // 🌟 ၂။ EventChannel ဆောက်ပြီး IntentTransferHandler ကို StreamHandler အဖြစ် သတ်မှတ်ပေးလိုက်တာ Bro
        streamChannel = EventChannel(flutterPluginBinding.binaryMessenger, "than_pkg_android_stream")
        streamChannel.setStreamHandler(intentTransferHandler)

        handlers = mapOf<String, PkgHandler>(
            "fileSelector" to FileSelectorHandler(),
            "os" to OsHandler(),
            "wifiHandler" to WifiHandler(),
            "fileHandler" to AndroidFileHandler(),
            "storagePermissionHandler" to StoragePermissionHandler(),
            "permissionHandler" to PermissionHandler(),
            "pdfHandler" to PdfHandler(),
            "storageHandler" to StorageHandler(),
            "safeStorageHandler" to SafeStorageHandler(),
            "textureHandler" to TextureHandler(flutterPluginBinding.textureRegistry),
            "videoHandler" to VideoHandler(),
            "mediaFetchHandler" to MediaFetchHandler(flutterPluginBinding.binaryMessenger),
            "cameraHandler" to CameraHandler(),
            "brightnessHandler" to BrightnessHandler(),
            "soundHandler" to SoundHandler(),
            "orientationHandler" to OrientationHandler(),
            "intentTransferHandler" to intentTransferHandler,
            "notificationHandler" to NotificationHandler(),
            "simpleNotificationHandler" to SimpleNotificationHandler(),
            "downloadHandler" to DownloadHandler()
        )
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        val parts = call.method.split("/")
        if (parts.size < 2) {
            result.notImplemented()
            return
        }

        val key = parts[0]
        val realMethod = parts[1]
        val handler = handlers[key]
        if(handler != null){
            latestContext?.let { it ->
                handler.updateContext(it, latestActivity)
            }
            handler.handle(realMethod, call, result)
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        streamChannel.setStreamHandler(null) // 🌟 Engine က ထွက်ရင် Stream ကိုပါ ဖြုတ်ပေးရပါတယ်
        latestContext = null
    }

    // --- ActivityAware ရဲ့ standard လုပ်ဆောင်ချက်များ ---
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activityBinding = binding
        latestActivity = binding.activity
        binding.addActivityResultListener(this)
        binding.addOnNewIntentListener(this) // 🌟 ၃။ NewIntent ဝင်လာတာကို နားထောင်ဖို့ စာရင်းသွင်းလိုက်တာပါ
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        activityBinding?.removeActivityResultListener(this)
        activityBinding?.removeOnNewIntentListener(this) // 🌟 ၄။ Activity က ထွက်ရင် နားထောင်တာကို ပြန်ဖြုတ်ပေးရပါတယ်
        activityBinding = null
        latestActivity = null
        handlers.values.forEach { it.onDetachedFromActivity() }
    }

    override fun onNewIntent(intent: Intent): Boolean {
        latestActivity?.intent = intent
        handlers.values.forEach { it.onNewIntent(intent) }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        val fileHandler = handlers["fileSelector"] as? FileSelectorHandler
        val isFileHandled =  fileHandler?.onActivityResult(requestCode, resultCode, data) ?: false
        if (isFileHandled) return true

        val storagePermissionHandler = handlers["storagePermissionHandler"] as? StoragePermissionHandler
        val storagePermissionHandled =  storagePermissionHandler?.onActivityResult(requestCode, resultCode, data) ?: false
        if(storagePermissionHandled) return true

        val safeStorageHandler = handlers["safeStorageHandler"] as? SafeStorageHandler
        val safeStorageHandled =  safeStorageHandler?.onActivityResult(requestCode, resultCode, data) ?: false
        if(safeStorageHandled) return true

        val permissionHandler = handlers["permissionHandler"] as? PermissionHandler
        val permissionHandled =  permissionHandler?.onActivityResult(requestCode, resultCode, data) ?: false
        if(permissionHandled) return true

        val cameraHandler = handlers["cameraHandler"] as? CameraHandler
        val cameraHandled =  cameraHandler?.onActivityResult(requestCode, resultCode, data) ?: false
        if(cameraHandled) return true

        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String?>, grantResults: IntArray): Boolean {
        val storagePermissionHandler = handlers["storagePermissionHandler"] as? StoragePermissionHandler
        val storagePermissionHandled =  storagePermissionHandler?.onRequestPermissionsResult(requestCode, permissions, grantResults) ?: false
        if(storagePermissionHandled) return true

        val permissionHandler = handlers["permissionHandler"] as? PermissionHandler
        val permissionHandled =  permissionHandler?.onRequestPermissionsResult(requestCode, grantResults) ?: false
        if(permissionHandled) return true
        return false
    }
}