package com.example.than_pkg_android

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class AudioHandler : PkgHandler() {

    override fun handle(
        method: String,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val ctx = context

        if (ctx == null) {
            result.error(
                "NO_CONTEXT",
                "Context is not available",
                null
            )
            return
        }

        when (method) {

            // Audio state
            "getAudioInfo" -> {
                getAudioInfo(ctx, result)
            }

            "getVolumeInfo" -> {
                getVolumeInfo(ctx, result)
            }

            "getAudioDevices" -> {
                getAudioDevices(ctx, result)
            }

            "isMusicActive" -> {
                isMusicActive(ctx, result)
            }

            "isSpeakerphoneOn" -> {
                isSpeakerphoneOn(ctx, result)
            }

            // Volume
            "getVolume" -> {
                getVolume(ctx, call, result)
            }

            "setVolume" -> {
                setVolume(ctx, call, result)
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    // -------------------------------------------------------------------------
    // Audio Info
    // -------------------------------------------------------------------------

    private fun getAudioInfo(
        ctx: Context,
        result: MethodChannel.Result
    ) {
        try {
            val audioManager =
                ctx.getSystemService(
                    Context.AUDIO_SERVICE
                ) as AudioManager

            result.success(
                mapOf(
                    "mode" to audioManager.mode,

                    "isMusicActive" to
                            audioManager.isMusicActive,

                    "isSpeakerphoneOn" to
                            audioManager.isSpeakerphoneOn,

                    "isBluetoothA2dpOn" to
                            audioManager.isBluetoothA2dpOn,

                    "isBluetoothScoOn" to
                            audioManager.isBluetoothScoOn,

                    "isWiredHeadsetOn" to
                            audioManager.isWiredHeadsetOn,

                    "ringerMode" to
                            audioManager.ringerMode,

                    "isVolumeFixed" to
                            audioManager.isVolumeFixed
                )
            )

        } catch (e: Exception) {
            result.error(
                "AUDIO_INFO_ERROR",
                e.localizedMessage
                    ?: "Failed to get audio info",
                null
            )
        }
    }

    // -------------------------------------------------------------------------
    // Volume Info
    // -------------------------------------------------------------------------

    private fun getVolumeInfo(
        ctx: Context,
        result: MethodChannel.Result
    ) {
        try {
            val audioManager =
                ctx.getSystemService(
                    Context.AUDIO_SERVICE
                ) as AudioManager

            val streams = listOf(
                AudioManager.STREAM_MUSIC,
                AudioManager.STREAM_RING,
                AudioManager.STREAM_ALARM,
                AudioManager.STREAM_NOTIFICATION,
                AudioManager.STREAM_SYSTEM,
                AudioManager.STREAM_VOICE_CALL
            )

            val info = mutableMapOf<String, Any?>()

            for (stream in streams) {

                val max =
                    audioManager.getStreamMaxVolume(stream)

                val current =
                    audioManager.getStreamVolume(stream)

                val muted =
                    audioManager.isStreamMute(stream)

                info[streamName(stream)] =
                    mapOf(
                        "volume" to current,
                        "maxVolume" to max,
                        "isMuted" to muted
                    )
            }

            result.success(info)

        } catch (e: Exception) {
            result.error(
                "VOLUME_INFO_ERROR",
                e.localizedMessage
                    ?: "Failed to get volume info",
                null
            )
        }
    }

    // -------------------------------------------------------------------------
    // Get Volume
    // -------------------------------------------------------------------------

    private fun getVolume(
        ctx: Context,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        try {
            val stream = call.argument<Int>("stream_type")

            if (stream == null) {
                result.error(
                    "INVALID_STREAM",
                    "Invalid audio stream",
                    null
                )
                return
            }

            val audioManager =
                ctx.getSystemService(
                    Context.AUDIO_SERVICE
                ) as AudioManager

            result.success(
                audioManager.getStreamVolume(stream)
            )

        } catch (e: Exception) {
            result.error(
                "GET_VOLUME_ERROR",
                e.localizedMessage
                    ?: "Failed to get volume",
                null
            )
        }
    }

    // -------------------------------------------------------------------------
    // Set Volume
    // -------------------------------------------------------------------------

    private fun setVolume(
        ctx: Context,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        try {
            val stream = call.argument<Int>("stream_type")


            val volume =
                call.argument<Int>("volume")

            if (stream == null) {
                result.error(
                    "INVALID_STREAM",
                    "Invalid audio stream",
                    null
                )
                return
            }

            if (volume == null) {
                result.error(
                    "INVALID_VOLUME",
                    "Missing volume",
                    null
                )
                return
            }

            val audioManager =
                ctx.getSystemService(
                    Context.AUDIO_SERVICE
                ) as AudioManager

            val maxVolume =
                audioManager.getStreamMaxVolume(stream)

            val safeVolume =
                volume.coerceIn(0, maxVolume)

            audioManager.setStreamVolume(
                stream,
                safeVolume,
                0
            )

            result.success(safeVolume)

        } catch (e: Exception) {
            result.error(
                "SET_VOLUME_ERROR",
                e.localizedMessage
                    ?: "Failed to set volume",
                null
            )
        }
    }

    // -------------------------------------------------------------------------
    // Audio Devices
    // -------------------------------------------------------------------------

    private fun getAudioDevices(
        ctx: Context,
        result: MethodChannel.Result
    ) {
        try {

            val audioManager =
                ctx.getSystemService(
                    Context.AUDIO_SERVICE
                ) as AudioManager

            val devices =
                audioManager.getDevices(
                    AudioManager.GET_DEVICES_INPUTS
                )

            val list = devices.map { device ->

                mapOf(
                    "id" to device.id,
                    "type" to device.type,
                    "name" to device.productName?.toString(),

                    "address" to
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                device.address
                            } else {
                                ""
                            },
                    "isSource" to (
                            device.isSource
                            ),
                    "isSink" to (
                            device.isSink
                            )
                )
            }

            result.success(list)

        } catch (e: Exception) {
            result.error(
                "AUDIO_DEVICE_ERROR",
                e.localizedMessage
                    ?: "Failed to get audio devices",
                null
            )
        }
    }

    // -------------------------------------------------------------------------
    // Music Active
    // -------------------------------------------------------------------------

    private fun isMusicActive(
        ctx: Context,
        result: MethodChannel.Result
    ) {
        try {
            val audioManager =
                ctx.getSystemService(
                    Context.AUDIO_SERVICE
                ) as AudioManager

            result.success(
                audioManager.isMusicActive
            )

        } catch (e: Exception) {
            result.error(
                "MUSIC_STATE_ERROR",
                e.localizedMessage
                    ?: "Failed to get music state",
                null
            )
        }
    }

    // -------------------------------------------------------------------------
    // Speakerphone
    // -------------------------------------------------------------------------

    private fun isSpeakerphoneOn(
        ctx: Context,
        result: MethodChannel.Result
    ) {
        try {
            val audioManager =
                ctx.getSystemService(
                    Context.AUDIO_SERVICE
                ) as AudioManager

            result.success(
                audioManager.isSpeakerphoneOn
            )

        } catch (e: Exception) {
            result.error(
                "SPEAKER_STATE_ERROR",
                e.localizedMessage
                    ?: "Failed to get speaker state",
                null
            )
        }
    }

    // -------------------------------------------------------------------------
    // Stream Name
    // -------------------------------------------------------------------------

    private fun streamName(
        stream: Int
    ): String {
        return when (stream) {

            AudioManager.STREAM_MUSIC ->
                "music"

            AudioManager.STREAM_RING ->
                "ring"

            AudioManager.STREAM_ALARM ->
                "alarm"

            AudioManager.STREAM_NOTIFICATION ->
                "notification"

            AudioManager.STREAM_SYSTEM ->
                "system"

            AudioManager.STREAM_VOICE_CALL ->
                "voiceCall"

            else ->
                "unknown"
        }
    }

    // -------------------------------------------------------------------------
    // Stream Type
    // -------------------------------------------------------------------------

    private fun getStreamType(
        name: String?
    ): Int? {
        return when (name) {

            "music" ->
                AudioManager.STREAM_MUSIC

            "ring" ->
                AudioManager.STREAM_RING

            "alarm" ->
                AudioManager.STREAM_ALARM

            "notification" ->
                AudioManager.STREAM_NOTIFICATION

            "system" ->
                AudioManager.STREAM_SYSTEM

            "voiceCall" ->
                AudioManager.STREAM_VOICE_CALL

            else ->
                null
        }
    }
}