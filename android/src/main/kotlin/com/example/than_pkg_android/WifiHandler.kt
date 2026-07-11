package com.example.than_pkg_android

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Collections
import kotlin.concurrent.thread

class WifiHandler : PkgHandler() {

    override fun handle(
        method: String,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val ctx = context ?: run {
            result.error("NO_CONTEXT", "Context is not available", null)
            return
        }

        // Network functions တွေကို Background မှာ မောင်းမယ်
        thread {
            when (method) {
                "getWifiDetails" -> getWifiDetails(ctx, result)
                else -> {
                    // Flutter Method-channel response ကို Main Thread ပေါ် ပြန်တင်ပေးမယ်
                    Handler(Looper.getMainLooper()).post {
                        result.notImplemented()
                    }
                }
            }
        }
    }

    private fun getWifiDetails(ctx: Context, result: MethodChannel.Result) {
        try {
            val info = mutableMapOf<String, Any?>()
            val wifiManager = ctx.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

            // ၁။ Active IPv4 Address တွေ ရှာမယ်
            val ipList = getAllAvailableIpv4Addresses()
            info["IP_ADDRESS_LIST"] = ipList

            // ၂။ Main Wi-Fi IP
            val ipAddressLong = wifiManager?.connectionInfo?.ipAddress ?: 0
            val mainWifiIp = if (ipAddressLong != 0) {
                String.format(
                    "%d.%d.%d.%d",
                    ipAddressLong and 0xff,
                    ipAddressLong shr 8 and 0xff,
                    ipAddressLong shr 16 and 0xff,
                    ipAddressLong shr 24 and 0xff
                )
            } else {
                if (ipList.isNotEmpty()) ipList[0] else "0.0.0.0"
            }
            info["MAIN_WIFI_IP"] = mainWifiIp

            // ၃။ Broadcast IP
            info["WIFI_BROADCAST_ADDRESS"] = getBroadcastAddress(mainWifiIp)

            // ၄။ Wi-Fi SSID
            var ssid = "Unknown"
            try {
                val infoWifi = wifiManager?.connectionInfo
                if (infoWifi != null && infoWifi.networkId != -1) {
                    ssid = infoWifi.ssid.replace("\"", "")
                }
            } catch (e: Exception) {
                ssid = "Permission Required"
            }
            info["WIFI_SSID"] = ssid

            // 🌟 🌟 🌟 [အဓိကအချက်] ရလာတဲ့ ဒေတာကို Main Thread ပေါ်တင်ပြီးမှ Flutter ဆီ ပို့မယ်!
            Handler(Looper.getMainLooper()).post {
                result.success(info)
            }

        } catch (e: Exception) {
            // Error တက်ရင်လည်း Main Thread ပေါ်ကနေပဲ ပို့ရမယ်
            Handler(Looper.getMainLooper()).post {
                result.error("WIFI_INFO_FAILED", e.localizedMessage, null)
            }
        }
    }

    private fun getAllAvailableIpv4Addresses(): List<String> {
        val addresses = mutableListOf<String>()
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (intf.isLoopback || !intf.isUp) continue

                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        val hostAddress = addr.hostAddress
                        if (hostAddress != null && hostAddress != "0.0.0.0") {
                            addresses.add(hostAddress)
                        }
                    }
                }
            }
        } catch (ex: Exception) { }
        return addresses
    }

    private fun getBroadcastAddress(ip: String): String {
        if (ip == "0.0.0.0" || !ip.contains(".")) return "255.255.255.255"
        try {
            val parts = ip.split(".")
            if (parts.size == 4) {
                return "${parts[0]}.${parts[1]}.${parts[2]}.255"
            }
        } catch (e: Exception) {}
        return "255.255.255.255"
    }
}