package com.example.than_pkg_android

import android.content.Context
import android.media.AudioManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class SoundHandler : PkgHandler() {
    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        val currentContext = context
        if (currentContext == null) {
            result.error("NO_CONTEXT", "Context is not available", null)
            return
        }

        val audioManager = currentContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        when (method) {
            "setVolume" -> {
                // Flutter ဘက်ကနေ 0.0 ကနေ 1.0 ကြား ပို့ပေးတာကို Android ရဲ့ Max Volume နဲ့ မြှောက်ပြီး သတ်မှတ်ပေးတာပါ
                val volumeInput = call.argument<Double>("volume")
                if (volumeInput != null && volumeInput in 0.0..1.0) {
                    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    val targetVolume = (volumeInput * maxVolume).toInt()

                    // FLAG_SHOW_UI ထည့်ထားရင် အသံတိုး/ကျယ်ရင် ဘေးမှာ Android System Volume Bar ပြပေးလိမ့်မယ် (မပြချင်ရင် 0 ထားပါ)
//                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, AudioManager.FLAG_SHOW_UI)
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0)

                    result.success(true)
                } else {
                    result.error("INVALID_ARGUMENT", "Volume must be between 0.0 and 1.0", null)
                }
            }
            "getVolume" -> {
                val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val volumePercentage = currentVolume.toDouble() / maxVolume.toDouble()
                result.success(volumePercentage)
            }
            else -> result.notImplemented()
        }
    }
}