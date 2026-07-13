// ignore_for_file: unused_local_variable, unused_import, avoid_print

import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:than_pkg_android/core/managers/battery_manager.dart';
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
    // init();
    super.initState();
  }

  @override
  void dispose() {
    _sub?.cancel();
    super.dispose();
  }

  StreamSubscription<dynamic>? _sub;

  void init() async {
    _sub = ThanPkgAndroid.getInstance.deviceSensorHandler.accelerometerStream
        .listen((event) {
          print('ThanDev: Stream -> $event');
          data = event.toString();
          if (!mounted) return;
          setState(() {});
        });
  }

  String data = '';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: isFullscreen ? null : AppBar(title: Text("Than Pkg")),
      body: Center(child: Text('Data: $data')),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          print('ThanDev: Start');
          // check storage permission
          final per = ThanPkgAndroid.getInstance.permissionHandler;
          if (!await per.isStoragePermission()) {
            await per.requestStoragePermission();
            return;
          }

          final handler = ThanPkgAndroid.getInstance.cameraHandler;
        },
      ),
    );
  }
}
