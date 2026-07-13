class OsBuildInfo {
  // 🟢 ၁။ အခြေခံ OS Version အချက်အလက်များ
  final String release;
  final int sdkInt;
  final String incremental;
  final String codename;

  // 🟢 ၂။ Device Hardware အချက်အလက်များ
  final String brand;
  final String manufacturer;
  final String model;
  final String product;
  final String hardware;
  final String device;
  final String display;
  final String fingerprint;

  // 🟢 ၃။ Android 6.0 (M) နှင့် အထက်အတွက်
  final String baseOs;
  final String securityPatch;

  // 🟢 ၄။ Android 12 (S) နှင့် အထက်အတွက်
  final int mediaPerformanceClass;

  // 🟢 ၅။ Android 13 (Tiramisu) နှင့် အထက်အတွက်
  final String releaseOrCodename;
  final String releaseOrPreviewDisplay;

  // 🟢 ၆။ Android 14 (U) နှင့် အထက်အတွက်
  final int sdkIntFull;

  // Constructor
  OsBuildInfo({
    required this.release,
    required this.sdkInt,
    required this.incremental,
    required this.codename,
    required this.brand,
    required this.manufacturer,
    required this.model,
    required this.product,
    required this.hardware,
    required this.device,
    required this.display,
    required this.fingerprint,
    required this.baseOs,
    required this.securityPatch,
    required this.mediaPerformanceClass,
    required this.releaseOrCodename,
    required this.releaseOrPreviewDisplay,
    required this.sdkIntFull,
  });

  // 🌟 Kotlin Map ကနေ ခွဲထုတ်ပြီး Object တည်ဆောက်မယ့် Factory Constructor
  factory OsBuildInfo.fromMap(Map<dynamic, dynamic> map) {
    return OsBuildInfo(
      // ၁။ OS Version
      release: map["RELEASE"]?.toString() ?? "",
      sdkInt: map["SDK_INT"] as int? ?? 0,
      incremental: map["INCREMENTAL"]?.toString() ?? "",
      codename: map["CODENAME"]?.toString() ?? "",

      // ၂။ Hardware
      brand: map["BRAND"]?.toString() ?? "",
      manufacturer: map["MANUFACTURER"]?.toString() ?? "",
      model: map["MODEL"]?.toString() ?? "",
      product: map["PRODUCT"]?.toString() ?? "",
      hardware: map["HARDWARE"]?.toString() ?? "",
      device: map["DEVICE"]?.toString() ?? "",
      display: map["DISPLAY"]?.toString() ?? "",
      fingerprint: map["FINGERPRINT"]?.toString() ?? "",

      // ၃။ Android 6.0+
      baseOs: map["BASE_OS"]?.toString() ?? "",
      securityPatch: map["SECURITY_PATCH"]?.toString() ?? "",

      // ၄။ Android 12+
      mediaPerformanceClass: map["MEDIA_PERFORMANCE_CLASS"] as int? ?? 0,

      // ၅။ Android 13+
      releaseOrCodename: map["RELEASE_OR_CODENAME"]?.toString() ?? "",
      releaseOrPreviewDisplay:
          map["RELEASE_OR_PREVIEW_DISPLAY"]?.toString() ?? "",

      // ၆။ Android 14+
      sdkIntFull: map["SDK_INT_FULL"] as int? ?? 0,
    );
  }

  // Debug လုပ်တဲ့အခါ Data တွေကို အလွယ်တကူ Print ထုတ်ကြည့်နိုင်အောင် toString() ပါ ထည့်ပေးထားတယ် Bro
  @override
  String toString() {
    return 'OsBuildInfo(model: $model, brand: $brand, sdkInt: $sdkInt, sdkIntFull: $sdkIntFull, securityPatch: $securityPatch)';
  }
}
