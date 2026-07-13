// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'package:flutter/services.dart';

class FileSelector {
  final MethodChannel _channel;
  final String _key;
  FileSelector(this._channel, {this._key = 'fileSelector'});

  /// ### Android File Picker
  /// - [multiple] `true` ပေးလျှင် ဖိုင်အများကြီး တစ်ပြိုင်နက် ရွေးနိုင်မည်။
  /// - [title] Picker UI အပေါ်ဆုံးတွင် ပေါ်မည့် ခေါင်းစဉ်စာသား။
  /// - [mimeType] ဖိုင်အမျိုးအစား သတ်မှတ်ရန်။ ဥပမာ- `image/*`, `video/*`, `application/pdf`
  /// - [localOnly] `true` ဆိုလျှင် Device ထဲက ဖိုင်များကိုပဲ ပြပေးမည် (Google Drive စသည်တို့ မပါဝင်ပါ)။
  ///
  /// ### use uriHandler
  /// - `ThanPkgAndroid.getInstance.uriHandler.copyContentToFile(contentUri,outpath,)`;
  /// - `ThanPkgAndroid.getInstance.uriHandler.moveContentToFile(contentUri, outpath)`;
  ///
  Future<List<String>> pickFiles({
    bool multiple = false,
    String title = 'Select Document',
    String mimeType = '*/*',
    bool localOnly = true,
  }) async {
    try {
      final List<dynamic>? result = await _channel
          .invokeListMethod<dynamic>('$_key/pickFile', {
            'multiple': multiple,
            'title': title,
            'mimeType': mimeType,
            'localOnly': localOnly,
          });

      // null ပြန်မယ့်အစား empty list [] ပဲ ပြန်ပေးလိုက်မယ်
      if (result == null) return [];
      return result.map((e) => e.toString()).toList();
    } on PlatformException catch (_) {
      return [];
    }
  }

  /// ### Pick Video File
  Future<List<String>> pickVideoFiles({
    bool multiple = false,
    String title = 'Select Video',
    bool localOnly = true,
  }) async {
    return await pickFiles(
      multiple: multiple,
      title: title,
      localOnly: localOnly,
      mimeType: 'video/*',
    );
  }

  /// ### Pick Image File
  Future<List<String>> pickImageFiles({
    bool multiple = false,
    String title = 'Select Image',
    bool localOnly = true,
  }) async {
    return await pickFiles(
      multiple: multiple,
      title: title,
      localOnly: localOnly,
      mimeType: 'image/*',
    );
  }

  /// ### Pick Audio File
  Future<List<String>> pickAudioFiles({
    bool multiple = false,
    String title = 'Select Audio',
    bool localOnly = true,
  }) async {
    return await pickFiles(
      multiple: multiple,
      title: title,
      localOnly: localOnly,
      mimeType: 'audio/*',
    );
  }

  /// ### Pick Pdf File
  Future<List<String>> pickPdfFiles({
    bool multiple = false,
    String title = 'Select Pdf',
    bool localOnly = true,
  }) async {
    return await pickFiles(
      multiple: multiple,
      title: title,
      localOnly: localOnly,
      mimeType: "application/pdf",
    );
  }
}
