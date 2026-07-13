import 'package:flutter/services.dart';

class AppSettingHandler {
  final MethodChannel _channel;
  final String _key;
  const AppSettingHandler(this._channel, {this._key = 'appSettingHandler'});

  // --- ပင်မ နှင့် App ဆိုင်ရာ Settings (Main & Apps) ---

  /// ဖုန်းရဲ့ ပင်မ Settings မျက်နှာပြင်ကြီးကို ဖွင့်ရန်
  Future<bool> openSystemSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openSystemSettings')) ??
        false;
  }

  /// ကိုယ့် App ရဲ့ App Info (Detail) မျက်နှာပြင်ကို ဖွင့်ရန် (Permission ပေးရန်၊ Cache ရှင်းရန်)
  Future<bool> openAppSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openAppSettings')) ??
        false;
  }

  /// *#*#4636#*#* (Phone Info / Radio Info) မျက်နှာပြင်ကို တိုက်ရိုက်ဖွင့်ရန်
  Future<bool> openPhoneInfo() async {
    return (await _channel.invokeMethod<bool>('$_key/openPhoneInfo')) ?? false;
  }

  /// ဖုန်းထဲမှာ သွင်းထားသမျှ App တွေအားလုံး စာရင်းပြတဲ့နေရာ (App List) ကို ဖွင့်ရန်
  Future<bool> openAppListSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openAppListSettings')) ??
        false;
  }

  /// ကိုယ့် App ရဲ့ Notification အပိတ်အဖွင့် Page သို့ တိုက်ရိုက်သွားရန် (Android 8.0+)
  Future<bool> openAppNotificationSettings() async {
    return (await _channel.invokeMethod<bool>(
          '$_key/openAppNotificationSettings',
        )) ??
        false;
  }

  // --- ဟာ့ဒ်ဝဲ နှင့် စနစ်ပိုင်းဆိုင်ရာ Settings (Hardware & System) ---

  /// ဖုန်း Storage နဲ့ SD card အခြေအနေ ကြည့်တဲ့နေရာ (Storage Settings) ကို ဖွင့်ရန်
  Future<bool> openStorageSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openStorageSettings')) ??
        false;
  }

  /// ဘက်ထရီအားသုံးစွဲမှုနဲ့ Power Saving မုဒ်များ ချိန်ညှိရာနေရာ (Battery Settings) ကို ဖွင့်ရန်
  Future<bool> openBatterySettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openBatterySettings')) ??
        false;
  }

  /// Developer တွေအတွက် သီးသန့် ချိန်ညှိနိုင်တဲ့ (Developer Options) ကို ဖွင့်ရန်
  Future<bool> openDeveloperSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openDeveloperSettings')) ??
        false;
  }

  /// မျက်နှာပြင် အလင်းအမှောင်၊ Dark Mode၊ Font size ချိန်တဲ့နေရာ (Display Settings) ကို ဖွင့်ရန်
  Future<bool> openDisplaySettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openDisplaySettings')) ??
        false;
  }

  /// မသန်စွမ်းမှုဆိုင်ရာ အကူအညီပေးရေး ဝန်ဆောင်မှုများ နေရာ (Accessibility Settings) ကို ဖွင့်ရန်
  Future<bool> openAccessibilitySettings() async {
    return (await _channel.invokeMethod<bool>(
          '$_key/openAccessibilitySettings',
        )) ??
        false;
  }

  /// ဖုန်းအကြောင်း အသေးစိတ်ပြသသည့် နေရာ (About Device / Device Info) ကို ဖွင့်ရန်
  Future<bool> openAboutSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openAboutSettings')) ??
        false;
  }

  // --- ကွန်ရက် နှင့် ချိတ်ဆက်မှု ဆိုင်ရာ (Network & Connectivity) ---

  /// Wi-Fi ဖွင့်/ပိတ် နှင့် ချိတ်ဆက်မှု စာရင်းပြရာနေရာ (Wi-Fi Settings) ကို ဖွင့်ရန်
  Future<bool> openWifiSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openWifiSettings')) ??
        false;
  }

  /// Bluetooth ဖွင့်/ပိတ် နှင့် ချိတ်ဆက်ရန် စာရင်းပြရာနေရာ (Bluetooth Settings) ကို ဖွင့်ရန်
  Future<bool> openBluetoothSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openBluetoothSettings')) ??
        false;
  }

  /// Mobile Data သုံးစွဲမှု အခြေအနေပြရာနေရာ (Data Usage Settings) ကို ဖွင့်ရန်
  Future<bool> openDataUsageSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openDataUsageSettings')) ??
        false;
  }

  /// လေယာဉ်မုဒ် (Airplane Mode) အပိတ်အဖွင့် လုပ်ရာနေရာကို ဖွင့်ရန်
  Future<bool> openAirplaneModeSettings() async {
    return (await _channel.invokeMethod<bool>(
          '$_key/openAirplaneModeSettings',
        )) ??
        false;
  }

  /// VPN ချိတ်ဆက်မှုများ စီမံရာနေရာ (VPN Settings) ကို ဖွင့်ရန်
  Future<bool> openVpnSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openVpnSettings')) ??
        false;
  }

  /// TV သို့ Screen Cast လွှင့်ရာနေရာ (Cast Settings) ကို ဖွင့်ရန်
  Future<bool> openCastSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openCastSettings')) ??
        false;
  }

  /// NFC အပိတ်/အဖွင့် လုပ်ရာနေရာ (NFC Settings) ကို ဖွင့်ရန်
  Future<bool> openNfcSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openNfcSettings')) ??
        false;
  }

  // --- လုံခြုံရေး နှင့် တည်နေရာ ဆိုင်ရာ (Security & Location) ---

  /// GPS နှင့် တည်နေရာဝန်ဆောင်မှု နေရာ (Location Settings) ကို ဖွင့်ရန်
  Future<bool> openLocationSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openLocationSettings')) ??
        false;
  }

  /// ဖုန်းလုံခြုံရေး (Security Settings) နေရာကို ဖွင့်ရန်
  Future<bool> openSecuritySettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openSecuritySettings')) ??
        false;
  }

  /// မျက်နှာ သို့မဟုတ် လက်ဗွေ (Biometrics) သတ်မှတ်ရာ နေရာကို ဖွင့်ရန် (Android 10+)
  Future<bool> openBiometricSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openBiometricSettings')) ??
        false;
  }

  // --- အသံ နှင့် အသိပေးချက် ဆိုင်ရာ (Sound & Notification) ---

  /// အသံအတိုးအကျယ်နှင့် ဖုန်းမြည်သံ ချိန်ညှိရာနေရာ (Sound Settings) ကို ဖွင့်ရန်
  Future<bool> openSoundSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openSoundSettings')) ??
        false;
  }

  /// Notification Policy (Do Not Disturb) ချိန်ညှိရာ နေရာကို ဖွင့်ရန်
  Future<bool> openNotificationSettings() async {
    return (await _channel.invokeMethod<bool>(
          '$_key/openNotificationSettings',
        )) ??
        false;
  }

  // --- နေ့ရက်၊ အချိန် နှင့် ဘာသာစကား (Date, Time & Language) ---

  /// နေ့စွဲနှင့် အချိန် ချိန်ညှိရာ နေရာ (Date & Time Settings) ကို ဖွင့်ရန်
  Future<bool> openDateSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openDateSettings')) ??
        false;
  }

  /// ဖုန်းဘာသာစကား ချိန်ညှိရာ နေရာ (Locale & Language Settings) ကို ဖွင့်ရန်
  Future<bool> openLocaleSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openLocaleSettings')) ??
        false;
  }

  /// Keyboard အပြောင်းအလဲနှင့် Input Method နေရာ (Keyboard Settings) ကို ဖွင့်ရန်
  Future<bool> openKeyboardSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openKeyboardSettings')) ??
        false;
  }

  // --- အခြား အထွေထွေ (Others) ---

  /// အကောင့်များ (Accounts) နှင့် စင့်ခ် (Sync) လုပ်ရာ နေရာကို ဖွင့်ရန်
  Future<bool> openSyncSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openSyncSettings')) ??
        false;
  }

  /// ဖုန်းတွင်း ရှာဖွေမှု (Search) ချိန်ညှိရာ နေရာကို ဖွင့်ရန်
  Future<bool> openSearchSettings() async {
    return (await _channel.invokeMethod<bool>('$_key/openSearchSettings')) ??
        false;
  }
}
