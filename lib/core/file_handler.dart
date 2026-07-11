import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

class FileHandler {
  final MethodChannel _channel;
  final String _key;
  const FileHandler(this._channel, {this._key = 'fileHandler'});

  /// ### Copy Content Uri To File
  Future<bool> copyContentToFile(String contentUri, String outpath) async {
    try {
      final bool? success = await _channel.invokeMethod(
        '$_key/copyToLocation',
        {'sourceUri': contentUri, 'targetPath': outpath},
      );
      return success ?? false;
    } on PlatformException catch (e) {
      debugPrint("ဖိုင်ကူးယူတာ မအောင်မြင်ပါ: ${e.message}");
      return false;
    }
  }

  /// ### Move Content Uri To File
  Future<bool> moveContentToFile(String contentUri, String outpath) async {
    try {
      final bool? success = await _channel.invokeMethod(
        '$_key/moveToLocation',
        {'sourceUri': contentUri, 'targetPath': outpath},
      );
      return success ?? false;
    } on PlatformException catch (e) {
      debugPrint("ဖိုင်ရွှေ့တာ မအောင်မြင်ပါ: ${e.message}");
      return false;
    }
  }
}
