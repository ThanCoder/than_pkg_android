package com.example.than_pkg_android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class BatteryHandler : PkgHandler(), EventChannel.StreamHandler {

    private var eventSink: EventChannel.EventSink? = null
    private var batteryReceiver: BroadcastReceiver? = null

    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        val ctx = context ?: return result.error("NO_CONTEXT", "Context is null", null)

        // Method Call နဲ့ တစ်ခုချင်းစီ လှမ်းတောင်းရင် အကုန်လုံးကို Map တစ်ခုတည်းနဲ့ ပြန်ပေးလိုက်တာ ပိုကောင်းတယ် Bro
        if (method == "getBatteryInfo") {
            result.success(getBatteryInfoMap(ctx))
        } else {
            result.notImplemented()
        }
    }

    override fun onListen(arguments: Any?, sink: EventChannel.EventSink?) {
        this.eventSink = sink
        val ctx = context ?: return

        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                    // ဘက်ထရီ အခြေအနေ ပြောင်းတိုင်း Data အစုံကို လွှတ်ပေးမယ်
                    eventSink?.success(getBatteryInfoMap(ctx))
                }
            }
        }
        ctx.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onCancel(arguments: Any?) {
        context?.unregisterReceiver(batteryReceiver)
        batteryReceiver = null
        eventSink = null
    }

    // --- ဘက်ထရီ Data Mode အစုံကို Map ဖွဲ့ပေးတဲ့ Helper Method ---
    private fun getBatteryInfoMap(ctx: Context): Map<String, Any> {
        val intent = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ?: return emptyMap()

        // ၁။ Battery Level
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryLevel = if (level >= 0 && scale > 0) ((level.toFloat() / scale.toFloat()) * 100).toInt() else -1

        // ၂။ Charging Status
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        // ၃။ Power Source (ဘယ်ကနေ အားသွင်းနေလဲ)
        val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val powerSource = when (chargePlug) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "WIRELESS"
            else -> "BATTERY"
        }

        // ၄။ Battery Health (ကျန်းမာရေး)
        val healthInt = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        val health = when (healthInt) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "GOOD"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "OVERHEAT" // ပူလွန်းလို့
            BatteryManager.BATTERY_HEALTH_DEAD -> "DEAD" // ပျက်နေပြီ
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "OVER_VOLTAGE" // ဗို့အားများလွန်းလို့
            BatteryManager.BATTERY_HEALTH_COLD -> "COLD" // အေးလွန်းလို့
            else -> "UNKNOWN"
        }

        // ၅။ Temperature (အပူချိန်ကို စင်တီဂရိတ် ပြောင်းတာပါ - Android က ၁၀ နဲ့ မြှောက်ပြီး ပေးလို့ ပြန်စားရတယ်)
        val tempRaw = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
        val temperature = tempRaw.toFloat() / 10

        // ၆။ Voltage (ဗို့အား - mV)
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)

        // ၇။ Technology (ဥပမာ - Li-ion)
        val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "UNKNOWN"

        return mapOf(
            "batteryLevel" to batteryLevel,
            "isCharging" to isCharging,
            "powerSource" to powerSource,
            "health" to health,
            "temperature" to temperature,
            "voltage" to voltage,
            "technology" to technology
        )
    }
}