// ignore_for_file: avoid_print

import 'package:than_pkg_android/than_pkg_android.dart';

void main() async {
  final handler = ThanPkgAndroid.getInstance.pathHandler;

  await handler.getCachePath();
  await handler.getExternalFilesPath();
  await handler.getFilesPath();

  handler.getDCIMPath();
  handler.getDeviceStoragePath();
  handler.getDocumentsPath();
  handler.getDownloadPath();
  handler.getMoviesPath();
  handler.getMusicPath();
  handler.getPicturesPath();
}
