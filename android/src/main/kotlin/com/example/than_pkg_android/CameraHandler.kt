package com.example.than_pkg_android

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraHandler : PkgHandler(),
    PluginRegistry.ActivityResultListener {

    companion object {
        private const val REQUEST_TAKE_PICTURE = 101
    }

    private var currentPhotoPath: String? = null

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

            "toggleTorch" -> {
                toggleTorch(
                    ctx,
                    call,
                    result
                )
            }

            "hasFlashlight" -> {
                hasFlashlight(
                    ctx,
                    result
                )
            }

            "takePicture" -> {
                takePicture(result)
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    // -------------------------------------------------------------------------
    // Torch
    // -------------------------------------------------------------------------

    private fun toggleTorch(
        ctx: Context,
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val enable = call.argument<Boolean>("enable")

        if (enable == null) {
            result.error(
                "INVALID_ARGUMENT",
                "Missing 'enable' argument",
                null
            )
            return
        }

        /*
         * setTorchMode() requires CAMERA permission.
         */
        if (
            ContextCompat.checkSelfPermission(
                ctx,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            result.error(
                "CAMERA_PERMISSION_DENIED",
                "Camera permission is required to control flashlight",
                null
            )
            return
        }

        val cameraManager =
            ctx.getSystemService(
                Context.CAMERA_SERVICE
            ) as? CameraManager

        if (cameraManager == null) {
            result.error(
                "CAMERA_SERVICE_NOT_AVAILABLE",
                "Camera service is not available",
                null
            )
            return
        }

        try {

            val cameraId =
                findTorchCamera(cameraManager)

            if (cameraId == null) {
                result.error(
                    "NO_FLASH",
                    "No camera with flashlight was found",
                    null
                )
                return
            }

            cameraManager.setTorchMode(
                cameraId,
                enable
            )

            result.success(true)

        } catch (e: SecurityException) {

            result.error(
                "CAMERA_PERMISSION_DENIED",
                e.localizedMessage
                    ?: "Camera permission denied",
                null
            )

        } catch (e: Exception) {

            result.error(
                "TORCH_ERROR",
                e.localizedMessage
                    ?: "Failed to control flashlight",
                null
            )
        }
    }

    /**
     * Find the camera that actually provides a flash.
     */
    private fun findTorchCamera(
        cameraManager: CameraManager
    ): String? {

        try {

            for (cameraId in cameraManager.cameraIdList) {

                try {

                    val characteristics =
                        cameraManager.getCameraCharacteristics(
                            cameraId
                        )

                    val flashAvailable =
                        characteristics.get(
                            CameraCharacteristics.FLASH_INFO_AVAILABLE
                        ) == true

                    if (flashAvailable) {
                        return cameraId
                    }

                } catch (_: Exception) {
                    // Ignore this camera and continue.
                }
            }

        } catch (_: Exception) {
            return null
        }

        return null
    }

    // -------------------------------------------------------------------------
    // Has Flashlight
    // -------------------------------------------------------------------------

    private fun hasFlashlight(
        ctx: Context,
        result: MethodChannel.Result
    ) {
        try {
            val cameraManager =
                ctx.getSystemService(
                    Context.CAMERA_SERVICE
                ) as? CameraManager

            if (cameraManager == null) {
                result.success(false)
                return
            }

            val hasFlash = cameraManager.cameraIdList.any { cameraId ->
                try {
                    val characteristics =
                        cameraManager.getCameraCharacteristics(
                            cameraId
                        )

                    characteristics.get(
                        CameraCharacteristics.FLASH_INFO_AVAILABLE
                    ) == true

                } catch (_: Exception) {
                    false
                }
            }

            result.success(hasFlash)

        } catch (e: Exception) {
            result.error(
                "FLASH_CHECK_ERROR",
                e.localizedMessage
                    ?: "Failed to check flashlight",
                null
            )
        }
    }

    // -------------------------------------------------------------------------
    // Take Picture
    // -------------------------------------------------------------------------

    private fun takePicture(
        result: MethodChannel.Result
    ) {
        val currentActivity = activity

        if (currentActivity == null) {
            result.error(
                "NO_ACTIVITY",
                "Activity is not available",
                null
            )
            return
        }

        /*
         * Prevent multiple camera requests.
         */
        if (pendingResult != null) {
            result.error(
                "CAMERA_BUSY",
                "A camera request is already in progress",
                null
            )
            return
        }

        val intent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (
            intent.resolveActivity(
                currentActivity.packageManager
            ) == null
        ) {
            result.error(
                "NO_CAMERA_APP",
                "No camera application found",
                null
            )
            return
        }

        val photoFile: File

        try {

            photoFile =
                createImageFile(currentActivity)

        } catch (e: Exception) {

            result.error(
                "FILE_CREATE_FAILED",
                e.localizedMessage
                    ?: "Could not create image file",
                null
            )
            return
        }

        currentPhotoPath =
            photoFile.absolutePath

        val photoUri: Uri

        try {

            photoUri =
                FileProvider.getUriForFile(
                    currentActivity,
                    "${currentActivity.packageName}.file_provider",
                    photoFile
                )

        } catch (e: IllegalArgumentException) {

            currentPhotoPath = null

            result.error(
                "MISSING_FILE_PROVIDER",
                "FileProvider configuration is missing. " +
                        "Check AndroidManifest.xml and file_paths.xml. " +
                        "Original error: ${e.localizedMessage}",
                null
            )

            return
        }

        intent.apply {

            putExtra(
                MediaStore.EXTRA_OUTPUT,
                photoUri
            )

            addFlags(
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        pendingResult = result

        try {

            currentActivity.startActivityForResult(
                intent,
                REQUEST_TAKE_PICTURE
            )

        } catch (e: Exception) {

            pendingResult = null
            deleteCurrentPhoto()
            currentPhotoPath = null

            result.error(
                "CAMERA_LAUNCH_FAILED",
                e.localizedMessage
                    ?: "Failed to launch camera",
                null
            )
        }
    }

    // -------------------------------------------------------------------------
    // Create Image File
    // -------------------------------------------------------------------------

    private fun createImageFile(
        context: Context
    ): File {

        val timestamp =
            SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())

        return File.createTempFile(
            "JPEG_${timestamp}_",
            ".jpg",
            context.cacheDir
        )
    }

    // -------------------------------------------------------------------------
    // Activity Result
    // -------------------------------------------------------------------------

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Boolean {

        if (
            requestCode != REQUEST_TAKE_PICTURE
        ) {
            return false
        }

        val result = pendingResult

        /*
         * Clear first so callback cannot accidentally be called twice.
         */
        pendingResult = null

        if (resultCode == Activity.RESULT_OK) {

            val path = currentPhotoPath

            if (
                path != null &&
                File(path).exists()
            ) {

                result?.success(path)

            } else {

                result?.error(
                    "PHOTO_NOT_FOUND",
                    "Camera returned successfully but image file was not found",
                    null
                )
            }

        } else {

            /*
             * User cancelled the camera.
             */
            deleteCurrentPhoto()

            result?.success(null)
        }

        currentPhotoPath = null

        return true
    }

    // -------------------------------------------------------------------------
    // Cleanup
    // -------------------------------------------------------------------------

    private fun deleteCurrentPhoto() {
        val path = currentPhotoPath ?: return

        try {
            File(path).delete()
        } catch (_: Exception) {
        }
    }

    override fun onDetachedFromActivity() {

        /*
         * Camera Activity is no longer available.
         */
        pendingResult?.error(
            "ACTIVITY_DETACHED",
            "Activity was detached while camera operation was in progress",
            null
        )

        pendingResult = null

        deleteCurrentPhoto()
        currentPhotoPath = null

        super.onDetachedFromActivity()
    }
}