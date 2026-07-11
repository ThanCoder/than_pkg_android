package com.example.than_pkg_android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.ByteArrayOutputStream
import kotlin.concurrent.thread
import androidx.core.net.toUri
import androidx.core.graphics.createBitmap
import java.io.File
import java.io.FileOutputStream

class PdfHandler : PkgHandler() {
    override fun handle(
        method: String,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val ctx = context ?: run {
            result.error("NO_CONTEXT", "Context is not available", null)
            return
        }

        // 🌟 ဒီနေရာကနေ Thread စခွဲလိုက်ရင် အောက်က function အကုန်လုံး Safe ဖြစ်သွားမယ်
        thread {
            when (method) {
                "saveToThumbnail" -> saveToThumbnail(ctx, call, result)
                "getPage" -> getPage(ctx, call, result)
                "getPageCount" -> getPageCount(ctx, call, result)
                else -> result.notImplemented()
            }
        }
    }

    // 🔥 အတွင်းထဲက thread { } တွေကို ဖြုတ်လိုက်ပါပြီ
    private fun saveToThumbnail(ctx: Context, call: MethodCall, result: MethodChannel.Result) {
        val pdfUriStr = call.argument<String>("pdfUri")
        val targetPath = call.argument<String>("targetPath")
        val width = call.argument<Int>("width")
        val height = call.argument<Int>("height")
        val pageIndex = call.argument<Int>("pageIndex") ?: 0
        val format = call.argument<String>("format") ?: "png"

        if (pdfUriStr == null || targetPath == null) {
            result.error("INVALID_ARGUMENTS", "pdfUri and targetPath are required", null)
            return
        }

        try {
            val bitmap = renderPdfToBitmap(ctx, pdfUriStr, pageIndex, width, height)
            if (bitmap == null) {
                result.success(false)
                return
            }

            val file = File(targetPath)
            file.parentFile?.mkdirs()

            FileOutputStream(file).use { out ->
                val compressFormat =
                    if (format.lowercase() == "jpg" || format.lowercase() == "jpeg") {
                        Bitmap.CompressFormat.JPEG
                    } else {
                        Bitmap.CompressFormat.PNG
                    }
                bitmap.compress(compressFormat, 100, out)
            }
            result.success(true)
        } catch (e: Exception) {
            result.error("SAVE_FAILED", e.localizedMessage, null)
        }
    }

    private fun getPage(ctx: Context, call: MethodCall, result: MethodChannel.Result) {
        val pdfUriStr = call.argument<String>("pdfUri")
        val width = call.argument<Int>("width")
        val height = call.argument<Int>("height")
        val pageIndex = call.argument<Int>("pageIndex") ?: 0
        val format = call.argument<String>("format") ?: "png"

        if (pdfUriStr == null) {
            result.error("INVALID_ARGUMENTS", "pdfUri is required", null)
            return
        }

        try {
            val bitmap = renderPdfToBitmap(ctx, pdfUriStr, pageIndex, width, height)
            if (bitmap == null) {
                result.success(null)
                return
            }

            val stream = ByteArrayOutputStream()
            val compressFormat =
                if (format.lowercase() == "jpg" || format.lowercase() == "jpeg") {
                    Bitmap.CompressFormat.JPEG
                } else {
                    Bitmap.CompressFormat.PNG
                }
            bitmap.compress(compressFormat, 100, stream)

            result.success(stream.toByteArray())
        } catch (e: Exception) {
            result.error("GET_PAGE_FAILED", e.localizedMessage, null)
        }
    }

    private fun getPageCount(ctx: Context, call: MethodCall, result: MethodChannel.Result) {
        val pdfUriStr = call.argument<String>("pdfUri") ?: return result.error(
            "INVALID_ARGUMENTS",
            "pdfUri is required",
            null
        )

        try {
            openFileDescriptor(ctx, pdfUriStr).use { fd ->
                if (fd != null) {
                    val renderer = PdfRenderer(fd)
                    val count = renderer.pageCount
                    renderer.close()
                    result.success(count)
                } else {
                    result.success(0)
                }
            }
        } catch (e: Exception) {
            result.error("COUNT_FAILED", e.localizedMessage, null)
        }
    }

    private fun openFileDescriptor(ctx: Context, pathOrUri: String): ParcelFileDescriptor? {
        return if (pathOrUri.startsWith("content://") || pathOrUri.startsWith("file://")) {
            ctx.contentResolver.openFileDescriptor(pathOrUri.toUri(), "r")
        } else {
            val file = File(pathOrUri)
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        }
    }

    private fun renderPdfToBitmap(
        ctx: Context,
        pdfUriStr: String,
        pageIndex: Int,
        targetWidth: Int?,
        targetHeight: Int?
    ): Bitmap? {
        var fileDescriptor: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        var page: PdfRenderer.Page? = null

        try {
            fileDescriptor = openFileDescriptor(ctx, pdfUriStr) ?: return null
            renderer = PdfRenderer(fileDescriptor)

            if (pageIndex < 0 || pageIndex >= renderer.pageCount) return null

            page = renderer.openPage(pageIndex)

            val width = targetWidth ?: page.width
            val height = targetHeight ?: page.height

            val bitmap = createBitmap(width, height)
            bitmap.eraseColor(Color.WHITE)

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            return bitmap
        } finally {
            try { page?.close() } catch (e: Exception) {}
            try { renderer?.close() } catch (e: Exception) {}
            try { fileDescriptor?.close() } catch (e: Exception) {}
        }
    }
}