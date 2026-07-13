import 'package:flutter/services.dart';

class TextureHandler {
  final MethodChannel _channel;
  final String _key;
  const TextureHandler(this._channel, {this._key = 'textureHandler'});

  /// ### Create Texture Id
  Future<int?> createTexture() async {
    final res = await _channel.invokeMethod<int>('$_key/createTexture');
    return res;
  }

  /// ### Release TextureId
  Future<bool> releaseTexture(int textureId) async {
    final res = await _channel.invokeMethod<bool>('$_key/releaseTexture', {
      'textureId': textureId,
    });
    return res ?? false;
  }

  /// ### Get Surface Pointer for Native/FFI
  Future<bool> getSurfacePointer(int textureId) async {
    final res = await _channel.invokeMethod<bool>('$_key/getSurfacePointer', {
      'textureId': textureId,
    });
    return res ?? false;
  }
}

// ၁။ Flutter ကနေ Texture ID အသစ်တစ်ခု တောင်းမယ်
// final Map<dynamic, dynamic> res = await MethodChannel('than_pkg_android').invokeMethod('textureHandler/createTexture');
// final int textureId = res['textureId'];

// // ၂။ UI မှာ Texture ပြဖို့ ချက်ချင်း နေရာချထားလို့ရပြီ
// // Texture(textureId: textureId)

// // ၃။ Android ဘက်မှာ Surface ကို အဆင်သင့်ဖြစ်အောင် လုပ်ခိုင်းမယ်
// await MethodChannel('than_pkg_android').invokeMethod('textureHandler/getSurfacePointer', {'textureId': textureId});

// // ၄။ အခု FFI အလှည့် ရောက်ပြီ! 
// // မင်းရဲ့ C++ / FFmpeg library ဆီကို textureId (သို့) ၎င်းနဲ့ သက်ဆိုင်တဲ့ Pointer ကို လှမ်းပို့ပေးလိုက်မယ်
// myFFmpegFFILibrary.startStream(textureId, videoPath);