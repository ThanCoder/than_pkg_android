import 'package:flutter/services.dart';

class FlutterUtils {
  Future<void> toggleFullscreen(bool isFullscreen) async {
    if (isFullscreen) {
      // Fullscreen အမှန်ဖြစ်သွားရင် - Status Bar နဲ့ Bottom Navigation Bar ကို ဝှက်လိုက်မယ်
      await SystemChrome.setEnabledSystemUIMode(SystemUiMode.immersiveSticky);
      // await SystemChrome.setPreferredOrientations([.landscapeLeft]);
    } else {
      // Fullscreen ပြန်ပိတ်ရင် - မူလအတိုင်း အကုန်ပြန်ပြမယ်
      await SystemChrome.setEnabledSystemUIMode(
        SystemUiMode.manual,
        overlays: SystemUiOverlay.values,
      );
      // await SystemChrome.setPreferredOrientations([.portraitUp]);
    }
  }
}