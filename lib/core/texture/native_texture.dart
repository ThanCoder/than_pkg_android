// 1. C++ signature ကို Dart အမြင်အတိုင်း သတ်မှတ်ခြင်း
// ignore_for_file: avoid_print

import 'dart:ffi' as ffi;
import 'dart:math' as math;
import 'package:ffi/ffi.dart';
import 'package:than_pkg_android/than_pkg_android.dart';

typedef FfiAndroidUpdateTextureNative =
    ffi.Void Function(
      ffi.Int64 surfacePointer,
      ffi.Pointer<ffi.Uint8> ffmpegBuffer,
      ffi.Uint32 width,
      ffi.Uint32 height,
    );

// 2. Dart ဘက်ကနေ အသုံးပြုမယ့် Function Signature
typedef FfiAndroidUpdateTextureDart =
    void Function(
      int surfacePointer,
      ffi.Pointer<ffi.Uint8> ffmpegBuffer,
      int width,
      int height,
    );

// Native Library Loader Class တစ်ခု ဆောက်ထားလိုက်ပါ
class NativeTextureManager {
  static NativeTextureManager instance = NativeTextureManager._();
  // NativeTextureManager._();
  // factory NativeTextureManager() => instance;

  ///
  ///     int surfacePointer,
  ///     ffi.Pointer`<ffi.Uint8>` ffmpegBuffer,
  ///     int width,
  ///     int height,
  late final FfiAndroidUpdateTextureDart androidUpdateTexture;

  NativeTextureManager._() {
    // CMakeLists.txt မှာ project(than_pkg_android) လို့ ပေးခဲ့ရင်
    // Library အမည်က libthan_pkg_android.so ဖြစ်လာပါမယ်
    final ffi.DynamicLibrary nativeLib = ffi.DynamicLibrary.open(
      'libthan_pkg_android.so',
    );

    // Function ကို ရှာပြီး Dart ဘက်မှာ သုံးလို့ရအောင် ချိတ်ဆက်ခြင်း
    androidUpdateTexture = nativeLib
        .lookup<ffi.NativeFunction<FfiAndroidUpdateTextureNative>>(
          'ffi_android_update_texture_pixels',
        )
        .asFunction<FfiAndroidUpdateTextureDart>();
  }
}

/// ### dummy test
Future<void> testTextureColor(
  int textureId, {
  int width = 200,
  int height = 200,
}) async {
  final handler = ThanPkgAndroid.getInstance.textureHandler;

  // ၂။ Surface Pointer လှမ်းယူမယ်
  int surfacePointer = await handler.getSurfacePointer(
    textureId,
    width: width,
    height: height,
  );

  if (surfacePointer == 0) {
    print("Error: Surface pointer is null");
    return;
  }

  // ၃။ စမ်းသပ်မည့် Width နဲ့ Height သတ်မှတ်မယ်

  int totalBytes = width * height * 4;

  // ၄။ Memory ပေါ်မှာ Buffer အလွတ်ဆောက်မယ်
  ffi.Pointer<ffi.Uint8> dummyBuffer = calloc<ffi.Uint8>(totalBytes);

  try {
    // --- ပြင်ဆင်ရမည့် နေရာ ---
    // တစ်ခါ function ခေါ်တိုင်း တစ်မျိုးထွက်အောင် Random generator ဆောက်မယ်
    final random = math.Random();

    // R, G, B တစ်ခုချင်းစီအတွက် 0 ကနေ 255 ကြား random value သတ်မှတ်မယ်
    int r = random.nextInt(256);
    int g = random.nextInt(256);
    int b = random.nextInt(256);

    print("Generated Random Color -> R: $r, G: $g, B: $b");

    // Loop ထဲမှာ ဒီ random အရောင်ကိုပဲ တစ်ပြင်လုံး ဖြည့်ပေးလိုက်မယ်
    for (int i = 0; i < totalBytes; i += 4) {
      dummyBuffer[i] = r; // Random Red
      dummyBuffer[i + 1] = g; // Random Green
      dummyBuffer[i + 2] = b; // Random Blue
      dummyBuffer[i + 3] = 255; // Alpha (Fully Opaque)
    }
    // ----------------------

    // ၆။ FFI ကနေတစ်ဆင့် Android Surface ထဲကို ပို့လိုက်မယ်
    NativeTextureManager.instance.androidUpdateTexture(
      surfacePointer,
      dummyBuffer,
      width,
      height,
    );

    print("Texture updated with Random Color successfully!");
  } catch (e) {
    print("FFI Call Error: $e");
  } finally {
    // Memory leak မဖြစ်အောင် buffer ပြန်ဖျက်မယ်
    calloc.free(dummyBuffer);
  }
}
