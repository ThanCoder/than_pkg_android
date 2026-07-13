// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'package:flutter/services.dart';

class WifiDetail {
  final String mainWifiIp;
  final String wifiBroadcastAddress;
  final String wifiSSid;
  final List<String> ipdAddressList;
  WifiDetail({
    required this.mainWifiIp,
    required this.wifiBroadcastAddress,
    required this.wifiSSid,
    required this.ipdAddressList,
  });

  factory WifiDetail.rawMap(Map<String, dynamic> map) {
    return WifiDetail(
      mainWifiIp: map['MAIN_WIFI_IP'] ?? '',
      wifiBroadcastAddress: map['WIFI_BROADCAST_ADDRESS'] ?? '',
      wifiSSid: map['WIFI_SSID'] ?? '',
      ipdAddressList: map['IP_ADDRESS_LIST'] == null
          ? []
          : List<String>.from(map['IP_ADDRESS_LIST']),
    );
  }

  @override
  String toString() {
    return 'WifiDetail(mainWifiIp: $mainWifiIp, wifiBroadcastAddress: $wifiBroadcastAddress, wifiSSid: $wifiSSid, ipdAddressList: $ipdAddressList)';
  }
}

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
  Future<WifiDetail?> getWifiDetails() async {
    final map = await _channel.invokeMapMethod<String, dynamic>(
      '$_key/getWifiDetails',
    );
    if (map != null) {
      return WifiDetail.rawMap(map);
    }
    return null;
  }
}
