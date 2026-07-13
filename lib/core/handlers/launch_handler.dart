import 'package:flutter/services.dart';

class LaunchHandler {
  final MethodChannel _channel;
  final String _key;
  const LaunchHandler(this._channel, {this._key = 'launchHandler'});

  Future<bool> openExternalBrowser(String url) async {
    return (await _channel.invokeMethod<bool>('$_key/openExternalBrowser', {
          'url': url,
        })) ??
        false;
  }

  Future<bool> launchUrl(String url) async {
    return (await _channel.invokeMethod<bool>('$_key/launchUrl', {
          'url': url,
        })) ??
        false;
  }

  Future<bool> openCustomTab(String url) async {
    return (await _channel.invokeMethod<bool>('$_key/openCustomTab', {
          'url': url,
        })) ??
        false;
  }

  /// ### Launch File
  ///
  Future<bool> launchFile(String filePath) async {
    return (await _channel.invokeMethod<bool>('$_key/launchFile', {
          'filePath': filePath,
        })) ??
        false;
  }
}
