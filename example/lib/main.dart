// ignore_for_file: unused_local_variable, unused_import, avoid_print

import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:than_pkg_android/core/managers/battery_manager.dart';
import 'package:than_pkg_android/core/texture/native_texture.dart';
import 'package:than_pkg_android/than_pkg_android.dart';

void main() {
  runApp(
    MaterialApp(
      debugShowCheckedModeBanner: false,
      home: const MyApp(),
      theme: ThemeData.dark(),
    ),
  );
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool isFullscreen = false;

  String data = '';
  int? textureId;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: isFullscreen ? null : AppBar(title: Text("Than Pkg")),
      body: Center(
        child: textureId == null
            ? Text('texture id is null')
            : Texture(textureId: textureId!),
      ),
      floatingActionButton: actionBtn(),
    );
  }

  FloatingActionButton actionBtn() {
    return FloatingActionButton(
      onPressed: () async {
        print('ThanDev: Start');
        // check storage permission
        // if (!await per.isStoragePermission()) {
        //   await per.requestStoragePermission();
        //   return;
        // }
        final per = ThanPkgAndroid.getInstance.permissionHandler;
        if (!await per.isCameraPermission()) {
          await per.requestCameraPermission();
        }
        final pkg = ThanPkgAndroid.getInstance.cameraHandler;

        final hasFlashlight = await pkg.hasFlashlight();
        print('Dev: hasFlashlight: $hasFlashlight');
        if (hasFlashlight) {
          await pkg.toggleTorch(enable: false);
        } else {
          await pkg.toggleTorch(enable: true);
        }

        // print('Dev: $uri');
      },
    );
  }
}
