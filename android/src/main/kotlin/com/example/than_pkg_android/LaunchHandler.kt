package com.example.than_pkg_android

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.File

class LaunchHandler : PkgHandler() {

    override fun handle(
        method: String,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val ctx = context ?: run {
            result.error(
                "NO_CONTEXT",
                "Android context is null",
                null
            )
            return
        }

        when (method) {

            // -----------------------------------------------------------------
            // External Browser
            // -----------------------------------------------------------------

            "openExternalBrowser",
            "launchUrl" -> {
                openExternalBrowser(
                    ctx,
                    call,
                    result
                )
            }

            // -----------------------------------------------------------------
            // Custom Tab
            // -----------------------------------------------------------------

            "openCustomTab" -> {
                openCustomTab(
                    ctx,
                    call,
                    result
                )
            }

            // -----------------------------------------------------------------
            // File
            // -----------------------------------------------------------------

            "launchFile" -> {
                launchFile(
                    ctx,
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
    // External Browser
    // -------------------------------------------------------------------------

    private fun openExternalBrowser(
        ctx: android.content.Context,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val url =
            call.argument<String>("url")

        if (url.isNullOrBlank()) {
            result.error(
                "INVALID_URL",
                "URL is empty",
                null
            )
            return
        }

        try {

            val intent =
                Intent(
                    Intent.ACTION_VIEW,
                    url.toUri()
                ).apply {
                    addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                }

            ctx.startActivity(intent)

            result.success(true)

        } catch (e: ActivityNotFoundException) {

            result.error(
                "NO_BROWSER",
                "No application can open this URL",
                null
            )

        } catch (e: Exception) {

            result.error(
                "CANNOT_OPEN_BROWSER",
                e.localizedMessage
                    ?: "Failed to open URL",
                null
            )
        }
    }

    // -------------------------------------------------------------------------
    // Custom Tab
    // -------------------------------------------------------------------------

    private fun openCustomTab(
        ctx: Context,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val url = call.argument<String>("url")

        if (url.isNullOrBlank()) {
            result.error(
                "INVALID_URL",
                "URL is empty",
                null
            )
            return
        }

        try {
            val uri = url.toUri()

            val intent = Intent(
                Intent.ACTION_VIEW,
                uri
            ).apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )

                // Custom Tabs extras
                putExtra(
                    "android.support.customtabs.extra.SESSION",
                    null as String?
                )

                putExtra(
                    "android.support.customtabs.extra.TITLE_VISIBILITY",
                    1
                )
            }

            ctx.startActivity(intent)

            result.success(true)

        } catch (e: ActivityNotFoundException) {

            result.error(
                "NO_BROWSER",
                "No browser application found",
                null
            )

        } catch (e: Exception) {

            result.error(
                "CANNOT_OPEN_CUSTOM_TAB",
                e.localizedMessage
                    ?: "Failed to open URL",
                null
            )
        }
    }
    // -------------------------------------------------------------------------
    // Launch File
    // -------------------------------------------------------------------------

    private fun launchFile(
        ctx: android.content.Context,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val filePath =
            call.argument<String>("filePath")

        if (filePath.isNullOrBlank()) {
            result.error(
                "INVALID_PATH",
                "File path is empty",
                null
            )
            return
        }

        val file =
            File(filePath)

        if (!file.exists()) {
            result.error(
                "FILE_NOT_FOUND",
                "File does not exist at: $filePath",
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

        try {

            val extension =
                file.extension.lowercase()

            val mimeType =
                MimeTypeMap
                    .getSingleton()
                    .getMimeTypeFromExtension(
                        extension
                    )
                    ?: "*/*"

            val fileUri =
                FileProvider.getUriForFile(
                    ctx,
                    "${ctx.packageName}.file_provider",
                    file
                )

            val intent =
                Intent(
                    Intent.ACTION_VIEW
                ).apply {

                    setDataAndType(
                        fileUri,
                        mimeType
                    )

                    addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    )

                    addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                    addFlags(
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }

            /*
             * Some Android devices/apps require explicit URI permission.
             */
            val resolvedApps =
                ctx.packageManager
                    .queryIntentActivities(
                        intent,
                        0
                    )

            if (resolvedApps.isEmpty()) {
                result.error(
                    "NO_APP_FOUND",
                    "No application can open this file type: $mimeType",
                    null
                )
                return
            }

            for (resolveInfo in resolvedApps) {

                ctx.grantUriPermission(
                    resolveInfo.activityInfo.packageName,
                    fileUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            ctx.startActivity(intent)

            result.success(true)

        } catch (e: IllegalArgumentException) {

            result.error(
                "FILE_PROVIDER_ERROR",
                e.localizedMessage
                    ?: "FileProvider configuration error",
                null
            )

        } catch (e: ActivityNotFoundException) {

            result.error(
                "NO_APP_FOUND",
                "No application can open this file",
                null
            )

        } catch (e: Exception) {

            result.error(
                "CANNOT_LAUNCH_FILE",
                e.localizedMessage
                    ?: "Failed to launch file",
                null
            )
        }
    }
}