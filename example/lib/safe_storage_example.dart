import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:than_pkg_android/than_pkg_android.dart';

class SafeStorageExample extends StatefulWidget {
  const SafeStorageExample({super.key});

  @override
  State<SafeStorageExample> createState() => _SafeStorageExampleState();
}

class _SafeStorageExampleState extends State<SafeStorageExample> {
  final safeStorage = ThanPkgAndroid.getInstance.safeStorage;

  String? treeUri;

  Future<void> selectFolder() async {
    final uri = await safeStorage.requestFolderPermission();

    if (uri == null) {
      debugPrint('Folder selection cancelled');
      return;
    }

    setState(() {
      treeUri = uri;
    });

    debugPrint('Selected folder: $uri');
  }

  Future<void> createFolder() async {
    final uri = treeUri;

    if (uri == null) {
      debugPrint('Please select a folder first');
      return;
    }

    final folderUri = await safeStorage.createFolder(
      treeUri: uri,
      folderName: 'MyFolder',
    );

    debugPrint('Created folder: $folderUri');
  }

  Future<void> createNestedFolders() async {
    final uri = treeUri;

    if (uri == null) {
      debugPrint('Please select a folder first');
      return;
    }

    // Creates:
    //
    // SelectedFolder/
    // └── data/
    //     └── books/
    //         └── covers/
    //
    await safeStorage.createFolder(treeUri: uri, folderName: 'data');

    await safeStorage.createFolder(
      treeUri: uri,
      relativePath: 'data',
      folderName: 'books',
    );

    final coversUri = await safeStorage.createFolder(
      treeUri: uri,
      relativePath: 'data/books',
      folderName: 'covers',
    );

    debugPrint('Covers folder: $coversUri');
  }

  Future<void> listFiles() async {
    final uri = treeUri;

    if (uri == null) {
      debugPrint('Please select a folder first');
      return;
    }

    final files = await safeStorage.listFiles(uri);

    if (files == null) {
      debugPrint('Failed to list files');
      return;
    }

    debugPrint('Root files:');
    // debugPrint('Dev: $files');

    for (final file in files) {
      debugPrint('Dev: $file');
    }
  }

  Future<void> listChildFiles() async {
    final uri = treeUri;

    if (uri == null) {
      debugPrint('Please select a folder first');
      return;
    }

    final files = await safeStorage.listFiles(
      uri,
      // relativePath: 'data/books/covers',
    );

    if (files == null) {
      debugPrint('Failed to list child folder');
      return;
    }

    for (final file in files) {
      debugPrint(
        'Dev: ${file.name} - '
        '${file.size} bytes - Size: ${file.sizeLable}',
      );
    }
  }

  Future<void> writeFile() async {
    final uri = treeUri;

    if (uri == null) {
      debugPrint('Please select a folder first');
      return;
    }

    final bytes = Uint8List.fromList('Hello from Flutter!'.codeUnits);

    final fileUri = await safeStorage.writeFileData(
      parentUri: uri,
      fileName: 'hello.txt',
      mimeType: 'text/plain',
      bytes: bytes,
    );

    debugPrint('File URI: $fileUri');
  }

  Future<void> writeChildFile() async {
    final uri = treeUri;

    if (uri == null) {
      debugPrint('Please select a folder first');
      return;
    }

    final bytes = Uint8List.fromList('Hello from child folder!'.codeUnits);

    final fileUri = await safeStorage.writeFileData(
      parentUri: uri,
      relativePath: 'data/books/covers',
      fileName: 'cover.txt',
      mimeType: 'text/plain',
      bytes: bytes,
    );

    debugPrint('Child file URI: $fileUri');
  }

  Future<void> deleteFile(String fileUri) async {
    final deleted = await safeStorage.deleteItem(fileUri);

    debugPrint('Deleted: $deleted');
  }

  Future<void> checkPermission() async {
    final uri = treeUri;

    if (uri == null) {
      debugPrint('Please select a folder first');
      return;
    }

    final hasPermission = await safeStorage.checkFolderPermission(uri);

    debugPrint('Has permission: $hasPermission');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Safe Storage')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: selectFolder,
                child: const Text('Select Folder'),
              ),
            ),

            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: createFolder,
                child: const Text('Create MyFolder'),
              ),
            ),

            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: createNestedFolders,
                child: const Text('Create Nested Folders'),
              ),
            ),

            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: listFiles,
                child: const Text('List Root Files'),
              ),
            ),

            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: listChildFiles,
                child: const Text('List Child Files'),
              ),
            ),

            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: writeFile,
                child: const Text('Write Root File'),
              ),
            ),

            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: writeChildFile,
                child: const Text('Write Child File'),
              ),
            ),

            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: checkPermission,
                child: const Text('Check Permission'),
              ),
            ),

            if (treeUri != null) ...[
              const SizedBox(height: 20),

              const Text(
                'Selected Tree URI',
                style: TextStyle(fontWeight: FontWeight.bold),
              ),

              const SizedBox(height: 8),

              SelectableText(treeUri!, textAlign: TextAlign.center),
            ],
          ],
        ),
      ),
    );
  }
}
