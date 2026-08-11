class DeviceInfo {
  final String manufacturer;
  final String model;
  final String brand;
  final String device;
  final String product;
  final String hardware;
  final String board;
  final String bootloader;
  final String display;
  final String host;
  final String buildId;
  final String tags;
  final String type;
  final String user;
  final List<String> supportedAbis;
  final List<String> supported32BitAbis;
  final List<String> supported64BitAbis;
  const DeviceInfo({
    required this.manufacturer,
    required this.model,
    required this.brand,
    required this.device,
    required this.product,
    required this.hardware,
    required this.board,
    required this.bootloader,
    required this.display,
    required this.host,
    required this.buildId,
    required this.tags,
    required this.type,
    required this.user,
    required this.supportedAbis,
    required this.supported32BitAbis,
    required this.supported64BitAbis,
  });

  factory DeviceInfo.fromMap(Map<String, dynamic> map) {
    return DeviceInfo(
      manufacturer: map['manufacturer'] as String,
      model: map['model'] ?? '',
      brand: map['brand'] ?? '',
      device: map['device'] ?? '',
      product: map['product'] ?? '',
      hardware: map['hardware'] ?? '',
      board: map['board'] ?? '',
      bootloader: map['bootloader'] ?? '',
      display: map['display'] ?? '',
      host: map['host'] ?? '',
      buildId: map['buildId'] ?? '',
      tags: map['tags'] ?? '',
      type: map['type'] ?? '',
      user: map['user'] ?? '',
      supportedAbis: List<String>.from(map['supportedAbis'] ?? []),
      supported32BitAbis: List<String>.from(map['supported32BitAbis'] ?? []),
      supported64BitAbis: List<String>.from(map['supported64BitAbis'] ?? []),
    );
  }

  @override
  String toString() {
    return 'DeviceInfo(manufacturer: $manufacturer, model: $model, brand: $brand, device: $device, product: $product, hardware: $hardware, board: $board, bootloader: $bootloader, display: $display, host: $host, buildId: $buildId, tags: $tags, type: $type, user: $user, supportedAbis: $supportedAbis, supported32BitAbis: $supported32BitAbis, supported64BitAbis: $supported64BitAbis)';
  }
}
