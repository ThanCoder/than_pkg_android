import 'package:flutter/services.dart';
import 'package:than_pkg_android/core/android_os.dart';
import 'package:than_pkg_android/core/android_safe_storage.dart';
import 'package:than_pkg_android/core/android_storage_handler.dart';
import 'package:than_pkg_android/core/android_pdf_handler.dart';
import 'package:than_pkg_android/core/android_utils.dart';
import 'package:than_pkg_android/core/file_handler.dart';
import 'package:than_pkg_android/core/file_selector.dart';
import 'package:than_pkg_android/core/storage_permission_handler.dart';

class ThanPkgAndroid {
  static ThanPkgAndroid? _instance;
  static ThanPkgAndroid get getInstance {
    _instance ??= ThanPkgAndroid();
    return _instance!;
  }

  final _channel = MethodChannel('than_pkg_android');

  late final os = AndroidOs(_channel);
  late final fileSelector = FileSelector(_channel);
  late final fileHandler = FileHandler(_channel);
  late final storagePermissionHandler = StoragePermissionHandler(_channel);
  late final pdfHandler = AndroidPdfHandler(_channel);
  late final storageHandler = AndroidStorageHandler(_channel);
  late final safeStorage = AndroidSafeStorage(_channel);
  final utils = AndroidUtils();
}
