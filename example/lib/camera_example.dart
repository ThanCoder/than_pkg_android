// ignore_for_file: avoid_print

import 'package:than_pkg_android/than_pkg_android.dart';

void main() async {
  final handler = ThanPkgAndroid.getInstance.cameraHandler;

  final imagPath = await handler.takePicture();
  //ThanDev: /data/user/0/com.example.than_pkg_android_example/cache/JPEG_20260713_164620_6235099976124367143.jpg

  print('ThanDev: $imagPath');

  //My Phone Not Working!.
  if (await handler.hasFlashlight()) {
    await handler.toggleTorch();
  }
}
