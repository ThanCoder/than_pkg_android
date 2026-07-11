package com.example.than_pkg_android

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Handler
import android.os.Looper
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class VideoHandler : PkgHandler() {

    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        when (method) {
            "getDuration" -> {
                getDuration(call, result)
            }
            "getThumbnail" -> {
                getThumbnailBytes(call, result)
            }
            "saveThumbnail" -> {
                saveThumbnailToFile(call, result)
            }
            else -> result.notImplemented()
        }
    }

    // ၁။ Video Duration ရှာပေးရန် (Millisecond ဖြင့် ပြန်ပေးမည်)
    private fun getDuration(call: MethodCall, result: MethodChannel.Result) {
        val path = call.argument<String>("path")
        if (path == null) {
            result.error("INVALID_ARGUMENT", "Path is null", null)
            return
        }

        // 🔥 Background Thread ပေါ်တင်မောင်းမယ်
        kotlin.concurrent.thread {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(path)
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val durationMs = time?.toLong() ?: 0L
                result.success(durationMs)
            } catch (e: Exception) {
                result.error("DURATION_ERROR", e.message, null)
            } finally {
                try {
                    retriever.release()
                } catch (e: Exception) {
                    // Older API compatibility
                }
            }
        }
    }
    // ၂။ Thumbnail ကို Uint8List (ByteArray) အနေနဲ့ Flutter ဆီ တိုက်ရိုက်ပြန်ပေးရန်
    private fun getThumbnailBytes(call: MethodCall, result: MethodChannel.Result) {
        val path = call.argument<String>("path")
        if (path == null) {
            result.error("INVALID_ARGUMENT", "Path is null", null)
            return
        }

        val timeUs = call.argument<Number>("time")?.toLong() ?: 0L
        val width = call.argument<Number>("width")?.toInt() ?: 0
        val height = call.argument<Number>("height")?.toInt() ?: 0
        val quality = call.argument<Number>("quality")?.toInt() ?: 80

        // 🔥 Background Thread ပေါ်တင်မောင်းမယ်
        kotlin.concurrent.thread {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(path)

                val bitmap = if (width > 0 && height > 0 && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
                    retriever.getScaledFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC, width, height)
                } else {
                    retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                }

                if (bitmap != null) {
                    val stream = java.io.ByteArrayOutputStream()
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, quality, stream)
                    val byteArray = stream.toByteArray()
                    result.success(byteArray)
                } else {
                    result.error("THUMBNAIL_FAILED", "Could not generate thumbnail bitmap", null)
                }
            } catch (e: Exception) {
                result.error("THUMBNAIL_ERROR", e.message, null)
            } finally {
                try {
                    retriever.release()
                } catch (e: Exception) {
                    // Older API compatibility
                }
            }
        }
    }

    private fun saveThumbnailToFile(call: MethodCall, result: MethodChannel.Result) {
        val videoPath = call.argument<String>("path")
        val savePath = call.argument<String>("savePath")
        if (videoPath == null || savePath == null) {
            result.error("INVALID_ARGUMENT", "Path or savePath is null", null)
            return
        }

        val timeUs = call.argument<Number>("time")?.toLong() ?: 0L
        val width = call.argument<Number>("width")?.toInt() ?: 0
        val height = call.argument<Number>("height")?.toInt() ?: 0
        val quality = call.argument<Number>("quality")?.toInt() ?: 80

        // 🔥 Background Thread ပေါ်တင်မောင်းမယ် (I/O နဲ့ Bitmap compression အတွက် အသက်ပဲ)
        kotlin.concurrent.thread {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(videoPath)

                val bitmap = if (width > 0 && height > 0 && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
                    retriever.getScaledFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC, width, height)
                } else {
                    retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                }

                if (bitmap != null) {
                    val file = java.io.File(savePath)
                    file.parentFile?.mkdirs()

                    val outStream = java.io.FileOutputStream(file)
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, quality, outStream)
                    outStream.flush()
                    outStream.close()
                    result.success(file.absolutePath)
                } else {
                    result.error("SAVE_FAILED", "Bitmap is null", null)
                }
            } catch (e: Exception) {
                result.error("SAVE_ERROR", e.message, null)
            } finally {
                try {
                    retriever.release()
                } catch (e: Exception) {
                    // Older API compatibility
                }
            }
        }
    }

    // ဗီဒီယိုရဲ့ သတ်မှတ်စက္ကန့်နေရာကနေ Thumbnail ကို Byte အနေနဲ့ ထုတ်ပေးမယ့်ကောင်
    private fun getVideoThumbnail(ctx: Context, call: MethodCall, result: MethodChannel.Result) {
        val videoPath = call.argument<String>("videoPath")
        val timeInUs = call.argument<Long>("timeInUs") ?: 1000000L // Default 1 စက္ကန့်နေရာကပုံကို ယူမယ်

        if (videoPath == null) {
            Handler(Looper.getMainLooper()).post {
                result.error("INVALID_ARGUMENTS", "videoPath is required", null)
            }
            return
        }

        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoPath)
            // သတ်မှတ်ထားတဲ့ Microsecond နေရာက Frame ကို ဆွဲထုတ်မယ်
            val bitmap = retriever.getFrameAtTime(timeInUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)

            if (bitmap != null) {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream) // Memory သက်သာအောင် quality 80 ပဲထားမယ်
                val byteArray = stream.toByteArray()

                Handler(Looper.getMainLooper()).post {
                    result.success(byteArray)
                }
            } else {
                Handler(Looper.getMainLooper()).post { result.success(null) }
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                result.error("THUMBNAIL_FAILED", e.localizedMessage, null)
            }
        } finally {
            try { retriever.release() } catch (e: Exception) {}
        }
    }

    // ဗီဒီယိုရဲ့ Duration နဲ့ Size စတဲ့ Metadata တွေ ဖတ်ပေးမယ့်ကောင်
    private fun getVideoInfo(ctx: Context, call: MethodCall, result: MethodChannel.Result) {
        val videoPath = call.argument<String>("videoPath")
        if (videoPath == null) {
            Handler(Looper.getMainLooper()).post {
                result.error("INVALID_ARGUMENTS", "videoPath is required", null)
            }
            return
        }

        val file = File(videoPath)
        if (!file.exists()) {
            Handler(Looper.getMainLooper()).post { result.error("NOT_FOUND", "Video file not found", null) }
            return
        }

        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoPath)
            val info = mutableMapOf<String, Any?>()

            info["DURATION"] = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            info["WIDTH"] = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            info["HEIGHT"] = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            info["ROTATION"] = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION) ?: "0"
            info["SIZE"] = file.length()

            Handler(Looper.getMainLooper()).post {
                result.success(info)
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                result.error("GET_INFO_FAILED", e.localizedMessage, null)
            }
        } finally {
            try { retriever.release() } catch (e: Exception) {}
        }
    }
}