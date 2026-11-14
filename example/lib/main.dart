// ignore_for_file: unused_local_variable, unused_import, avoid_print

import 'dart:async';
import 'dart:typed_data';

import 'package:material_ui/material_ui.dart';
import 'package:than_pkg_android/core/managers/battery_manager.dart';
import 'package:than_pkg_android/core/texture/native_texture.dart';
import 'package:than_pkg_android/than_pkg_android.dart';

void main() {
  runApp(
    MaterialApp(
      debugShowCheckedModeBanner: false,
      home: const MyApp(),
      // theme: ThemeData.dark(),
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
      body: Placeholder(),
      floatingActionButton: actionBtn(),
    );
  }

  FloatingActionButton actionBtn() {
    return FloatingActionButton(
      onPressed: () async {
        print('ThanDev: Start');
        final per = ThanPkgAndroid.getInstance.permissionHandler;
        // check storage permission
        if (!await per.isStoragePermission()) {
          await per.requestStoragePermission();
          return;
        }

        final pkg = ThanPkgAndroid.getInstance.mediaSelector;
        final list = await pkg.fetchAudio();
        if (list.isEmpty) return;
        print(list.first);
      },
    );
  }

  void goExample() {
    // Navigator.push(
    //   context,
    //   MaterialPageRoute(builder: (context) => SafeStorageExample()),
    // );
  }
}
