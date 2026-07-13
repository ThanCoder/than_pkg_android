import 'package:flutter/services.dart';

class WifiHandler {
  final MethodChannel _channel;
  final String _key;
  const WifiHandler(this._channel, {this._key = 'wifiHandler'});

  ///
  /// 
  /// ### Need Permission
  /// ```xml
  /// <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
  /// ```
  /// ```json
  /// {
  ///   "IP_ADDRESS_LIST": ["192.168.43.1", "192.168.1.105", "10.0.2.15"],
  ///   "MAIN_WIFI_IP": "192.168.43.1",
  ///   "WIFI_BROADCAST_ADDRESS": "192.168.43.255",
  ///   "WIFI_SSID": "MyRedmiHotspot"
  /// }
  /// ```
  Future<Map<String, dynamic>> getWifiDetails() async {
    final res = await _channel.invokeMapMethod<String, dynamic>(
      '$_key/getWifiDetails',
    );
    return res ?? {};
  }
}
