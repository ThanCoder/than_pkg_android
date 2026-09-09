package com.example.than_pkg_android

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlin.concurrent.thread

class MediaFetchHandler(messenger: BinaryMessenger) : PkgHandler(), EventChannel.StreamHandler {

    private var eventSink: EventChannel.EventSink? = null
    private var contentObserver: ContentObserver? = null

    init {
        // Real-time Update အတွက် EventChannel ထောင်ခြင်း
        EventChannel(messenger, "than_pkg_android/media_stream").setStreamHandler(this)
    }

    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        val ctx = context ?: activity ?: run {
            result.error("NO_CONTEXT", "Context is not available", null)
            return
        }

        // 🌟 တကယ့် Main Thread ကြီး သက်သာသွားအောင် သီးသန့် Background Thread ဖွင့်လိုက်မယ်
        thread {
            try {
                val data = when (method) {
                    "fetchImages" -> fetchMedia(ctx, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaType.IMAGE)
                    "fetchVideos" -> fetchMedia(ctx, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaType.VIDEO)
                    "fetchAudio" -> fetchMedia(ctx, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaType.AUDIO)
                    else -> null
                }

                if (data != null) {
                    // 🚨 activity?.runOnUiThread { } ကို ဖြုတ်လိုက်တာ သတိပြုပါ!
                    // ရလာတဲ့ data list အကြီးကြီးကို binary format ပြောင်းတာကို Background Thread ပေါ်မှာပဲ လုပ်ခိုင်းပြီး
                    // Flutter ဆီ တိုက်ရိုက် Result လှမ်းပို့လိုက်မယ်။ ဒါမှ UI လုံးဝ မဆွဲထားတော့မှာ။
                    result.success(data)
                } else {
                    result.notImplemented()
                }
            } catch (e: Exception) {
                result.error("FETCH_ERROR", e.message, null)
            }
        }
    }

    enum class MediaType { IMAGE, VIDEO, AUDIO }

    private fun fetchMedia(context: Context, contentUri: Uri, type: MediaType): List<Map<String, Any>> {
        val mediaList = mutableListOf<Map<String, Any>>()
        val maxLimit = 500 // 🔥 ဒေတာဘေ့စ် ဝန်မပိအောင် တစ်ခါတောင်းရင် အပုဒ် ၅၀၀ ပဲ ကန့်သတ်မယ်

        // ဘာကော်လံတွေ ယူမလဲ သတ်မှတ်ခြင်း
        val projection = mutableListOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATA
        )

        // Video နဲ့ Audio အတွက် Duration ထပ်ထည့်မယ်
        if (type == MediaType.VIDEO || type == MediaType.AUDIO) {
            projection.add(MediaStore.MediaColumns.DURATION)
        }

        val contentResolver = context.contentResolver
        val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // ✨ Android 8.0+ အတွက် Modern Way (Bundle သုံးပြီး Limit ပတ်ခြင်း)
            val queryArgs = Bundle().apply {
                putInt(ContentResolver.QUERY_ARG_LIMIT, maxLimit)
                putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(MediaStore.MediaColumns.DATE_ADDED))
                putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING)
            }
            contentResolver.query(contentUri, projection.toTypedArray(), queryArgs, null)
        } else {
            // ✨ Android ဗားရှင်းအဟောင်းတွေအတွက် Legacy Way
            val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC LIMIT $maxLimit"
            contentResolver.query(contentUri, projection.toTypedArray(), null, null, sortOrder)
        }

        cursor?.use { cursorInstance ->
            val idCol = cursorInstance.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameCol = cursorInstance.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val sizeCol = cursorInstance.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val dataCol = cursorInstance.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

            val durationCol = if (type == MediaType.VIDEO || type == MediaType.AUDIO) {
                cursorInstance.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)
            } else -1

            while (cursorInstance.moveToNext()) {
                val id = cursorInstance.getLong(idCol)
                val name = cursorInstance.getString(nameCol) ?: ""
                val size = cursorInstance.getLong(sizeCol)
                val realPath = cursorInstance.getString(dataCol) ?: ""
                val duration = if (durationCol != -1) cursorInstance.getLong(durationCol) else 0L

                // content:// uri တည်ဆောက်ခြင်း
                val itemUri = ContentUris.withAppendedId(contentUri, id).toString()

                val mediaMap = mapOf(
                    "id" to id,
                    "name" to name,
                    "size" to size,
                    "uri" to itemUri,
                    "path" to realPath,
                    "duration" to duration
                )
                mediaList.add(mediaMap)
            }
        }
        return mediaList
    }

    // --- StreamHandler Callbacks (Flutter က Stream လာနားစွင့်တဲ့အချိန် အလုပ်လုပ်မည့်နေရာ) ---

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events
        val ctx = context ?: activity ?: return

        // DB ပြောင်းလဲမှုကို စောင့်ကြည့်မည့် Observer တည်ဆောက်ခြင်း
        contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)

                // Main Thread ကနေ Flutter ဆီ ပို့ဖို့အတွက် UI Thread ပေါ်တင်ပေးရမယ်
                activity?.runOnUiThread {
                    eventSink?.success(uri?.toString() ?: "changed")
                }
            }
        }

        // Media Store URIs တွေကို စောင့်ကြည့်ခိုင်းမယ်
        ctx.contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentObserver!!)
        ctx.contentResolver.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, contentObserver!!)
        ctx.contentResolver.registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, contentObserver!!)
    }

    override fun onCancel(arguments: Any?) {
        // Stream ပိတ်သွားရင် observer ကို ဖြုတ်ပစ်မယ် (Memory Leak မဖြစ်အောင်)
        val ctx = context ?: activity ?: return
        contentObserver?.let {
            ctx.contentResolver.unregisterContentObserver(it)
        }
        eventSink = null
        contentObserver = null
    }
}