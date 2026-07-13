import 'package:flutter/services.dart';

class NativeDownloadManager {
  final MethodChannel _channel;
  final String _key;
  const NativeDownloadManager(
    this._channel, {
    this._key = 'nativeDownloadManager',
  });

  /// ### Android Native DownloadManager
  ///
  /// Return: `downloadId`
  ///
  Future<String?> enqueue(
    String url, {
    required String filename,
    String? title,
    String? description,
  }) async {
    return await _channel.invokeMethod<String>('$_key/enqueue', {
      'url': url,
      'filename': filename,
      'title': title,
      'description': description,
    });
  }

  /// ### Cancel Download Item
  Future<bool> cancel(String downloadId) async {
    return (await _channel.invokeMethod<bool>('$_key/cancel')) ?? false;
  }

  /// ### Pause Download Item
  Future<bool> pause(String downloadId) async {
    return (await _channel.invokeMethod<bool>('$_key/pause')) ?? false;
  }

  /// ### Resume Download Item
  Future<bool> resume(String downloadId) async {
    return (await _channel.invokeMethod<bool>('$_key/resume')) ?? false;
  }
}

/*
<!-- အင်တာနက်ကနေ ဖိုင်ဒေါင်းဖို့အတွက် (မဖြစ်မနေ လိုတယ်) -->
<uses-permission android:name="android.android.permission.INTERNET" />

<!-- ဒေါင်းလုဒ်ပြီးသွားရင် ဖုန်းရဲ့ Public Download folder ထဲ ဖိုင်သိမ်းဖို့အတွက် -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />

<!-- 🌟 အရေးကြီးဆုံး: Android 13+ (API 33) ဖုန်းတွေမှာ ဒေါင်းလုဒ်ဆွဲနေတဲ့ Noti တက်လာဖို့အတွက် -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
 */
