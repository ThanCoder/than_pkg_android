package com.example.than_pkg_android

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.File

class AudioFileInfoHandler : PkgHandler() {

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

            "getAudioFileInfo" -> {
                getAudioFileInfo(
                    ctx,
                    call,
                    result
                )
            }

            "getAudioCoverArt" -> {
                getAudioCoverArt(
                    call,
                    result
                )
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    // -------------------------------------------------------------------------
    // Audio File Info
    // -------------------------------------------------------------------------

    private fun getAudioFileInfo(
        ctx: Context,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val path =
            call.argument<String>("path")

        if (path.isNullOrEmpty()) {
            result.error(
                "INVALID_PATH",
                "Missing audio file path",
                null
            )
            return
        }

        val file = File(path)

        if (!file.exists()) {
            result.error(
                "FILE_NOT_FOUND",
                "Audio file does not exist",
                null
            )
            return
        }

        if (!file.isFile) {
            result.error(
                "INVALID_FILE",
                "Path is not a file",
                null
            )
            return
        }

        val retriever =
            MediaMetadataRetriever()

        try {

            retriever.setDataSource(path)

            val duration =
                getLongMetadata(
                    retriever,
                    MediaMetadataRetriever.METADATA_KEY_DURATION
                )

            val bitrate =
                getLongMetadata(
                    retriever,
                    MediaMetadataRetriever.METADATA_KEY_BITRATE
                )

            val sampleRate =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    getLongMetadata(
                        retriever,
                        MediaMetadataRetriever.METADATA_KEY_SAMPLERATE
                    )
                } else {
                    null
                }

            val info =
                mutableMapOf<String, Any?>()

            // -----------------------------------------------------------------
            // File
            // -----------------------------------------------------------------

            info["path"] =
                file.absolutePath

            info["name"] =
                file.name

            info["extension"] =
                file.extension.ifEmpty { null }

            info["size"] =
                file.length()

            info["lastModified"] =
                file.lastModified()

            // -----------------------------------------------------------------
            // Media
            // -----------------------------------------------------------------

            info["duration"] =
                duration

            info["durationMs"] =
                duration

            info["bitrate"] =
                bitrate

            info["sampleRate"] =
                sampleRate

            info["mimeType"] =
                getMetadata(
                    retriever,
                    MediaMetadataRetriever.METADATA_KEY_MIMETYPE
                )

            info["hasAudio"] =
                getMetadata(
                    retriever,
                    MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO
                )

            info["hasVideo"] =
                getMetadata(
                    retriever,
                    MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO
                )

            // -----------------------------------------------------------------
            // Metadata
            // -----------------------------------------------------------------

            info["title"] =
                getMetadata(
                    retriever,
                    MediaMetadataRetriever.METADATA_KEY_TITLE
                )

            info["artist"] =
                getMetadata(
                    retriever,
                    MediaMetadataRetriever.METADATA_KEY_ARTIST
                )

            info["album"] =
                getMetadata(
                    retriever,
                    MediaMetadataRetriever.METADATA_KEY_ALBUM
                )

            info["albumArtist"] =
                getMetadata(
                    retriever,
                    MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST
                )

            info["composer"] =
                getMetadata(
                    retriever,
                    MediaMetadataRetriever.METADATA_KEY_COMPOSER
                )

            info["genre"] =
                getMetadata(
                    retriever,
                    MediaMetadataRetriever.METADATA_KEY_GENRE
                )

            info["year"] =
                getMetadata(
                    retriever,
                    MediaMetadataRetriever.METADATA_KEY_YEAR
                )

            info["date"] =
                getMetadata(
                    retriever,
                    MediaMetadataRetriever.METADATA_KEY_DATE
                )

            info["discNumber"] =
                getMetadata(
                    retriever,
                    MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER
                )

            info["trackNumber"] =
                getMetadata(
                    retriever,
                    MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER
                )

            result.success(info)

        } catch (e: Exception) {

            result.error(
                "AUDIO_INFO_ERROR",
                e.localizedMessage
                    ?: "Failed to read audio metadata",
                null
            )

        } finally {

            try {
                retriever.release()
            } catch (_: Exception) {
            }
        }
    }

    // -------------------------------------------------------------------------
    // Audio Cover Art
    // -------------------------------------------------------------------------

    private fun getAudioCoverArt(
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val path =
            call.argument<String>("path")

        val cachePath =
            call.argument<String>("cachePath")

        if (path.isNullOrEmpty()) {
            result.error(
                "INVALID_PATH",
                "Missing audio file path",
                null
            )
            return
        }

        if (cachePath.isNullOrEmpty()) {
            result.error(
                "INVALID_CACHE_PATH",
                "Missing cachePath",
                null
            )
            return
        }

        val audioFile =
            File(path)

        if (!audioFile.exists() || !audioFile.isFile) {
            result.error(
                "FILE_NOT_FOUND",
                "Audio file does not exist",
                null
            )
            return
        }

        val cacheDir =
            File(cachePath)

        val retriever =
            MediaMetadataRetriever()

        try {

            // -------------------------------------------------------------
            // Create cache directory
            // -------------------------------------------------------------

            if (!cacheDir.exists()) {
                if (!cacheDir.mkdirs()) {
                    result.error(
                        "CACHE_CREATE_FAILED",
                        "Could not create cache directory",
                        null
                    )
                    return
                }
            }

            // -------------------------------------------------------------
            // Read embedded artwork
            // -------------------------------------------------------------

            retriever.setDataSource(path)

            val artwork =
                retriever.embeddedPicture

            if (artwork == null || artwork.isEmpty()) {
                result.success(null)
                return
            }

            // -------------------------------------------------------------
            // Generate cache file name
            // -------------------------------------------------------------

            val fileName =
                "${audioFile.nameWithoutExtension}.jpg"

            val coverFile =
                File(
                    cacheDir,
                    fileName
                )

            // -------------------------------------------------------------
            // Write artwork
            // -------------------------------------------------------------

            coverFile.outputStream().use { output ->
                output.write(artwork)
                output.flush()
            }

            result.success(
                coverFile.absolutePath
            )

        } catch (e: Exception) {

            result.error(
                "COVER_ART_ERROR",
                e.localizedMessage
                    ?: "Failed to extract cover art",
                null
            )

        } finally {

            try {
                retriever.release()
            } catch (_: Exception) {
            }
        }
    }

    // -------------------------------------------------------------------------
    // Metadata Helper
    // -------------------------------------------------------------------------

    private fun getMetadata(
        retriever: MediaMetadataRetriever,
        key: Int
    ): String? {
        return try {
            retriever.extractMetadata(key)
        } catch (_: Exception) {
            null
        }
    }

    // -------------------------------------------------------------------------
    // Long Metadata Helper
    // -------------------------------------------------------------------------

    private fun getLongMetadata(
        retriever: MediaMetadataRetriever,
        key: Int
    ): Long? {
        return try {
            retriever
                .extractMetadata(key)
                ?.toLongOrNull()
        } catch (_: Exception) {
            null
        }
    }
}