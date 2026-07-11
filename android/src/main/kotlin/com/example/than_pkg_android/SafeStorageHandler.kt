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

    override fun handle(method: String, call: MethodCall, result: MethodChannel.Result) {
        val act = activity ?: run {
            result.error("NO_ACTIVITY", "Activity is not available", null)
            return
        }

        when (method) {
            // ၁။ User ဆီကနေ သတ်မှတ်ထားတဲ့ Folder ဝင်ရောက်ခွင့် Permission တောင်းခြင်း
            "requestFolderPermission" -> {
                this.pendingResult = result
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                    // လုံခြုံရေးအရ Permission ကို အမြဲသိမ်းထားခွင့် ပေးခြင်း
                    addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    )
                }
                act.startActivityForResult(intent, 2002)
            }

            // ၂။ ခွင့်ပြုချက်ရထားတဲ့ Folder ထဲမှာ Folder အသစ်ဆောက်ခြင်း
            "createFolder" -> {
                val treeUriStr = call.argument<String>("treeUri")
                val folderName = call.argument<String>("folderName")

                if (treeUriStr == null || folderName == null) {
                    return result.error("INVALID_ARGS", "treeUri and folderName are required", null)
                }

                val treeUri = treeUriStr.toUri()
                // Permission ကို ဆက်ထိန်းထားရန်
                persistPermission(act, treeUri)

                val rootFolder = DocumentFile.fromTreeUri(act, treeUri)
                val newFolder = rootFolder?.createDirectory(folderName)

                if (newFolder != null) {
                    result.success(newFolder.uri.toString()) // ဆောက်ပြီးသား Folder ရဲ့ Uri ကို ပြန်ပေးမယ်
                } else {
                    result.error("CREATE_FAILED", "Could not create folder", null)
                }
            }

            // ၃။ ဖိုင် သို့မဟုတ် Folder တစ်ခုခုကို ဖျက်ခြင်း (Delete)
            "deleteItem" -> {
                val targetUriStr = call.argument<String>("targetUri") ?: return result.error(
                    "INVALID_ARGS", "targetUri is required", null
                )

                val targetUri = targetUriStr.toUri()
                val fileDoc =
                    DocumentFile.fromSingleUri(act, targetUri) ?: DocumentFile.fromTreeUri(
                        act, targetUri
                    )

                if (fileDoc != null && fileDoc.exists()) {
                    val deleted = fileDoc.delete()
                    result.success(deleted)
                } else {
                    result.success(false)
                }
            }
            // ... class ထဲက when (method) အောက်မှာ ထည့်ရန် ...
            "listFiles" -> {
                val treeUriStr = call.argument<String>("treeUri") ?: return result.error(
                    "INVALID_ARGS", "treeUri is required", null
                )

                thread {
                    try {
                        val treeUri = treeUriStr.toUri()
                        val rootFolder = DocumentFile.fromTreeUri(context!!, treeUri)
                        val fileList = mutableListOf<Map<String, Any>>()

                        rootFolder?.listFiles()?.forEach { file ->
                            val fileInfo = mapOf(
                                "name" to (file.name ?: ""),
                                "uri" to file.uri.toString(),
                                "isDirectory" to file.isDirectory,
                                "size" to file.length()
                            )
                            fileList.add(fileInfo)
                        }
                        result.success(fileList) // ဖိုင်စာရင်းကို List ပုံစံနဲ့ Flutter ဘက် ပြန်ပို့မယ်
                    } catch (e: Exception) {
                        result.error("LIST_FAILED", e.localizedMessage, null)
                    }
                }
            }

            "writeFileData" -> {
                val parentUriStr = call.argument<String>("parentUri") // ရထားတဲ့ Folder Uri
                val fileName = call.argument<String>("fileName")
                val mimeType = call.argument<String>("mimeType") ?: "application/octet-stream"
                val bytes = call.argument<ByteArray>("bytes")

                if (parentUriStr == null || fileName == null || bytes == null) {
                    return result.error(
                        "INVALID_ARGS", "parentUri, fileName and bytes are required", null
                    )
                }

                thread {
                    try {
                        val parentUri = parentUriStr.toUri()
                        val parentFolder = DocumentFile.fromTreeUri(context!!, parentUri)

                        // ဖိုင်အသစ်ဆောက်မယ် (ရှိပြီးသားဆိုရင် content က overwrite ဖြစ်အောင်)
                        val newFile = parentFolder?.createFile(mimeType, fileName)

                        if (newFile != null) {
                            context!!.contentResolver.openOutputStream(newFile.uri)
                                .use { outputStream ->
                                    outputStream?.write(bytes)
                                }
                            result.success(newFile.uri.toString()) // ရေးပြီးသွားရင် အဲဒီဖိုင်ရဲ့ Uri အစစ်ကို ပြန်ပေးမယ်
                        } else {
                            result.error("WRITE_FAILED", "Could not create file inside URI", null)
                        }
                    } catch (e: Exception) {
                        result.error("WRITE_FAILED", e.localizedMessage, null)
                    }
                }
            }
            // ... class ထဲက when (method) အောက်မှာ ထည့်ရန် ...
            "checkFolderPermission" -> {
                val treeUriStr = call.argument<String>("treeUri") ?: return result.error(
                    "INVALID_ARGS", "treeUri is required", null
                )

                try {
                    val treeUri = treeUriStr.toUri()
                    var hasPermission = false

                    // Android စနစ်ထဲမှာ လက်ရှိ App က ယူထားတဲ့ Persisted (အမြဲတမ်း) Permission တွေကို လိုက်စစ်တာ ဖြစ်ပါတယ်
                    val persistedPermissions = context!!.contentResolver.persistedUriPermissions
                    for (permission in persistedPermissions) {
                        if (permission.uri == treeUri && permission.isWritePermission) {
                            hasPermission = true
                            break
                        }
                    }

                    // တကယ်လို့ စာရင်းထဲမှာ ရှိနေရင်တောင် ဖိုင်စနစ်ထဲကနေ တကယ်သုံးလို့ရသေးလား ထပ်စစ်တယ်
                    if (hasPermission) {
                        val fileDoc = DocumentFile.fromTreeUri(context!!, treeUri)
                        if (fileDoc != null && fileDoc.exists() && fileDoc.canWrite()) {
                            result.success(true) // Permission ရှိနေဆဲ၊ တန်းသုံးလို့ရတယ်
                            return
                        }
                    }

                    result.success(false) // Permission မရှိတော့ဘူး (User က setting ထဲကနေ clear လုပ်လိုက်တာမျိုး)
                } catch (e: Exception) {
                    result.success(false)
                }
            }

            else -> result.notImplemented()
        }
    }

    // Activity Result ပြန်လာချိန် ဖမ်းယူခြင်း
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == 2002) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val treeUri = data.data
                if (treeUri != null) {
                    // ဖုန်းပိတ်ပြီး ပြန်ဖွင့်ရင်တောင် Permission မပျောက်အောင် အမြဲတမ်းသိမ်းခိုင်းခြင်း
                    activity?.contentResolver?.takePersistableUriPermission(
                        treeUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    pendingResult?.success(treeUri.toString()) // Flutter ဘက်ကို Uri string ပြန်ပို့မယ်
                } else {
                    pendingResult?.success(null)
                }
            } else {
                pendingResult?.success(null)
            }
            pendingResult = null
            return true
        }
        return false
    }

    // Permission သက်တမ်း ကုန်မသွားအောင် စစ်ဆေးပေးတဲ့ Helper
    private fun persistPermission(context: Context, uri: Uri) {
        try {
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        } catch (e: Exception) {
            // အရင်က ယူပြီးသားဆိုရင် e တက်နိုင်လို့ ignore လုပ်ထားလို့ရပါတယ်
        }
    }
}