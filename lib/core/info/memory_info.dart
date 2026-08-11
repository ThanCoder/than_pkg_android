import 'package:than_pkg_android/core/utils.dart';

class MemoryInfo {
  final int totalBytes;
  final int availableBytes;
  final bool lowMemory;
  final int thresholdBytes;
  const MemoryInfo({
    required this.totalBytes,
    required this.availableBytes,
    required this.lowMemory,
    required this.thresholdBytes,
  });

  factory MemoryInfo.fromMap(Map<String, dynamic> map) {
    return MemoryInfo(
      totalBytes: map['totalBytes'] ?? 0,
      availableBytes: map['availableBytes'] ?? 0,
      lowMemory: map['lowMemory'] ?? false,
      thresholdBytes: map['thresholdBytes'] ?? 0,
    );
  }

  String get total => Utils.formatSizeLable(totalBytes);
  String get available => Utils.formatSizeLable(availableBytes);
  String get threshold => Utils.formatSizeLable(thresholdBytes);

  @override
  String toString() {
    return 'total: $total,available: $available,threshold: $threshold - MemoryInfo(totalBytes: $totalBytes, availableBytes: $availableBytes, lowMemory: $lowMemory, thresholdBytes: $thresholdBytes)';
  }
}
