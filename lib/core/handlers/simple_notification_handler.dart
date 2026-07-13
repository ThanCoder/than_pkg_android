import 'package:flutter/services.dart';

class SimpleNotificationHandler {
  final MethodChannel _channel;
  final String _key;
  const SimpleNotificationHandler(
    this._channel, {
    this._key = 'simpleNotificationHandler',
  });

  Future<String?> getChannelId() async {
    return await _channel.invokeMethod<String>('$_key/getChannelId');
  }

  /// ### Show Notification
  ///
  /// Set `AndroidManifest.xml`
  ///
  /// ```xml
  /// <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
  /// ```
  /// ### Check Runtime Permission
  ///
  /// ```dart
  ///  if (!await ThanPkgAndroid.getInstance.permissionHandler
  ///     .isNotificationPermission()) {
  ///   await ThanPkgAndroid.getInstance.permissionHandler
  ///       .requestNotificationPermission();
  /// }
  /// ```
  ///
  Future<bool> showInfo(String title, String content) async {
    final res = await _channel.invokeMethod<bool>('$_key/showInfo', {
      'title': title,
      'content': content,
    });
    return res ?? false;
  }

  ///
  /// Set `AndroidManifest.xml`
  ///
  /// ```xml
  /// <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
  /// ```
  ///
  /// ### Check Runtime Permission
  ///
  /// ```dart
  ///  if (!await ThanPkgAndroid.getInstance.permissionHandler
  /// .isNotificationPermission()) {
  ///   await ThanPkgAndroid.getInstance.permissionHandler
  /// .requestNotificationPermission();
  /// }
  /// ```
  Future<bool> showProgress(String title, String content) async {
    final res = await _channel.invokeMethod<bool>('$_key/showProgress', {
      'title': title,
      'content': content,
    });
    return res ?? false;
  }

  /// ### Update Progress
  ///
  /// progress range: `1-100`
  ///
  /// speed: `1.5 MB/s`
  ///
  Future<bool> updateProgress(int progress, String speed) async {
    final res = await _channel.invokeMethod<bool>('$_key/updateProgress', {
      'progress': progress,
      'speed': speed, //'1.5 MB/s',
    });
    return res ?? false;
  }

  /// ### Finish Progress
  Future<bool> finishProgress(String title, String content) async {
    final res = await _channel.invokeMethod<bool>('$_key/finish', {
      'title': title,
      'content': content,
    });
    return res ?? false;
  }

  /// ### Close Notification
  Future<bool> dismiss(String title, String content) async {
    final res = await _channel.invokeMethod<bool>('$_key/dismiss');
    return res ?? false;
  }
}



/*


// ၂။ Progress တက်လာတိုင်း (ဥပမာ ရာခိုင်နှုန်း တက်လာရင်)
await channel.invokeMethod('SimpleNotificationHandler/updateProgress', {
  'progress': 45, // ၄၅ ရာခိုင်နှုန်း ရောက်ပြီ
  'speed': '1.5 MB/s',
});

// ၃။ ဒေါင်းလုဒ် ပြီးသွားတဲ့အခါ
await channel.invokeMethod('SimpleNotificationHandler/finish', {
  'title': 'ဒေါင်းလုဒ် အောင်မြင်ပါသည်',
  'content': 'ထူးအိမ်သင်_ထာဝရ။mp3 ကို သိမ်းဆည်းပြီးပါပြီ။',
});
*/