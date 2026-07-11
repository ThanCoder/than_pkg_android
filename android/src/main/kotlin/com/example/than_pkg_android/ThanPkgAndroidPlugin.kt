package com.example.than_pkg_android

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

/** ThanPkgAndroidPlugin */
class ThanPkgAndroidPlugin :
    FlutterPlugin,
    MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener {
    // The MethodChannel that will the communication between Flutter and native Android
    //
    // This local reference serves to register the plugin with the Flutter Engine and unregister it
    // when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private var activityBinding: ActivityPluginBinding? = null
    private var latestContext: android.content.Context? = null
    private var latestActivity: Activity? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        latestContext = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "than_pkg_android")
        channel.setMethodCallHandler(this)
    }

    private val handlers = mapOf<String, PkgHandler>(
        "fileSelector" to FileSelectorHandler(),
        "os" to OsHandler(),
        "fileHandler" to AndroidFileHandler(),
        "storagePermissionHandler" to StoragePermissionHandler(),
        "pdfHandler" to PdfHandler(),
        "storageHandler" to StorageHandler(),
        "safeStorageHandler" to SafeStorageHandler()
    )

    override fun onMethodCall(
        call: MethodCall,
        result: Result
    ) {
        // ဥပမာ- "os/getPlatformVersion" လို့ လာရင် ["os", "getPlatformVersion"] ဆိုပြီး ခွဲလိုက်မယ်
        val parts = call.method.split("/")

        if (parts.size < 2) {
            result.notImplemented()
            return
        }

        val key = parts[0]          // "os"
        val realMethod = parts[1]   // "getPlatformVersion"
        val handler = handlers[key]
        if(handler != null){
            latestContext?.let { it ->
                handler.updateContext(it,latestActivity)
            }
            handler.handle(realMethod,call,result)
        }else{
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        latestContext = null
    }
    // --- ActivityAware ရဲ့ standard လုပ်ဆောင်ချက်များ ---
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activityBinding = binding
        latestActivity = binding.activity
        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
       onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        activityBinding?.removeActivityResultListener(this)
        activityBinding = null
        latestActivity=null
        handlers.values.forEach { it.onDetachedFromActivity() }
    }
    // --- Activity ကနေ ဓာတ်ပုံရိုက်တာ၊ ဖိုင်ရွေးတာတွေ ပြန်လာရင် ဒီကို ရောက်မယ် ---
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Boolean {
        // FileSelectorHandler ရဲ့ ရလဒ်ဖြစ်မဖြစ် လှမ်းစစ်ပြီး လွှဲပေးလိုက်တာ
        val fileHandler = handlers["fileSelector"] as? FileSelectorHandler
        val isFileHandled =  fileHandler?.onActivityResult(requestCode, resultCode, data) ?: false

        if (isFileHandled) return true

        // PermissionHandler ဆီ လွှဲပေးခြင်း
        val storagePermissionHandler = handlers["storagePermissionHandler"] as? StoragePermissionHandler
        val storagePermissionHandled =  storagePermissionHandler?.onActivityResult(requestCode, resultCode, data) ?: false
        if(storagePermissionHandled) return true;

        val safeStorageHandler = handlers["safeStorageHandler"] as? SafeStorageHandler;
        return safeStorageHandler?.onActivityResult(resultCode,resultCode,data)?:false
    }
    // Android 6.0 ~ 10 ရလဒ်အတွက် (Dialog က ပြန်လာရင်)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray
    ): Boolean {
        // PermissionHandler ဆီ လွှဲပေးခြင်း
        val storagePermissionHandler = handlers["storagePermissionHandler"] as? StoragePermissionHandler
        return storagePermissionHandler?.onRequestPermissionsResult(requestCode, permissions, grantResults) ?: false
    }
}
