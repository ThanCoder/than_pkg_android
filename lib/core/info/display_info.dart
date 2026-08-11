class DisplayInfo {
  final int width;
  final int height;
  final double density;
  final int densityDpi;
  final double scaledDensity;
  final double xdpi;
  final double ydpi;
  const DisplayInfo({
    required this.width,
    required this.height,
    required this.density,
    required this.densityDpi,
    required this.scaledDensity,
    required this.xdpi,
    required this.ydpi,
  });

  factory DisplayInfo.fromMap(Map<String, dynamic> map) {
    return DisplayInfo(
      width: map['width'] ?? 0,
      height: map['height'] ?? 0,
      density: map['density'] ?? 0.0,
      densityDpi: map['densityDpi'] ?? 0,
      scaledDensity: map['scaledDensity'] ?? 0.0,
      xdpi: map['xdpi'] ?? 0.0,
      ydpi: map['ydpi'] ?? 0.0,
    );
  }

  @override
  String toString() {
    return 'DisplayInfo(width: $width, height: $height, density: $density, densityDpi: $densityDpi, scaledDensity: $scaledDensity, xdpi: $xdpi, ydpi: $ydpi)';
  }
}
