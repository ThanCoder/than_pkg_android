package com.example.than_pkg_android

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

class ThanPkgAndroidPlugin :
    FlutterPlugin,
    MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener, PluginRegistry.NewIntentListener {

    private lateinit var channel: MethodChannel
    private lateinit var streamChannel: EventChannel
    private lateinit var batteryStreamChannel: EventChannel
    private lateinit var networkStreamChannel: EventChannel
    private lateinit var deviceSensorStreamChannel: EventChannel

    private var activityBinding: ActivityPluginBinding? = null
    private var latestContext: android.content.Context? = null
    private var latestActivity: Activity? = null

    // 🌟 တကယ်သုံးနေတဲ့ Handler တွေကိုပဲ Dynamic သိမ်းမယ့် Map (Memory အများကြီး သက်သာသွားစေတယ်)
    private val activeHandlers = mutableMapOf<String, PkgHandler>()

    // Flutter Binding ကို Factory Method ထဲမှာ သုံးနိုင်အောင် ခေတ္တသိမ်းထားမယ်
    private var pluginBinding: FlutterPlugin.FlutterPluginBinding? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        pluginBinding = flutterPluginBinding
        latestContext = flutterPluginBinding.applicationContext

        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "than_pkg_android")
        channel.setMethodCallHandler(this)

        // 🌟 Route တွေကို ကြေညာခဲ့ပေမယ့် Handler Object ကို Factory ကနေပဲ လိုအပ်မှ ဆောက်ခိုင်းမယ် (Lazy Initialization)
        streamChannel = EventChannel(flutterPluginBinding.binaryMessenger, "than_pkg_android_stream")
        streamChannel.setStreamHandler(getOrCreateHandler("intentTransferHandler") as? EventChannel.StreamHandler)

        batteryStreamChannel = EventChannel(flutterPluginBinding.binaryMessenger, "than_pkg_android_battery_stream")
        batteryStreamChannel.setStreamHandler(getOrCreateHandler("batteryHandler") as? EventChannel.StreamHandler)
        // 🌟 Network Stream အတွက် EventChannel အသစ် ဖွင့်လိုက်ပြီ
        networkStreamChannel = EventChannel(flutterPluginBinding.binaryMessenger, "than_pkg_android_network_stream")
        networkStreamChannel.setStreamHandler(getOrCreateHandler("networkHandler") as? EventChannel.StreamHandler)

        deviceSensorStreamChannel = EventChannel(flutterPluginBinding.binaryMessenger, "than_pkg_android_device_sensor_stream")
        deviceSensorStreamChannel.setStreamHandler(getOrCreateHandler("deviceSensorHandler") as EventChannel.StreamHandler)
    }

    // 🌟 အဓိက အသက်ပဲ Bro! သုံးခါနီးမှ Object ကို On-Demand ဆောက်ပေးမယ့် Factory Method
    private fun getOrCreateHandler(key: String): PkgHandler? {
        // ရှိပြီးသား ကောင်ဆိုရင် အသစ်မဆောက်ဘဲ တန်းပြန်ပေးမယ်
        if (activeHandlers.containsKey(key)) return activeHandlers[key]

        val binding = pluginBinding ?: return null
        val messenger = binding.binaryMessenger
        val textureRegistry = binding.textureRegistry

        val newHandler = when (key) {
            "fileSelector" -> FileSelectorHandler()
            "osHandler" -> OsHandler()
            "wifiHandler" -> WifiHandler()
            "fileHandler" -> AndroidFileHandler()
            "storagePermissionHandler" -> StoragePermissionHandler()
            "permissionHandler" -> PermissionHandler()
            "pdfHandler" -> PdfHandler()
            "storageHandler" -> StorageHandler()
            "safeStorageHandler" -> SafeStorageHandler()
            "textureHandler" -> TextureHandler(textureRegistry)
            "videoHandler" -> VideoHandler()
            "mediaFetchHandler" -> MediaFetchHandler(messenger)
            "cameraHandler" -> CameraHandler()
            "brightnessHandler" -> BrightnessHandler()
            "soundHandler" -> SoundHandler()
            "orientationHandler" -> OrientationHandler()
            "notificationHandler" -> NotificationHandler()
            "simpleNotificationHandler" -> SimpleNotificationHandler()
            "nativeDownloadManager" -> NativeDownloadManager()
            "privacyHandler" -> PrivacyHandler()
            "appSettingHandler" -> AppSettingHandler()
            "launchHandler" -> LaunchHandler()
            "intentTransferHandler" -> IntentTransferHandler() // Stream feature ပါတဲ့ကောင်
            "batteryHandler" -> BatteryHandler() // Stream feature ပါတဲ့ကောင်သစ်
            "networkHandler" -> NetworkHandler()
            "deviceSensorHandler" -> DeviceSensorHandler()
            else -> null
        }

        if (newHandler != null) {
            // Context ရှိနှင့်ပြီးသား ဖြစ်နေရင် ချက်ချင်းထည့်ပေးလိုက်မယ်
            latestContext?.let { newHandler.updateContext(it, latestActivity) }
            activeHandlers[key] = newHandler
        }
        return newHandler
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        val parts = call.method.split("/")
        if (parts.size < 2) {
            result.notImplemented()
            return
        }

        val key = parts[0]
        val realMethod = parts[1]

        // 🌟 ဒီမှာ Map အဟောင်းထဲက မရှာတော့ဘဲ Factory Method ဆီကနေ တောင်းမယ်
        val handler = getOrCreateHandler(key)

        if (handler != null) {
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
        streamChannel.setStreamHandler(null)
        batteryStreamChannel.setStreamHandler(null)
        networkStreamChannel.setStreamHandler(null)
        deviceSensorStreamChannel.setStreamHandler(null)

        activeHandlers.clear() // Engine ဖြုတ်ရင် Memory ကနေ လုံးဝထုတ်မယ်
        latestContext = null
        pluginBinding = null
    }

    // --- ActivityAware လုပ်ဆောင်ချက်များ ---
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activityBinding = binding
        latestActivity = binding.activity
        binding.addActivityResultListener(this)
        binding.addOnNewIntentListener(this)

        // Active ဖြစ်နေတဲ့ Handler တွေကို လက်ရှိ Activity State သွားပေးမယ်
        activeHandlers.values.forEach {
            latestContext?.let { ctx -> it.updateContext(ctx, latestActivity) }
        }
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        activityBinding?.removeActivityResultListener(this)
        activityBinding?.removeOnNewIntentListener(this)
        activityBinding = null
        latestActivity = null

        // Active ဖြစ်နေတဲ့ ကောင်တွေကိုပဲ ပတ်ပြီး Detach လုပ်တော့မယ်
        activeHandlers.values.forEach { it.onDetachedFromActivity() }
        activeHandlers.clear() // Activity က ထွက်ရင် ပိုစိတ်ချရအောင် map ကိုပါ ရှင်းပစ်မယ်
    }

    override fun onNewIntent(intent: Intent): Boolean {
        latestActivity?.intent = intent
        activeHandlers.values.forEach { it.onNewIntent(intent) }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        // activeHandlers ထဲမှာ လက်ရှိ ရှိနေမှသာ Cast လုပ်ပြီး Check မယ် (မလိုအပ်ဘဲ ကြိုဆောက်မထားတော့ဘူး)
        (activeHandlers["fileSelector"] as? FileSelectorHandler)?.let {
            if (it.onActivityResult(requestCode, resultCode, data)) return true
        }
        (activeHandlers["storagePermissionHandler"] as? StoragePermissionHandler)?.let {
            if (it.onActivityResult(requestCode, resultCode, data)) return true
        }
        (activeHandlers["safeStorageHandler"] as? SafeStorageHandler)?.let {
            if (it.onActivityResult(requestCode, resultCode, data)) return true
        }
        (activeHandlers["permissionHandler"] as? PermissionHandler)?.let {
            if (it.onActivityResult(requestCode, resultCode, data)) return true
        }
        (activeHandlers["cameraHandler"] as? CameraHandler)?.let {
            if (it.onActivityResult(requestCode, resultCode, data)) return true
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String?>, grantResults: IntArray): Boolean {
        (activeHandlers["storagePermissionHandler"] as? StoragePermissionHandler)?.let {
            if (it.onRequestPermissionsResult(requestCode, permissions, grantResults)) return true
        }
        (activeHandlers["permissionHandler"] as? PermissionHandler)?.let {
            if (it.onRequestPermissionsResult(requestCode, grantResults)) return true
        }
        return false
    }
}