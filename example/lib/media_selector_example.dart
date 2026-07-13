// ignore_for_file: avoid_print

import 'package:than_pkg_android/than_pkg_android.dart';

void main() async {
  // check storage permission
  final per = ThanPkgAndroid.getInstance.storagePermissionHandler;
  if (!await per.isStoragePermissionGranted()) {
    await per.requestStoragePermission();
  }

  final mediaSelector = ThanPkgAndroid.getInstance.mediaSelector;

  ///result android uris
  // List<MediaFile> audioFiles = await mediaSelector.fetchAudio();
  // List<MediaFile> imageFiles = await mediaSelector.fetchImages();
  List<MediaFile> files = await mediaSelector.fetchVideos();

  print('ThanDev: ${files.first}');
  //result: ThanDev: MediaFile(id: 42445, name: Tumbbad (2018).mp4, size: 996276482, uri: content://media/external/video/media/42445, path: /storage/emulated/0/Download/Telegram/Tumbbad (2018).mp4, duration: 1:45:27.125000)

  //media db stream listener
  mediaSelector.onMediaChanged.listen((event) {});
}
