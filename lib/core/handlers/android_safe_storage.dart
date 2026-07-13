import 'package:flutter/services.dart';

class AndroidSafeStorage {
  final MethodChannel _channel;
  final String _key;
  const AndroidSafeStorage(this._channel, {this._key = 'safeStorageHandler'});

  /// ၁။ User ဆီကနေ တိကျတဲ့ Folder တစ်ခုလုံးအတွက် ဝင်ရောက်ခွင့်တောင်းမယ်
  /// ဒါကို ခေါ်လိုက်ရင် Android က Folder ရွေးခိုင်းတဲ့ Screen တက်လာပါလိမ့်မယ်။
  Future<String?> requestFolderPermission() async {
    try {
      final String? uri = await _channel.invokeMethod(
        '$_key/requestFolderPermission',
      );
      return uri; // ဒါက "content://com.android.externalstorage.documents/tree/..." ဆိုပြီး ပြန်လာပါလိမ့်မယ်
    } catch (e) {
      return null;
    }
  }

  /// ၂။ ခွင့်ပြုချက်ရထားတဲ့ Tree Uri ထဲမှာ Folder အသစ်လှမ်းဆောက်မယ်
  Future<String?> createFolder({
    required String treeUri,
    required String folderName,
  }) async {
    try {
      final String? newFolderUri = await _channel.invokeMethod(
        '$_key/createFolder',
        {'treeUri': treeUri, 'folderName': folderName},
      );
      return newFolderUri;
    } catch (e) {
      return null;
    }
  }

  Future<bool> checkFolderPermission(String treeUri) async {
    try {
      final bool? hasPermission = await _channel.invokeMethod(
        '$_key/checkFolderPermission',
        {'treeUri': treeUri},
      );
      return hasPermission ?? false;
    } catch (e) {
      return false;
    }
  }

  /// ၃။ ပေးထားတဲ့ Uri ကို ဖျက်ပစ်မယ် (File ရော Folder ပါ ဖျက်လို့ရပါတယ်)
  Future<bool> deleteItem(String targetUri) async {
    try {
      final bool? success = await _channel.invokeMethod('$_key/deleteItem', {
        'targetUri': targetUri,
      });
      return success ?? false;
    } catch (e) {
      return false;
    }
  }

  /// ခွင့်ပြုချက်ရထားတဲ့ Folder Uri အောက်မှာ ရှိသမျှ ဖိုင်နဲ့ Folder စာရင်းကို လှမ်းယူမယ်
  Future<List<Map<dynamic, dynamic>>?> listFiles(String treeUri) async {
    try {
      final List<dynamic>? files = await _channel.invokeMethod(
        '$_key/listFiles',
        {'treeUri': treeUri},
      );
      if (files != null) {
        return files.cast<Map<dynamic, dynamic>>();
      }
      return [];
    } catch (e) {
      return null;
    }
  }

  /// ခွင့်ပြုချက်ရထားတဲ့ Uri ထဲကို တိုက်ရိုက် Data ရေးထည့်မယ် (ဥပမာ- obb ထဲ ဖိုင်အသစ်သွားချတာမျိုး)
  Future<String?> writeFileData({
    required String parentUri,
    required String fileName,
    required Uint8List bytes,
    String mimeType = 'application/octet-stream',
  }) async {
    try {
      final String? fileUri = await _channel
          .invokeMethod('$_key/writeFileData', {
            'parentUri': parentUri,
            'fileName': fileName,
            'mimeType': mimeType,
            'bytes': bytes,
          });
      return fileUri;
    } catch (e) {
      return null;
    }
  }
}
