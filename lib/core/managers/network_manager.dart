import 'package:flutter/services.dart';

//"WIFI", "CELLULAR", "ETHERNET", "NONE"
enum NetworkType {
  wifi('WIFI'),
  cellular('CELLULAR'),
  ethernet('ETHERNET'),
  none('NONE');

  final String value;
  const NetworkType(this.value);

  static NetworkType fromValue(String val) {
    return values.firstWhere((e) => e.value == val, orElse: () => none);
  }
}

class NetworkManager {
  final MethodChannel _channel;
  final String _key;
  NetworkManager(this._channel, {this._key = 'networkHandler'});

  final EventChannel _streamChannel = EventChannel(
    'than_pkg_android_network_stream',
  );

  ///
  /// ### Set: `AndroidManifest.xml`
  /// ```xml
  /// <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
  /// ```
  ///
  Future<NetworkType> checkNetworkStatus() async {
    final status = await _channel.invokeMethod<String>(
      '$_key/checkNetworkStatus',
    );
    return NetworkType.fromValue(status ?? '');
  }

  ///
  /// ### Set: `AndroidManifest.xml`
  /// ```xml
  /// <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
  /// ```
  ///
  Stream<NetworkType> get networkStatusStream {
    return _streamChannel.receiveBroadcastStream().map(
      (event) => NetworkType.fromValue(event.toString()),
    );
  }
}

/*
class ThanNetworkManager {
  static const MethodChannel _channel = MethodChannel('than_pkg_android');
  static const EventChannel _streamChannel = EventChannel('than_pkg_android_network_stream');

  // One-time လှမ်းစစ်တဲ့ Function
  static Future<String> checkNetworkStatus() async {
    final String status = await _channel.invokeMethod('networkHandler/checkNetworkStatus');
    return status;
  }

  // Real-time နားထောင်မယ့် Stream
  static Stream<String> get networkStatusStream {
    return _streamChannel.receiveBroadcastStream().map((event) => event.toString());
  }
}
 */
