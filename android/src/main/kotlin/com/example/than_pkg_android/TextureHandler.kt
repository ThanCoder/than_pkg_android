package com.example.than_pkg_android

import android.view.Surface
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.view.TextureRegistry

class TextureHandler(private val textureRegistry: TextureRegistry) : PkgHandler() {

    // Map ထဲမှာ SurfaceProducer ရော Surface ကိုပါ သိမ်းထားမယ် (GC က လာမစားအောင်)
    private val producers = HashMap<Long, TextureRegistry.SurfaceProducer>()
    private val surfaces = HashMap<Long, Surface>()

    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        when (method) {
            "createTexture" -> {
                try {
                    // 💡 ခေတ်မီ SurfaceProducer API အသစ်ကို သုံးမယ်
                    val producer = textureRegistry.createSurfaceProducer()
                    val textureId = producer.id()
                    producers[textureId] = producer

                    result.success(textureId)
                } catch (e: Exception) {
                    result.error("TEXTURE_CREATE_FAILED", e.message, null)
                }
            }

            "getSurfacePointer" -> {
                val textureId = call.argument<Number>("textureId")?.toLong()
                val width = call.argument<Number>("width")?.toInt() ?: 200
                val height = call.argument<Number>("height")?.toInt() ?: 200

                val producer = producers[textureId]

                if (producer != null) {
                    // 1. Buffer Size ကို Flutter Texture System ထဲ တိုက်ရိုက် ကြိုသတ်မှတ်တယ်
                    producer.setSize(width, height)

                    // 2. Surface ယူမယ်
                    val surface = producer.surface
                    surfaces[textureId!!] = surface

                    // 3. JNI ဘက်က ANativeWindow_fromSurface ကို သုံးပြီး တရားဝင် Pointer Address ထုတ်မယ်
                    try {
                        val nativePointer = getNativeWindowFromSurface(surface)

                        if (nativePointer == 0L) {
                            result.error("POINTER_NULL", "Cannot get ANativeWindow pointer from Surface", null)
                        } else {
                            result.success(nativePointer)
                        }
                    } catch (e: Exception) {
                        result.error("POINTER_ERROR", e.message, null)
                    }
                } else {
                    result.error("NOT_FOUND", "Texture ID not found", null)
                }
            }

            "releaseTexture" -> {
                val textureId = call.argument<Number>("textureId")?.toLong()
                if (textureId != null) {
                    releaseTexture(textureId)
                    result.success(true)
                } else {
                    result.error("INVALID_ARGUMENT", "TextureId is null", null)
                }
            }

            else -> result.notImplemented()
        }
    }

    private fun releaseTexture(textureId: Long) {
        // Surface ကို release လုပ်ပြီး map ထဲက ဖျက်မယ်
        surfaces.remove(textureId)?.release()

        // SurfaceProducer ကို ခေါ်ပြီး release လုပ်မယ် (textures map အဟောင်းနေရာမှာ အစားထိုးတာပါ)
        producers.remove(textureId)?.release()
    }

    override fun onDetachedFromActivity() {
        super.onDetachedFromActivity()
        // Plugin ပိတ်သွားရင် memory leak မဖြစ်အောင် အကုန်ရှင်းမယ်
        val keys = producers.keys.toList()
        keys.forEach { releaseTexture(it) }
    }

    companion object {
        init {
            System.loadLibrary("than_pkg_android") // 👈 မင်းရဲ့ C++ CMake က ထွက်လာတဲ့ .so နာမည် (ဥပမာ libthan_pkg_android.so ဆိုရင် "than_pkg_android" လို့ ထည့်ပါ)
        }
    }

    // 💡 external key word ထည့်ပေးရပါမယ်
    private external fun getNativeWindowFromSurface(surface: Surface): Long
}