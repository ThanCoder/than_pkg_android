package com.example.than_pkg_android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import androidx.core.net.toUri
import kotlin.concurrent.thread

class SafeStorageHandler : PkgHandler() {

    companion object {
        private const val REQUEST_FOLDER_PERMISSION = 2002
    }

    override fun handle(
        method: String,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val ctx = context ?: run {
            result.error(
                "NO_CONTEXT",
                "Android context is not available",
                null
            )
            return
        }

        when (method) {

            "requestFolderPermission" -> {
                requestFolderPermission(result)
            }

            "createFolder" -> {
                createFolder(ctx, call, result)
            }

            "checkFolderPermission" -> {
                checkFolderPermission(ctx, call, result)
            }

            "deleteItem" -> {
                deleteItem(ctx, call, result)
            }

            "listFiles" -> {
                listFiles(ctx, call, result)
            }

            "writeFileData" -> {
                writeFileData(ctx, call, result)
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    // -------------------------------------------------------------------------
    // Request folder permission
    // -------------------------------------------------------------------------

    private fun requestFolderPermission(
        result: MethodChannel.Result
    ) {
        val act = activity ?: run {
            result.error(
                "NO_ACTIVITY",
                "Activity is not available",
                null
            )
            return
        }

        pendingResult = result

        try {
            val intent = Intent(
                Intent.ACTION_OPEN_DOCUMENT_TREE
            ).apply {
                addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                            Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
                )
            }

            act.startActivityForResult(
                intent,
                REQUEST_FOLDER_PERMISSION
            )
        } catch (e: Exception) {
            pendingResult = null

            result.error(
                "FOLDER_PICKER_ERROR",
                e.localizedMessage
                    ?: "Failed to open folder picker",
                null
            )
        }
    }

    // -------------------------------------------------------------------------
    // Create folder
    // -------------------------------------------------------------------------

    private fun createFolder(
        ctx: Context,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val treeUriStr =
            call.argument<String>("treeUri")

        val folderName =
            call.argument<String>("folderName")

        val relativePath =
            call.argument<String>("relativePath")

        if (treeUriStr == null) {
            result.error(
                "INVALID_ARGS",
                "treeUri is required",
                null
            )
            return
        }

        if (folderName == null) {
            result.error(
                "INVALID_ARGS",
                "folderName is required",
                null
            )
            return
        }

        thread {
            try {
                val treeUri = treeUriStr.toUri()

                persistPermission(
                    ctx,
                    treeUri
                )

                val parentFolder =
                    resolveFolder(
                        ctx,
                        treeUri,
                        relativePath
                    )

                if (parentFolder == null) {
                    result.error(
                        "PARENT_NOT_FOUND",
                        "Could not resolve parent folder",
                        null
                    )
                    return@thread
                }

                val existing =
                    parentFolder.findFile(folderName)

                if (existing != null &&
                    existing.isDirectory
                ) {
                    result.success(
                        existing.uri.toString()
                    )
                    return@thread
                }

                val newFolder =
                    parentFolder.createDirectory(
                        folderName
                    )

                if (newFolder != null) {
                    result.success(
                        newFolder.uri.toString()
                    )
                } else {
                    result.error(
                        "CREATE_FAILED",
                        "Could not create folder",
                        null
                    )
                }

            } catch (e: Exception) {
                result.error(
                    "CREATE_FAILED",
                    e.localizedMessage
                        ?: "Failed to create folder",
                    null
                )
            }
        }
    }

    // -------------------------------------------------------------------------
    // Check permission
    // -------------------------------------------------------------------------

    private fun checkFolderPermission(
        ctx: Context,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val treeUriStr =
            call.argument<String>("treeUri")
                ?: run {
                    result.error(
                        "INVALID_ARGS",
                        "treeUri is required",
                        null
                    )
                    return
                }

        try {
            val treeUri = treeUriStr.toUri()

            val hasPermission =
                ctx.contentResolver
                    .persistedUriPermissions
                    .any {
                        it.uri == treeUri &&
                                it.isReadPermission &&
                                it.isWritePermission
                    }

            if (!hasPermission) {
                result.success(false)
                return
            }

            val folder =
                DocumentFile.fromTreeUri(
                    ctx,
                    treeUri
                )

            result.success(
                folder != null &&
                        folder.exists() &&
                        folder.canRead() &&
                        folder.canWrite()
            )

        } catch (_: Exception) {
            result.success(false)
        }
    }

    // -------------------------------------------------------------------------
    // Delete file / folder
    // -------------------------------------------------------------------------

    private fun deleteItem(
        ctx: Context,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val targetUriStr =
            call.argument<String>("targetUri")

        if (targetUriStr == null) {
            result.error(
                "INVALID_ARGS",
                "targetUri is required",
                null
            )
            return
        }

        thread {
            try {
                val uri = targetUriStr.toUri()

                val document =
                    DocumentFile.fromSingleUri(
                        ctx,
                        uri
                    )

                if (document == null) {
                    result.success(false)
                    return@thread
                }

                if (!document.exists()) {
                    result.success(false)
                    return@thread
                }

                result.success(
                    document.delete()
                )

            } catch (e: Exception) {
                result.error(
                    "DELETE_FAILED",
                    e.localizedMessage
                        ?: "Failed to delete item",
                    null
                )
            }
        }
    }

    // -------------------------------------------------------------------------
    // List files
    // -------------------------------------------------------------------------

    private fun listFiles(
        ctx: Context,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val treeUriStr =
            call.argument<String>("treeUri")

        val relativePath =
            call.argument<String>("relativePath")

        if (treeUriStr == null) {
            result.error(
                "INVALID_ARGS",
                "treeUri is required",
                null
            )
            return
        }

        thread {
            try {
                val treeUri =
                    treeUriStr.toUri()

                val folder =
                    resolveFolder(
                        ctx,
                        treeUri,
                        relativePath
                    )

                if (folder == null) {
                    result.error(
                        "FOLDER_NOT_FOUND",
                        "Folder could not be resolved",
                        null
                    )
                    return@thread
                }

                val fileList =
                    folder.listFiles()
                        .map {
                            mapOf(
                                "name" to (
                                        it.name ?: ""
                                        ),
                                "uri" to
                                        it.uri.toString(),
                                "isDirectory" to
                                        it.isDirectory,
                                "isFile" to
                                        it.isFile,
                                "size" to
                                        it.length(),
                                "mimeType" to
                                        it.type,
                                "canRead" to
                                        it.canRead(),
                                "canWrite" to
                                        it.canWrite(),
                                "exists" to
                                        it.exists()
                            )
                        }

                result.success(fileList)

            } catch (e: Exception) {
                result.error(
                    "LIST_FAILED",
                    e.localizedMessage
                        ?: "Failed to list files",
                    null
                )
            }
        }
    }

    // -------------------------------------------------------------------------
    // Write file
    // -------------------------------------------------------------------------

    private fun writeFileData(
        ctx: Context,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val parentUriStr =
            call.argument<String>("parentUri")

        val relativePath =
            call.argument<String>("relativePath")

        val fileName =
            call.argument<String>("fileName")

        val mimeType =
            call.argument<String>("mimeType")
                ?: "application/octet-stream"

        val bytes =
            call.argument<ByteArray>("bytes")

        if (parentUriStr == null) {
            result.error(
                "INVALID_ARGS",
                "parentUri is required",
                null
            )
            return
        }

        if (fileName == null) {
            result.error(
                "INVALID_ARGS",
                "fileName is required",
                null
            )
            return
        }

        if (bytes == null) {
            result.error(
                "INVALID_ARGS",
                "bytes is required",
                null
            )
            return
        }

        thread {
            try {
                val parentUri =
                    parentUriStr.toUri()

                val parentFolder =
                    resolveFolder(
                        ctx,
                        parentUri,
                        relativePath
                    )

                if (parentFolder == null) {
                    result.error(
                        "PARENT_NOT_FOUND",
                        "Parent folder could not be resolved",
                        null
                    )
                    return@thread
                }

                if (!parentFolder.canWrite()) {
                    result.error(
                        "NO_WRITE_PERMISSION",
                        "Folder is not writable",
                        null
                    )
                    return@thread
                }

                // Remove an existing file with the same name.
                val existing =
                    parentFolder.findFile(fileName)

                if (existing != null) {
                    existing.delete()
                }

                val newFile =
                    parentFolder.createFile(
                        mimeType,
                        fileName
                    )

                if (newFile == null) {
                    result.error(
                        "WRITE_FAILED",
                        "Could not create file",
                        null
                    )
                    return@thread
                }

                ctx.contentResolver
                    .openOutputStream(
                        newFile.uri
                    )
                    .use { output ->
                        if (output == null) {
                            throw IllegalStateException(
                                "Could not open output stream"
                            )
                        }

                        output.write(bytes)
                        output.flush()
                    }

                result.success(
                    newFile.uri.toString()
                )

            } catch (e: Exception) {
                result.error(
                    "WRITE_FAILED",
                    e.localizedMessage
                        ?: "Failed to write file",
                    null
                )
            }
        }
    }

    // -------------------------------------------------------------------------
    // Resolve child folder
    // -------------------------------------------------------------------------

    /**
     * Resolves a folder relative to the selected tree URI.
     *
     * Example:
     *
     * treeUri
     *   └── data
     *       └── books
     *           └── covers
     *
     * relativePath = "data/books/covers"
     */
    private fun resolveFolder(
        ctx: Context,
        treeUri: Uri,
        relativePath: String?
    ): DocumentFile? {

        var current =
            DocumentFile.fromTreeUri(
                ctx,
                treeUri
            ) ?: return null

        if (relativePath.isNullOrBlank()) {
            return current
        }

        val parts =
            relativePath
                .replace("\\", "/")
                .split("/")
                .filter {
                    it.isNotBlank() &&
                            it != "." &&
                            it != ".."
                }

        for (part in parts) {
            val child =
                current.findFile(part)
                    ?: return null

            if (!child.isDirectory) {
                return null
            }

            current = child
        }

        return current
    }

    // -------------------------------------------------------------------------
    // Activity result
    // -------------------------------------------------------------------------

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Boolean {

        if (
            requestCode !=
            REQUEST_FOLDER_PERMISSION
        ) {
            return false
        }

        val result = pendingResult
            ?: return true

        if (
            resultCode == Activity.RESULT_OK &&
            data?.data != null
        ) {
            val treeUri =
                data.data!!

            try {
                activity?.contentResolver
                    ?.takePersistableUriPermission(
                        treeUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
            } catch (e: Exception) {
                result.error(
                    "PERSIST_PERMISSION_FAILED",
                    e.localizedMessage
                        ?: "Could not persist folder permission",
                    null
                )

                pendingResult = null
                return true
            }

            result.success(
                treeUri.toString()
            )
        } else {
            result.success(null)
        }

        pendingResult = null

        return true
    }

    // -------------------------------------------------------------------------
    // Persist URI permission
    // -------------------------------------------------------------------------

    private fun persistPermission(
        ctx: Context,
        uri: Uri
    ) {
        try {
            ctx.contentResolver
                .takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
        } catch (_: Exception) {
            // Permission may already be persisted.
        }
    }
}