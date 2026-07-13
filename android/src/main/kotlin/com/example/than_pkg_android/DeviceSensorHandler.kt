package com.example.than_pkg_android

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class DeviceSensorHandler : PkgHandler(), EventChannel.StreamHandler, SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var eventSink: EventChannel.EventSink? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    // 🌟 နောက်ဆုံးရရှိထားတဲ့ Sensor values တွေကို သိမ်းထားဖို့ variable ကြေညာမယ်
    private var currentX: Float = 0.0f
    private var currentY: Float = 0.0f
    private var currentZ: Float = 0.0f

    // --- ၁။ One-time Method Call နဲ့ လှမ်းတောင်းရင် လက်ရှိတန်ဖိုးကို ချက်ချင်းပြန်ပေးဖို့ ---
    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        val ctx = context ?: run {
            result.error("NO_CONTEXT", "Context is not available", null)
            return
        }

        when (method) {
            "getAccelerometerValues" -> {
                // တကယ်လို့ SensorManager ကို မတည်ဆောက်ရသေးရင် တစ်ခါတည်းဆောက်ပြီး အလုပ်လုပ်ခိုင်းမယ်
                if (sensorManager == null) {
                    // 🌟 Context.SENSOR_SERVICE ကို သုံးပြီး စနစ်တကျ Cast လုပ်ပေးရပါမယ်
                    sensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
                    accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                }

                if (accelerometer == null) {
                    result.error("SENSOR_NOT_AVAILABLE", "Accelerometer sensor not found on this device", null)
                    return
                }

                // လက်ရှိ နောက်ဆုံးသိမ်းထားမိတဲ့ sensor data ကို Map အနေနဲ့ ပြန်ပေးလိုက်မယ် (Snapshot value)
                val currentData = mapOf(
                    "x" to currentX,
                    "y" to currentY,
                    "z" to currentZ
                )
                result.success(currentData)
            }
            else -> result.notImplemented()
        }
    }

    // --- ၂။ Stream စတင်နားထောင်ချိန် (Flutter က .listen() လုပ်ချိန်) ---
    override fun onListen(arguments: Any?, sink: EventChannel.EventSink?) {
        this.eventSink = sink
        val ctx = context ?: return

        sensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Sensor နားထောင်ခြင်း စတင်မယ်
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: run {
            sink?.error("SENSOR_NOT_AVAILABLE", "Accelerometer sensor not found on this device", null)
        }
    }

    // --- ၃။ Stream Cancel ဖြစ်သွားရင် နားထောင်တာကို ရပ်ပစ်မယ် ---
    override fun onCancel(arguments: Any?) {
        sensorManager?.unregisterListener(this)
        sensorManager = null
        accelerometer = null
        eventSink = null
    }

    // Sensor တန်ဖိုးတွေ လှုပ်ရှားမှုအရ ပြောင်းလဲလာချိန်တိုင်း ဝင်မယ့်နေရာ
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        // X, Y, Z ဝင်ရိုးတန်ဖိုးများကို Update လုပ်ပြီး Variable ထဲ အမြဲသိမ်းထားမယ်
        currentX = event.values[0]
        currentY = event.values[1]
        currentZ = event.values[2]

        val sensorData = mapOf(
            "x" to currentX,
            "y" to currentY,
            "z" to currentZ
        )

        // Flutter ဘက်က Stream (.listen) နားထောင်နေရင် Data အမြဲလှမ်းပို့ပေးမယ်
        eventSink?.let { sink ->
            mainHandler.post {
                sink.success(sensorData)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // လူပ်ရှားမှု တိကျချက်ပြောင်းလဲသွားရင် လုပ်ဆောင်ရန်
    }
}