// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'package:flutter/services.dart';

class IntentTransferReceiveData {
  final String type;
  final String data;
  const IntentTransferReceiveData({required this.type, required this.data});

  @override
  String toString() => 'IntentTransferReceiveData(type: $type, data: $data)';
}

class IntentTransferHandler {
  final MethodChannel _channel;
  final String _key;

  // 🌟 Stream အတွက် EventChannel တစ်ခု ထည့်သွင်း ကြေညာမယ်
  // (Kotlin ဘက်က channel name "than_pkg_android_stream" နဲ့ အတိအကျ တူရပါမယ်)
  static const EventChannel _eventChannel = EventChannel(
    'than_pkg_android_stream',
  );

  const IntentTransferHandler(
    this._channel, {
    this._key =
        'intentTransferHandler', // 💡 Kotlin ရဲ့ handlers map ထဲက key နဲ့ တူအောင် သတိထားပါ (e.g. 'intentTransfer' ဖြစ်နိုင်ပါတယ်)
  });

  /// ### Send Text to other apps
  Future<bool> sendText(String text) async {
    return (await _channel.invokeMethod<bool>('$_key/sendText', {
          'text': text,
        })) ??
        false;
  }

  /// ### Receive Initial Data (Cold Start)
  ///
  /// Set `AndroidManifest.xml`
  /// ```xml
  ///  <activity android:name=".MainActivity" ...>
  ///
  /// <!-- Add Lines -->
  ///     <intent-filter>
  ///         <action android:name="android.intent.action.SEND" />
  ///         <category android:name="android.intent.category.DEFAULT" />
  ///         <data android:mimeType="text/plain" />
  ///     </intent-filter>
  ///
  /// </activity>
  /// ```
  Future<IntentTransferReceiveData?> getInitialData() async {
    final res = await _channel.invokeMapMethod<String, dynamic>(
      '$_key/getInitialData',
    );
    if (res != null) {
      final type = res['type']?.toString() ?? 'Unknown Type';
      final data = res['data']?.toString() ?? 'Unknown Data';
      return IntentTransferReceiveData(type: type, data: data);
    }
    return null;
  }

  /// ### 🌟 Receive Data Live via Stream (App is in background/running)
  ///
  /// Set `AndroidManifest.xml`
  /// ```xml
  ///  <activity android:name=".MainActivity" ...>
  ///
  /// <!-- Add Lines -->
  ///     <intent-filter>
  ///         <action android:name="android.intent.action.SEND" />
  ///         <category android:name="android.intent.category.DEFAULT" />
  ///         <data android:mimeType="text/plain" />
  ///     </intent-filter>
  ///
  /// </activity>
  /// ```
  Stream<IntentTransferReceiveData> get receiveDataStream {
    return _eventChannel.receiveBroadcastStream().map((dynamic event) {
      final Map<dynamic, dynamic> res = event as Map<dynamic, dynamic>;
      final type = res['type']?.toString() ?? 'Unknown Type';
      final data = res['data']?.toString() ?? 'Unknown Data';
      return IntentTransferReceiveData(type: type, data: data);
    });
  }
}
