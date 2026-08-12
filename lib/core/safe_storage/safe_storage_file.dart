import 'package:than_pkg_android/core/utils.dart';

class SafeStorageFile {
  final String name;
  final String uri;
  final bool isDirectory;
  final bool isFile;
  final int size;
  final String mimeType;
  final bool canRead;
  final bool canWrite;
  final bool exists;
  const SafeStorageFile({
    required this.name,
    required this.uri,
    required this.isDirectory,
    required this.isFile,
    required this.size,
    required this.mimeType,
    required this.canRead,
    required this.canWrite,
    required this.exists,
  });

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'name': name,
      'uri': uri,
      'isDirectory': isDirectory,
      'isFile': isFile,
      'size': size,
      'mimeType': mimeType,
      'canRead': canRead,
      'canWrite': canWrite,
      'exists': exists,
    };
  }

  factory SafeStorageFile.fromMap(Map<String, dynamic> map) {
    return SafeStorageFile(
      name: map['name'] ?? '',
      uri: map['uri'] ?? '',
      isDirectory: map['isDirectory'] ?? false,
      isFile: map['isFile'] ?? false,
      size: map['size'] ?? 0,
      mimeType: map['mimeType'] ?? '',
      canRead: map['canRead'] ?? false,
      canWrite: map['canWrite'] ?? false,
      exists: map['exists'] ?? false,
    );
  }
  String get sizeLable => Utils.formatSizeLable(size);

  @override
  String toString() {
    return 'SafeStorageFile(name: $name, uri: $uri, isDirectory: $isDirectory, isFile: $isFile, size: $size, mimeType: $mimeType, canRead: $canRead, canWrite: $canWrite, exists: $exists)';
  }
}


/*
{name: MyFolder, uri: content://com.android.externalstorage.documents/tree/primary%3ADownload%2FTest%2FSafe%20Storage/document/primary%3ADownload%2FTest%2FSafe%20Storage%2FMyFolder, isDirectory: true, isFile: false, size: 3452, mimeType: null, canRead: true, canWrite: true, exists: true}

 */