import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';

enum PdfImageFormat { jpg, png }

class AndroidPdfHandler {
  final MethodChannel _channel;
  final String _key;
  const AndroidPdfHandler(this._channel, {this._key = 'pdfHandler'});

  /// ### Save Pdf Thumbnail
  ///
  /// [format] 'png' or 'jpg'
  Future<bool> saveToThumbnail({
    required String pdfPathOrUri,
    required String targetPath,
    int? width,
    int? height,
    int pageIndex = 0,
    PdfImageFormat format = .jpg,
  }) async {
    try {
      final bool? success = await _channel
          .invokeMethod('$_key/saveToThumbnail', {
            'pdfUri': pdfPathOrUri,
            'targetPath': targetPath,
            'width': width,
            'height': height,
            'pageIndex': pageIndex,
            'format': format.name,
          });
      return success ?? false;
    } catch (e) {
      debugPrint("[AndroidPdf:saveToThumbnail]: $e");
      return false;
    }
  }

  /// ### Get Pdf Page Image
  /// [format] 'png' or 'jpg'
  Future<Uint8List?> getPage({
    required String pdfPathOrUri,
    int? width,
    int? height,
    int pageIndex = 0,
    PdfImageFormat format = .jpg,
  }) async {
    try {
      final Uint8List? bytes = await _channel.invokeMethod('$_key/getPage', {
        'pdfUri': pdfPathOrUri,
        'width': width,
        'height': height,
        'pageIndex': pageIndex,
        'format': format,
      });
      return bytes;
    } catch (e) {
      debugPrint("[AndroidPdf:getPage]: $e");
      return null;
    }
  }

  /// ၃။ PDF ရဲ့ စာမျက်နှာ စုစုပေါင်း အရေအတွက်ကိုပဲ သီးသန့်သိချင်ရင် သုံးရန်
  Future<int> getPageCount(String pdfPathOrUri) async {
    try {
      final int? count = await _channel.invokeMethod('$_key/getPageCount', {
        'pdfUri': pdfPathOrUri,
      });
      return count ?? 0;
    } catch (e) {
      return 0;
    }
  }
}
