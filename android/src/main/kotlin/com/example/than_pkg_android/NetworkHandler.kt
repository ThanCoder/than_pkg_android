package com.example.than_pkg_android // မင်းရဲ့ actual package name ပြောင်းပေးပါ

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresPermission
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlin.concurrent.thread

class NetworkHandler : PkgHandler(), EventChannel.StreamHandler {

    private var eventSink: EventChannel.EventSink? = null
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var airplaneModeReceiver: BroadcastReceiver? = null

    // --- ၁။ One-time Method Call နဲ့ လှမ်းတောင်းရင် ပြန်ပေးဖို့ ---
    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        val ctx = context ?: run {
            result.error("NO_CONTEXT", "Context is not available", null)
            return
        }

        thread {
            when (method) {
                "checkNetworkStatus" -> {
                    val status = getActiveNetworkStatus(ctx)
                    //send stream
                    sendNetworkStatus(status)
                    Handler(Looper.getMainLooper()).post {

                        result.success(status) // Return values: "WIFI", "CELLULAR", "ETHERNET", "NONE"
                    }
                }
                else -> {
                    Handler(Looper.getMainLooper()).post {
                        result.notImplemented()
                    }
                }
            }
        }
    }

    // --- ၂။ Stream စတင်နားထောင်ချိန် (Flutter က .listen() လုပ်ချိန်) ---
    override fun onListen(arguments: Any?, sink: EventChannel.EventSink?) {
        this.eventSink = sink
        val ctx = context ?: return

        connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // นားထောင်စဥ်မှာ လက်ရှိ ဖြစ်နေတဲ့ အခြေအနေကို ပထမဆုံးတစ်ကြိမ် ချက်ချင်းပို့ပေးမယ်
        sendNetworkStatus(getActiveNetworkStatus(ctx))

        // Callback တည်ဆောက်ခြင်း
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                sendNetworkStatus(getActiveNetworkStatus(ctx))
            }

            override fun onLost(network: Network) {
                // လိုင်းတစ်ခုခု ပြုတ်သွားရင် ကျန်တဲ့လိုင်း ရှိသေးလား ပြန်စစ်ခိုင်းမယ်
                sendNetworkStatus(getActiveNetworkStatus(ctx))
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                // အင်တာနက် အခြေအနေ ပြောင်းလဲသွားရင် (ဥပမာ- internet validated ဖြစ်သွားတာမျိုး) လက်ရှိ တကယ့် status ကို ပြန်စစ်မယ်
                sendNetworkStatus(getActiveNetworkStatus(ctx))
            }

            override fun onUnavailable() {
                sendNetworkStatus(getActiveNetworkStatus(ctx))
            }


        }

        // 🌟 အဆင့်မြှင့်ထားသော နေရာ - Android 7.0 (API 24) နှင့်အထက်ဆိုရင် registerDefaultNetworkCallback ကို သုံးပြီး
        // တစ်ဖုန်းလုံးရဲ့ Default Network ပြောင်းလဲမှုကို တိတိကျကျ စောင့်ကြည့်မယ်။
        connectivityManager?.registerDefaultNetworkCallback(networkCallback!!)

        // 🌟 လုံးဝစိတ်ချရအောင် လှည့်ကွက်ထည့်ခြင်း - Airplane Mode ပြောင်းလဲမှုကို ဖမ်းမယ့် BroadcastReceiver
        airplaneModeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                    // Airplane mode ဖွင့်/ပိတ် လုပ်လိုက်ရင် NetworkCallback တွေ ငြိမ်နေရင်တောင် ဒီကောင်က ချက်ချင်းမိပြီး UI ကို NONE ပြောင်းပေးမယ်
                    sendNetworkStatus(getActiveNetworkStatus(ctx))
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        ctx.registerReceiver(airplaneModeReceiver, filter)
    }

    // --- ၃။ Stream Cancel ဖြစ်သွားရင် (Unlisten လုပ်ရင်) Callback နဲ့ Receiver တွေကို ပြန်ဖြုတ်တာ ---
    override fun onCancel(arguments: Any?) {
        // NetworkCallback ကို ဖြုတ်ခြင်း
        try {
            connectivityManager?.unregisterNetworkCallback(networkCallback!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Airplane Mode Receiver ကို ပြန်ဖြုတ်ခြင်း (Memory leak မဖြစ်အောင်)
        try {
            context?.unregisterReceiver(airplaneModeReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        networkCallback = null
        airplaneModeReceiver = null
        connectivityManager = null
        eventSink = null
    }

    // Main Thread ပေါ် ပြန်တင်ပြီး Flutter ဆီ Data လွှတ်ပေးတဲ့ Helper
    private fun sendNetworkStatus(status: String) {
        Handler(Looper.getMainLooper()).post {
            eventSink?.success(status)
        }
    }

    // ဖုန်းရဲ့ လက်ရှိ Network အခြေအနေကို တိုက်ရိုက် စစ်ဆေးပေးတဲ့ Helper
    private fun getActiveNetworkStatus(ctx: Context): String {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = cm.activeNetwork ?: return "NONE"
        val actNw = cm.getNetworkCapabilities(nw) ?: return "NONE"
        return parseNetworkStatus(actNw)
    }

    // NetworkCapabilities ကနေ အင်တာနက် တကယ်ရှိမရှိနဲ့ လိုင်းအမျိုးအစား ခွဲခြားပေးတဲ့ Helper
    private fun parseNetworkStatus(capabilities: NetworkCapabilities): String {
        // တကယ် အင်တာနက် data စီးဆင်းမှု ရှိမရှိ (Validated ဖြစ်မဖြစ်) စစ်တာ
        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        if (!hasInternet) return "NONE"

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "CELLULAR"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ETHERNET"
            else -> "NONE"
        }
    }
}