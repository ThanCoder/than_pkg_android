
//
// Created by thancoder on 7/17/26.
//
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <jni.h>
#include <cstring>


// 💡 နာမည်ကို TextureHandler ဖြစ်အောင် ပြောင်းပေးရပါမယ် (underscores တွေ သတိထားပါ)
extern "C" JNIEXPORT jlong JNICALL
Java_com_example_than_1pkg_1android_TextureHandler_getNativeWindowFromSurface(
        JNIEnv* env, jobject thiz, jobject surface) {
    if (!surface) return 0;
    ANativeWindow* window = ANativeWindow_fromSurface(env, surface);
    return reinterpret_cast<jlong>(window);
}
// Dart FFI ကနေ လှမ်းခေါ်ပြီး Frame ထည့်မယ့်ကုဒ်
extern "C" __attribute__((visibility("default"))) __attribute__((used)) void
ffi_android_update_texture_pixels(int64_t surface_pointer,
                                  uint8_t* ffmpeg_buffer, uint32_t width,
                                  uint32_t height) {
    ANativeWindow* window = reinterpret_cast<ANativeWindow*>(surface_pointer);
    if (!window || !ffmpeg_buffer) return;

    // 💡 SurfaceProducer နဲ့ ဆိုရင် ဒီစာကြောင်းက GPU buffer format ကို ကောင်းကောင်း ညှိပေးနိုင်ပါတယ်
    ANativeWindow_setBuffersGeometry(window, width, height, WINDOW_FORMAT_RGBA_8888);

    ANativeWindow_Buffer window_buffer;

    // Lock ချမယ်
    if (ANativeWindow_lock(window, &window_buffer, nullptr) < 0) {
        return;
    }

    uint8_t* src_row = ffmpeg_buffer;
    uint8_t* dst_row = reinterpret_cast<uint8_t*>(window_buffer.bits);

    uint32_t src_stride = width * 4;
    uint32_t dst_stride = window_buffer.stride * 4;

    if (dst_row != nullptr) {
        for (uint32_t y = 0; y < height; ++y) {
            std::memcpy(dst_row, src_row, src_stride);
            src_row += src_stride;
            dst_row += dst_stride;
        }
    }

    // GPU ဆီ လွှဲပေးမယ်
    ANativeWindow_unlockAndPost(window);
}