import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:than_pkg_android/core/safe_storage/safe_storage_file.dart';

/// Provides safe access to user-selected Android folders using
/// the Storage Access Framework (SAF).
///
/// This class communicates with the native Android implementation
/// through a [MethodChannel].
class AndroidSafeStorage {
  final MethodChannel _channel;
  final String _key;

  const AndroidSafeStorage(this._channel, {this._key = 'safeStorageHandler'});

  String _getMethod(String methodName) => '$_key/$methodName';

  /// Requests permission to access a folder selected by the user.
  ///
  /// Opens the Android system folder picker and requests persistent
  /// read/write access to the selected folder.
  ///
  /// Returns the selected folder's tree URI, such as:
  /// `content://com.android.externalstorage.documents/tree/...`
  ///
  /// Returns `null` if the user cancels the picker or an error occurs.
  Future<String?> requestFolderPermission() async {
    try {
      final String? uri = await _channel.invokeMethod(
        _getMethod('requestFolderPermission'),
      );

      return uri;
    } catch (_) {
      return null;
    }
  }

  /// Creates a new folder inside an authorized folder.
  ///
  /// [treeUri] is the tree URI obtained from
  /// [requestFolderPermission].
  ///
  /// [relativePath] optionally specifies a child path inside [treeUri].
  ///
  /// Example:
  /// ```dart
  /// createFolder(
  ///   treeUri: rootUri,
  ///   relativePath: 'data/books',
  ///   folderName: 'covers',
  /// );
  /// ```
  ///
  /// This creates:
  /// `rootUri/data/books/covers`
  ///
  /// If [relativePath] is omitted, the folder is created directly
  /// inside [treeUri].
  ///
  /// Returns the URI of the created or existing folder.
  Future<String?> createFolder({
    required String treeUri,
    required String folderName,
    String? relativePath,
  }) async {
    try {
      final String? newFolderUri = await _channel.invokeMethod(
        _getMethod('createFolder'),
        {
          'treeUri': treeUri,
          'folderName': folderName,
          'relativePath': relativePath,
        },
      );

      return newFolderUri;
    } catch (_) {
      return null;
    }
  }

  /// Checks whether the application still has persistent write access
  /// to the specified folder.
  ///
  /// [treeUri] must be a previously authorized tree URI.
  ///
  /// Returns `true` when the permission is still available and the
  /// folder can be accessed and written to.
  Future<bool> checkFolderPermission(String treeUri) async {
    try {
      final bool? hasPermission = await _channel.invokeMethod(
        _getMethod('checkFolderPermission'),
        {'treeUri': treeUri},
      );

      return hasPermission ?? false;
    } catch (_) {
      return false;
    }
  }

  /// Deletes a file or folder using its content URI.
  ///
  /// [targetUri] must point to a file or folder that the application
  /// has permission to modify.
  ///
  /// Returns `true` when the item is successfully deleted.
  Future<bool> deleteItem(String targetUri) async {
    try {
      final bool? success = await _channel.invokeMethod(
        _getMethod('deleteItem'),
        {'targetUri': targetUri},
      );

      return success ?? false;
    } catch (_) {
      return false;
    }
  }

  /// Lists the files and folders inside an authorized folder.
  ///
  /// [treeUri] is the authorized root tree URI.
  ///
  /// [relativePath] optionally specifies a child folder.
  ///
  /// Example:
  /// ```dart
  /// final files = await storage.listFiles(
  ///   rootUri,
  ///   relativePath: 'data/books/covers',
  /// );
  /// ```
  ///
  /// Each returned item contains:
  /// - `name`
  /// - `uri`
  /// - `isDirectory`
  /// - `isFile`
  /// - `size`
  /// - `mimeType`
  /// - `canRead`
  /// - `canWrite`
  /// - `exists`
  ///
  /// Returns an empty list when the folder contains no items.
  /// Returns `null` if an error occurs.
  Future<List<SafeStorageFile>?> listFiles(
    String treeUri, {
    String? relativePath,
  }) async {
    try {
      final files = await _channel.invokeMethod<List<dynamic>>(
        _getMethod('listFiles'),
        {'treeUri': treeUri, 'relativePath': relativePath},
      );

      if (files != null) {
        return files
            .map((e) => SafeStorageFile.fromMap(Map<String, dynamic>.from(e)))
            .toList();
      }

      return [];
    } catch (e) {
      debugPrint('Dev: [AndroidSafeStorage:listFiles]: $e');
      return null;
    }
  }

  /// Writes binary data to a file inside an authorized folder.
  ///
  /// [parentUri] is the authorized root or parent folder URI.
  ///
  /// [relativePath] optionally specifies a child folder inside
  /// [parentUri].
  ///
  /// Example:
  /// ```dart
  /// await storage.writeFileData(
  ///   parentUri: rootUri,
  ///   relativePath: 'data/books/covers',
  ///   fileName: 'cover.jpg',
  ///   mimeType: 'image/jpeg',
  ///   bytes: imageBytes,
  /// );
  /// ```
  ///
  /// This writes the file to:
  /// `rootUri/data/books/covers/cover.jpg`
  ///
  /// If a file with the same [fileName] already exists, it will be
  /// deleted before the new file is created.
  ///
  /// [bytes] contains the binary data to write.
  ///
  /// [mimeType] specifies the MIME type of the file.
  ///
  /// Returns the URI of the created file.
  ///
  /// This method is intended for relatively small files because the
  /// entire [bytes] buffer is passed through the platform channel.
  /// Large files should use a streaming or chunked API instead.
  Future<String?> writeFileData({
    required String parentUri,
    required String fileName,
    required Uint8List bytes,
    String? relativePath,
    String mimeType = 'application/octet-stream',
  }) async {
    try {
      final String? fileUri = await _channel
          .invokeMethod(_getMethod('writeFileData'), {
            'parentUri': parentUri,
            'relativePath': relativePath,
            'fileName': fileName,
            'mimeType': mimeType,
            'bytes': bytes,
          });

      return fileUri;
    } catch (_) {
      return null;
    }
  }
}
