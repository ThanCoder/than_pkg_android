import 'package:flutter/services.dart';

import 'media_file.dart';



class MediaSelector {
  final MethodChannel _channel;
  final String _key;
  
  // Real-time update အတွက် EventChannel
  final EventChannel _eventChannel = const EventChannel('than_pkg_android/media_stream');

  MediaSelector(this._channel, {this._key = 'mediaFetchHandler'});

  /// ### Media DB ပြောင်းလဲမှုများကို Live နားစွင့်ရန် Stream
  /// ဖုန်းထဲ ဖိုင်အသစ်ဝင်လာရင်/ဖျက်လိုက်ရင် ဒီ Stream ကနေ Event လှမ်းပစ်ပေးလိမ့်မယ်။
  Stream<String> get onMediaChanged => _eventChannel.receiveBroadcastStream().map((event) => event.toString());

  Future<List<MediaFile>> _fetchMedia(String method) async {
    try {
      final List<dynamic>? result = await _channel.invokeListMethod<dynamic>('$_key/$method');
      if (result == null) return [];
      return result.map((e) => MediaFile.fromMap(e as Map<dynamic, dynamic>)).toList();
    } on PlatformException catch (_) {
      return [];
    }
  }

  /// ### ဖုန်းထဲက ဓာတ်ပုံအားလုံးကို DB ထဲကနေ ဆွဲထုတ်ရန်
  Future<List<MediaFile>> fetchImages() => _fetchMedia('fetchImages');

  /// ### ဖုန်းထဲက ဗီဒီယိုအားလုံးကို DB ထဲကနေ ဆွဲထုတ်ရန်
  Future<List<MediaFile>> fetchVideos() => _fetchMedia('fetchVideos');

  /// ### ဖုန်းထဲက သီချင်း/အသံဖိုင်အားလုံးကို DB ထဲကနေ ဆွဲထုတ်ရန်
  Future<List<MediaFile>> fetchAudio() => _fetchMedia('fetchAudio');
}