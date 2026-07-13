// ignore_for_file: avoid_print

import 'package:than_pkg_android/than_pkg_android.dart';

void main() async {
  // check storage permission
  final per = ThanPkgAndroid.getInstance.storagePermissionHandler;
  if (!await per.isStoragePermissionGranted()) {
    await per.requestStoragePermission();
  }

  print('ThanDev: Start');
  final selector = ThanPkgAndroid.getInstance.fileSelector;

  ///result android uris
  List<String> files = await selector.pickFiles();

  //result:
  // [content://com.android.fileexplorer.myprovider/external_files/DCIM/Screenshots/Screenshot_2026-07-03-19-30-03-538_than.app.than_player-edit.jpg]
  print('ThanDev: $files');

  //you nedd to use `uriHandler`
  final uriHandler = ThanPkgAndroid.getInstance.uriHandler;

  final outPath = ThanPkgAndroid.getInstance.pathHandler
      .getDeviceStoragePath(); // /storage/emulated/0/

  await uriHandler.copyContentToFile(files.first, '$outPath/test.png');
  print('ThanDev: $outPath/test.png');

  ThanPkgAndroid.getInstance.osHandler.showToast('Copied');
}
