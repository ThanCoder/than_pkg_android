package com.example.than_pkg_android

import android.graphics.SurfaceTexture
import android.view.Surface
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.view.TextureRegistry
import kotlin.also

class TextureHandler(private val textureRegistry: TextureRegistry) : PkgHandler() {

    // Texture Entry တွေကို မှတ်ထားမယ့် Map
    private val textures = mutableMapOf<Long, TextureRegistry.SurfaceTextureEntry>()
    // FFI/C++ ဘက်က သုံးနိုင်အောင် Surface တွေကို သိမ်းထားမယ့် Map
    private val surfaces = mutableMapOf<Long, Surface>()

    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        when (method) {
            "createTexture" -> {
                try {
                    val entry = textureRegistry.createSurfaceTexture()
                    val textureId = entry.id()
                    textures[textureId] = entry

                    result.success(textureId)
                } catch (e: Exception) {
                    result.error("TEXTURE_CREATE_FAILED", e.message, null)
                }
            }

            "getSurfacePointer" -> {
                // FFmpeg / C++ ဘက်ကနေ Android Native Surface ပေါ် တိုက်ရိုက် ကုဒ်ဆွဲနိုင်ဖို့
                // ANativeWindow pointer အဖြစ် သုံးမယ့် Surface object ကို လှမ်းယူတာ
                val textureId = call.argument<Number>("textureId")?.toLong()
                val entry = textures[textureId]

                if (entry != null) {
                    // ရှိပြီးသား Surface ရှိရင် ပြန်ပေးမယ်၊ မရှိရင် အသစ်ဆောက်မယ်
                    val surface = surfaces[textureId] ?: Surface(entry.surfaceTexture()).also {
                        surfaces[textureId!!] = it
                    }

                    // မှတ်ချက်- တကယ်တမ်း FFI နဲ့ သုံးရင် Java Surface Object ကို JNI ကနေတစ်ဆင့်
                    // C++ ဘက်က ANativeWindow_fromSurface(env, surface) ဆိုပြီး ပြောင်းသုံးရပါတယ်
                    // ဒီမှာတော့ Java Object ကို အဆင်သင့်ဖြစ်အောင် သိမ်းပေးထားတာပါ
                    result.success(true)
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
        surfaces.remove(textureId)?.release()
        textures.remove(textureId)?.release()
    }

    override fun onDetachedFromActivity() {
        super.onDetachedFromActivity()
        // Plugin ပိတ်သွားရင် memory leak မဖြစ်အောင် အကုန်ရှင်းမယ်
        val keys = textures.keys.toList()
        keys.forEach { releaseTexture(it) }
    }
}