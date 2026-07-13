import 'package:flutter/services.dart';

class DownloadHandler {
  final MethodChannel _channel;
  final String _key;
  const DownloadHandler(this._channel, {this._key = 'downloadHandler'});
}

/*
<!-- အင်တာနက်ကနေ ဖိုင်ဒေါင်းဖို့အတွက် (မဖြစ်မနေ လိုတယ်) -->
<uses-permission android:name="android.android.permission.INTERNET" />

<!-- ဒေါင်းလုဒ်ပြီးသွားရင် ဖုန်းရဲ့ Public Download folder ထဲ ဖိုင်သိမ်းဖို့အတွက် -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />

<!-- 🌟 အရေးကြီးဆုံး: Android 13+ (API 33) ဖုန်းတွေမှာ ဒေါင်းလုဒ်ဆွဲနေတဲ့ Noti တက်လာဖို့အတွက် -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
 */
