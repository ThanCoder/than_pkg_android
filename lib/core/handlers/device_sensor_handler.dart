import 'package:flutter/services.dart';

//<uses-permission android:name="android.permission.HIGH_SAMPLING_RATE_SENSORS"/>
class DeviceSensorHandler {
  final MethodChannel _channel;
  final String _key;
  DeviceSensorHandler(this._channel, {this._key = 'deviceSensorHandler'});

  final EventChannel _eventChannel = EventChannel(
    'than_pkg_android_device_sensor_stream',
  );

  // ၁။ တစ်ကြိမ်ပဲ လက်ရှိတန်ဖoshi ကို လှမ်းတောင်းချင်ရင် (One-time Call)
  Future<Map<dynamic, dynamic>> getAccelerometerValues() async {
    final values = await _channel.invokeMethod<Map<dynamic, dynamic>>(
      '$_key/getAccelerometerValues',
    );
    return values ?? {"x": 0.0, "y": 0.0, "z": 0.0};
  }

  // ၂။ တောက်လျှောက် လှုပ်ရှားမှုတွေကို စောင့်ကြည့်ချင်ရင် (Continuous Stream)
  Stream<Map<dynamic, dynamic>> get accelerometerStream {
    return _eventChannel.receiveBroadcastStream().map(
      (event) => event as Map<dynamic, dynamic>,
    );
  }
}
