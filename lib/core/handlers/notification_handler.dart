import 'package:flutter/services.dart';

class NotificationHandler {
  final MethodChannel _channel;
  final String _key;
  const NotificationHandler(this._channel, {this._key = 'notificationHandler'});

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
  ///
  Future<void> show(
    int id, {
    required String title,
    required String content,
  }) async {
    await _channel.invokeMethod('$_key/show', {
      'id': id,
      'title': title,
      'content': content,
    });
  }

  Future<void> updateProgress(int id, int progress, {String speed = ''}) async {
    await _channel.invokeMethod('$_key/updateProgress', {
      'id': id,
      'progress': progress,
      'speed': speed,
    });
  }

  Future<void> finish(
    int id, {
    required String title,
    required String content,
  }) async {
    await _channel.invokeMethod('$_key/finish', {
      'id': id,
      'title': title,
      'content': content,
    });
  }

  Future<void> cancel(int id) async {
    await _channel.invokeMethod('$_key/cancel', {'id': id});
  }
}


/*
class ThanNotificationTask {
  final int id; // Task တစ်ခုစီရဲ့ ID (e.g. 1, 2, 3)
  final MethodChannel _channel;

  ThanNotificationTask(this.id, this._channel);

  Future<void> show({required String title, required String content}) async {
    await _channel.invokeMethod('notificationHandler/show', {
      'id': id,
      'title': title,
      'content': content,
    });
  }

  Future<void> updateProgress(int progress, {String speed = ''}) async {
    await _channel.invokeMethod('notificationHandler/updateProgress', {
      'id': id,
      'progress': progress,
      'speed': speed,
    });
  }

  Future<void> finish({required String title, required String content}) async {
    await _channel.invokeMethod('notificationHandler/finish', {
      'id': id,
      'title': title,
      'content': content,
    });
  }

  Future<void> cancel() async {
    await _channel.invokeMethod('notificationHandler/cancel', {'id': id});
  }
}

*/