// ignore_for_file: avoid_print

import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:than_pkg_android/than_pkg_android.dart';
import 'package:than_pkg_android_example/video_list_listener_example.dart';

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

  void useAllowedUri(String allowedFolderUri) async {
    // ၁။ ၎င်း Uri အောက်မှာ ဘာဖိုင်တွေရှိလဲ လှမ်းကြည့်မယ်
    List<Map<dynamic, dynamic>>? files = await ThanPkgAndroid
        .getInstance
        .safeStorage
        .listFiles(allowedFolderUri);

    if (files != null) {
      for (var file in files) {
        print("ဖိုင်နာမည်: ${file['name']}");
        print("၎င်းဖိုင်ရဲ့ တကယ့် Uri လမ်းကြောင်း: ${file['uri']}");
        print("Folder ဟုတ်မဟုတ်: ${file['isDirectory']}");
      }
    }

    // ၂။ ၎င်း Uri ထဲကို ဖိုင်အသစ်တစ်ခု လှမ်းရေးထည့်မယ် (ဥပမာ- patch.obb ဖိုင် သွားထည့်သလိုမျိုး)
    Uint8List dummyData = Uint8List.fromList([
      1,
      2,
      3,
      4,
      5,
    ]); // သင့်ရဲ့ file bytes data

    String? newFileUri = await ThanPkgAndroid.getInstance.safeStorage
        .writeFileData(
          parentUri: allowedFolderUri, // User ဆီက ရထားတဲ့ Uri
          fileName: "patch.obb",
          bytes: dummyData,
        );

    if (newFileUri != null) {
      print(
        "Obb သို့မဟုတ် Target Folder ထဲကို ဖိုင်လှမ်းထည့်တာ အောင်မြင်ပါပြီ။ Uri: $newFileUri",
      );
    }
  }

  Future<void> manageCustomStorage() async {
    // ၁။ အရင်ဆုံး User ကို Folder ရွေးခိုင်းပြီး Permission ယူမယ်
    // (User က obb folder ကို ရွေးပြီး "Use this folder" ဆိုတာကို နှိပ်ပေးရပါမယ်)
    String? allowedFolderUri = await ThanPkgAndroid.getInstance.safeStorage
        .requestFolderPermission();

    if (allowedFolderUri != null) {
      print("ခွင့်ပြုချက်ရရှိထားတဲ့ Folder Uri: $allowedFolderUri");

      // ၂။ ၎င်း Folder ထဲမှာ "MyGameData" ဆိုတဲ့ Folder အသစ်ဆောက်မယ်
      String? newFolderUri = await ThanPkgAndroid.getInstance.safeStorage
          .createFolder(treeUri: allowedFolderUri, folderName: "MyGameData");

      if (newFolderUri != null) {
        print(
          "Folder အသစ်ကို အောင်မြင်စွာ ဆောက်ပြီးပါပြီ။ လမ်းကြောင်း - $newFolderUri",
        );

        // ၃။ လိုအပ်လို့ ပြန်ဖျက်ချင်ရင်လည်း ၎င်း Uri ကို တန်းဖျက်လို့ရပါတယ်
        bool isDeleted = await ThanPkgAndroid.getInstance.safeStorage
            .deleteItem(newFolderUri);
        if (isDeleted) {
          print("ဆောက်ထားတဲ့ Folder ကို ပြန်ဖျက်လိုက်ပါပြီ။");
        }
      }
    } else {
      print("User က Permission မပေးခဲ့ပါဘူး။");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: isFullscreen ? null : AppBar(title: Text("Than Pkg")),
      body: Center(child: Text('isFullscreen: $isFullscreen')),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          // Navigator.push(
          //   context,
          //   MaterialPageRoute(builder: (context) => VideoListListenerExample()),
          // );
          print('ThanDev: Start');
          print(
            'ThanDev: ${await ThanPkgAndroid.getInstance.cameraHandler.toggleTorch()}',
          );
          // print(
          //   'ThanDev: ${await ThanPkgAndroid.getInstance.cameraHandler.takePicture()}',
          // );
          // print(
          //   'wifi: ${await ThanPkgAndroid.getInstance.wifiHandler.getWifiDetails()}',
          // );
          // isFullscreen = !isFullscreen;
          // setState(() {});

          // ThanPkgAndroid.getInstance.pathHandler.

          // ThanPkgAndroid.toggleFullscreen(isFullscreen);
          // print(
          //   'pickFile: ${await ThanPkgAndroid.getInstance.fileSelector.pickFile()}',
          // );
          // print(
          //   'pickFiles: ${await ThanPkgAndroid.getInstance.fileSelector.pickFiles()}',
          // );
          // print('res: ${await ThanPkgAndroid.getInstance.os.getOsBuildInfo()}');
          // debugPrint(
          //   'requestStoragePermission: ${await ThanPkgAndroid.getInstance.storagePermissionHandler.requestStoragePermission()}',
          // );

          // debugPrint(
          //   'isStoragePermissionGranted: ${await ThanPkgAndroid.getInstance.storagePermissionHandler.isStoragePermissionGranted()}',
          // );
        },
      ),
    );
  }
}
