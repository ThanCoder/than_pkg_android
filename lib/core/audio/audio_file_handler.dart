import 'package:flutter/services.dart';
// import 'index.dart';

class AudioFileHandler {
  final MethodChannel _channel;
  final String _key;
  const AudioFileHandler(this._channel, {this._key = 'AudioFileInfoHandler'});

  String _getMethod(String methodName) => '$_key/$methodName';
}
