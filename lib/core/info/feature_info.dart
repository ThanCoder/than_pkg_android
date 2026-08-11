class FeatureInfo {
  final bool camera;
  final bool cameraFront;
  final bool bluetooth;
  final bool bluetoothLe;
  final bool nfc;
  final bool gps;
  final bool wifi;
  final bool telephony;
  final bool fingerprint;
  final bool usbHost;
  final bool usbAccessory;
  final bool sensorAccelerometer;
  final bool sensorGyroscope;
  FeatureInfo({
    required this.camera,
    required this.cameraFront,
    required this.bluetooth,
    required this.bluetoothLe,
    required this.nfc,
    required this.gps,
    required this.wifi,
    required this.telephony,
    required this.fingerprint,
    required this.usbHost,
    required this.usbAccessory,
    required this.sensorAccelerometer,
    required this.sensorGyroscope,
  });

  factory FeatureInfo.fromMap(Map<String, dynamic> map) {
    return FeatureInfo(
      camera: map['camera'] ?? false,
      cameraFront: map['cameraFront'] ?? false,
      bluetooth: map['bluetooth'] ?? false,
      bluetoothLe: map['bluetoothLe'] ?? false,
      nfc: map['nfc'] ?? false,
      gps: map['gps'] ?? false,
      wifi: map['wifi'] ?? false,
      telephony: map['telephony'] ?? false,
      fingerprint: map['fingerprint'] ?? false,
      usbHost: map['usbHost'] ?? false,
      usbAccessory: map['usbAccessory'] ?? false,
      sensorAccelerometer: map['sensorAccelerometer'] ?? false,
      sensorGyroscope: map['sensorGyroscope'] ?? false,
    );
  }

  @override
  String toString() {
    return 'FeatureInfo(camera: $camera, cameraFront: $cameraFront, bluetooth: $bluetooth, bluetoothLe: $bluetoothLe, nfc: $nfc, gps: $gps, wifi: $wifi, telephony: $telephony, fingerprint: $fingerprint, usbHost: $usbHost, usbAccessory: $usbAccessory, sensorAccelerometer: $sensorAccelerometer, sensorGyroscope: $sensorGyroscope)';
  }
}
