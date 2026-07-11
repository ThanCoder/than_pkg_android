// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

class FileSelector {
  final MethodChannel _channel;
  final String _key;
  FileSelector(this._channel, {this._key = 'fileSelector'});

  Future<String?> pickFile() async {
    return await _channel.invokeMethod<String>('$_key/pickFile');
  }

  Future<List<String>> pickFiles() async {
    try {
      final res = await _channel.invokeListMethod<String>('$_key/pickFiles');
      if (res == null) return [];
      return List<String>.from(res);
    } catch (e) {
      debugPrint('[FileSelector:pickFiles]: $e');
      return [];
    }
  }
}
