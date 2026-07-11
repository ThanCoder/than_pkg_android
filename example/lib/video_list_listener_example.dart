// ignore_for_file: avoid_print

import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:than_pkg_android/than_pkg_android.dart';

class VideoListListenerExample extends StatefulWidget {
  const VideoListListenerExample({super.key});

  @override
  VideoListListenerExampleState createState() =>
      VideoListListenerExampleState();
}

class VideoListListenerExampleState extends State<VideoListListenerExample> {
  final mediaSelector = ThanPkgAndroid.getInstance.mediaSelector;
  List<MediaFile> videos = [];
  StreamSubscription? _subscription;

  @override
  void initState() {
    super.initState();
    _loadVideos();

    // 🌟 Android Media Database ကို Live စောင့်ကြည့်ပြီး Auto-Update လုပ်ခြင်း
    _subscription = mediaSelector.onMediaChanged.listen((event) {
      print(
        "Media DB ပြောင်းလဲသွားလို့ ဗီဒီယိုစာရင်းကို Auto Refresh လုပ်ပေးလိုက်ပါပြီ: $event",
      );
      _loadVideos();
    });
  }

  String? cachePath;

  Future<void> _loadVideos() async {
    cachePath = await ThanPkgAndroid.getInstance.pathHandler.getCachePath();
    final data = await mediaSelector.fetchVideos();
    setState(() {
      videos = data;
    });
  }

  @override
  void dispose() {
    _subscription?.cancel(); // Memory leak မဖြစ်အောင် ပြန်ပိတ်ရန်
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    print('cache: $cachePath');
    return Scaffold(
      appBar: AppBar(title: Text('Video List Listener')),
      body: ListView.builder(
        itemCount: videos.length,
        itemBuilder: (context, index) {
          final video = videos[index];
          return Column(
            crossAxisAlignment: .start,
            children: [
              ListTile(
                title: Text(video.name),
                subtitle: Text(
                  "${(video.size / (1024 * 1024)).toStringAsFixed(2)} MB",
                ),
                trailing: Text(
                  "${video.duration.inMinutes}:${video.duration.inSeconds.remainder(60)}",
                ),
              ),
              SizedBox(width: 120, height: 140, child: getThumbnail(video)),
            ],
          );
        },
      ),
    );
  }

  Widget getThumbnail(MediaFile video) {
    // print('id: ${video.id} - path: ${video.path}');
    // print('cache: $cachePath');
    final cacheFile = File('$cachePath/${video.id}.png');
    if (cacheFile.existsSync()) {
      return Image.file(cacheFile);
    }

    return FutureBuilder(
      future: ThanPkgAndroid.getInstance.videoHandler.saveThumbnail(
        video.path,
        cacheFile.path,
      ),
      builder: (context, snapshot) {
        if (snapshot.connectionState == .waiting) {
          return Center(child: CircularProgressIndicator.adaptive());
        }
        return Image.file(cacheFile);
      },
    );
  }
}
