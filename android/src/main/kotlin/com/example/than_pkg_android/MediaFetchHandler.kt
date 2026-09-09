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

class MediaFetchHandler(messenger: BinaryMessenger) :
PkgHandler(),
EventChannel.StreamHandler {


private var eventSink: EventChannel.EventSink? = null
private var contentObserver: ContentObserver? = null

init {
    EventChannel(
        messenger,
        "than_pkg_android/media_stream",
    ).setStreamHandler(this)
}

override fun handle(
    method: String,
    call: MethodCall,
    result: MethodChannel.Result,
) {
    val ctx = context ?: activity ?: run {
        result.error(
            "NO_CONTEXT",
            "Context is not available",
            null,
        )
        return
    }

    thread {
        try {
            val data = when (method) {
                "fetchImages" -> fetchMedia(
                    ctx,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaType.IMAGE,
                )

                "fetchVideos" -> fetchMedia(
                    ctx,
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    MediaType.VIDEO,
                )

                "fetchAudio" -> fetchMedia(
                    ctx,
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    MediaType.AUDIO,
                )

                else -> null
            }

            if (data != null) {
                result.success(data)
            } else {
                result.notImplemented()
            }
        } catch (e: Exception) {
            result.error(
                "FETCH_ERROR",
                e.message,
                null,
            )
        }
    }
}

enum class MediaType {
    IMAGE,
    VIDEO,
    AUDIO,
}

private fun fetchMedia(
    context: Context,
    contentUri: Uri,
    type: MediaType,
): List<Map<String, Any>> {

    val mediaList = mutableListOf<Map<String, Any>>()

    val maxLimit = 500

    // =========================
    // Projection
    // =========================

    val projection = mutableListOf(
        MediaStore.MediaColumns._ID,
        MediaStore.MediaColumns.DISPLAY_NAME,
        MediaStore.MediaColumns.SIZE,

        // Deprecated on Android 10+ ဖြစ်ပေမယ့်
        // backward compatibility အတွက် ယာယီထားထားသည်။
        MediaStore.MediaColumns.DATA,

        // Unix timestamp (seconds)
        MediaStore.MediaColumns.DATE_ADDED,
        MediaStore.MediaColumns.DATE_MODIFIED,
    )

    // Video / Audio Duration
    if (
        type == MediaType.VIDEO ||
        type == MediaType.AUDIO
    ) {
        projection.add(
            MediaStore.MediaColumns.DURATION,
        )
    }

    // Image / Video ရိုက်ခဲ့သော date
    if (
        type == MediaType.IMAGE ||
        type == MediaType.VIDEO
    ) {
        projection.add(
            MediaStore.MediaColumns.DATE_TAKEN,
        )
    }

    val contentResolver = context.contentResolver

    val cursor =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val queryArgs = Bundle().apply {

                putInt(
                    ContentResolver.QUERY_ARG_LIMIT,
                    maxLimit,
                )

                putStringArray(
                    ContentResolver.QUERY_ARG_SORT_COLUMNS,
                    arrayOf(
                        MediaStore.MediaColumns.DATE_ADDED,
                    ),
                )

                putInt(
                    ContentResolver.QUERY_ARG_SORT_DIRECTION,
                    ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
                )
            }

            contentResolver.query(
                contentUri,
                projection.toTypedArray(),
                queryArgs,
                null,
            )
        } else {

            val sortOrder =
                "${MediaStore.MediaColumns.DATE_ADDED} DESC LIMIT $maxLimit"

            contentResolver.query(
                contentUri,
                projection.toTypedArray(),
                null,
                null,
                sortOrder,
            )
        }

    cursor?.use { cursorInstance ->

        // =========================
        // Basic Columns
        // =========================

        val idCol =
            cursorInstance.getColumnIndexOrThrow(
                MediaStore.MediaColumns._ID,
            )

        val nameCol =
            cursorInstance.getColumnIndexOrThrow(
                MediaStore.MediaColumns.DISPLAY_NAME,
            )

        val sizeCol =
            cursorInstance.getColumnIndexOrThrow(
                MediaStore.MediaColumns.SIZE,
            )

        val dataCol =
            cursorInstance.getColumnIndexOrThrow(
                MediaStore.MediaColumns.DATA,
            )

        // =========================
        // Date Columns
        // =========================

        val dateAddedCol =
            cursorInstance.getColumnIndexOrThrow(
                MediaStore.MediaColumns.DATE_ADDED,
            )

        val dateModifiedCol =
            cursorInstance.getColumnIndexOrThrow(
                MediaStore.MediaColumns.DATE_MODIFIED,
            )

        // =========================
        // Duration
        // =========================

        val durationCol =
            if (
                type == MediaType.VIDEO ||
                type == MediaType.AUDIO
            ) {
                cursorInstance.getColumnIndexOrThrow(
                    MediaStore.MediaColumns.DURATION,
                )
            } else {
                -1
            }

        // =========================
        // Date Taken
        // =========================

        val dateTakenCol =
            if (
                type == MediaType.IMAGE ||
                type == MediaType.VIDEO
            ) {
                cursorInstance.getColumnIndexOrThrow(
                    MediaStore.MediaColumns.DATE_TAKEN,
                )
            } else {
                -1
            }

        // =========================
        // Read Media
        // =========================

        while (cursorInstance.moveToNext()) {

            val id =
                cursorInstance.getLong(idCol)

            val name =
                cursorInstance.getString(nameCol)
                    ?: ""

            val size =
                cursorInstance.getLong(sizeCol)

            val realPath =
                cursorInstance.getString(dataCol)
                    ?: ""

            val duration =
                if (durationCol != -1) {
                    cursorInstance.getLong(durationCol)
                } else {
                    0L
                }

            // DATE_ADDED:
            // Unix timestamp in seconds
            val dateAdded =
                cursorInstance.getLong(dateAddedCol)

            // DATE_MODIFIED:
            // Unix timestamp in seconds
            val dateModified =
                cursorInstance.getLong(dateModifiedCol)

            // DATE_TAKEN:
            // Unix timestamp in milliseconds
            val dateTaken =
                if (dateTakenCol != -1) {
                    cursorInstance.getLong(dateTakenCol)
                } else {
                    0L
                }

            // =========================
            // content:// URI
            // =========================

            val itemUri =
                ContentUris.withAppendedId(
                    contentUri,
                    id,
                ).toString()

            // =========================
            // Result
            // =========================

            val mediaMap = mapOf(
                "id" to id,
                "name" to name,
                "size" to size,
                "uri" to itemUri,
                "path" to realPath,

                // Duration in milliseconds
                "duration" to duration,

                // Unix timestamp in seconds
                "dateAdded" to dateAdded,

                // Unix timestamp in seconds
                "dateModified" to dateModified,

                // Unix timestamp in milliseconds
                // Image / Video only
                "dateTaken" to dateTaken,
            )

            mediaList.add(mediaMap)
        }
    }

    return mediaList
}

// =========================
// Event Stream
// =========================

override fun onListen(
    arguments: Any?,
    events: EventChannel.EventSink?,
) {

    eventSink = events

    val ctx =
        context ?: activity ?: return

    contentObserver =
        object :
            ContentObserver(
                Handler(
                    Looper.getMainLooper(),
                ),
            ) {

            override fun onChange(
                selfChange: Boolean,
                uri: Uri?,
            ) {

                super.onChange(
                    selfChange,
                    uri,
                )

                activity?.runOnUiThread {

                    eventSink?.success(
                        uri?.toString()
                            ?: "changed",
                    )
                }
            }
        }

    ctx.contentResolver.registerContentObserver(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        true,
        contentObserver!!,
    )

    ctx.contentResolver.registerContentObserver(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        true,
        contentObserver!!,
    )

    ctx.contentResolver.registerContentObserver(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        true,
        contentObserver!!,
    )
}

override fun onCancel(
    arguments: Any?,
) {

    val ctx =
        context ?: activity ?: return

    contentObserver?.let {
        ctx.contentResolver.unregisterContentObserver(
            it,
        )
    }

    eventSink = null
    contentObserver = null
}


}
