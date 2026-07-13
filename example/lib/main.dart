// ignore_for_file: unused_import, avoid_print

import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/material.dart';
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

  @override
  void initState() {
    init();
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  void init() async {}

  String data = '';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: isFullscreen ? null : AppBar(title: Text("Than Pkg")),
      body: Center(child: Text('Data: $data')),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          // Navigator.push(
          //   context,
          //   MaterialPageRoute(builder: (context) => VideoListListenerExample()),
          // );
          print('ThanDev: Start');
          // print(
          //   'ThanDev: ${await ThanPkgAndroid.getInstance.cameraHandler.toggleTorch()}',
          // );
        },
      ),
    );
  }
}
