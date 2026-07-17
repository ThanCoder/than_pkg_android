// ignore_for_file: unused_local_variable, unused_import, avoid_print

import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:than_pkg_android/core/managers/battery_manager.dart';
import 'package:than_pkg_android/core/texture/native_texture.dart';
import 'package:than_pkg_android/than_pkg_android.dart';

class TextureExample extends StatefulWidget {
  const TextureExample({super.key});

  @override
  State<TextureExample> createState() => _TextureExampleState();
}

class _TextureExampleState extends State<TextureExample> {
  @override
  void dispose() {
    if (textureId != null) {
      ThanPkgAndroid.getInstance.textureHandler.releaseTexture(textureId!);
    }
    super.dispose();
  }

  int? textureId;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("Texture Example")),
      body: Center(
        child: textureId == null
            ? Text('texture id is null')
            : Texture(textureId: textureId!),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          print('ThanDev: Start');

          final handler = ThanPkgAndroid.getInstance.textureHandler;
          // if (textureId != null) {
          //   await handler.releaseTexture(textureId!);
          //   textureId = null;
          //   setState(() {});
          //   return;
          // }
          textureId ??= await handler.createTexture();
          

          // test color texture
          testTextureColor(textureId!);
          setState(() {});
        },
      ),
    );
  }
}
