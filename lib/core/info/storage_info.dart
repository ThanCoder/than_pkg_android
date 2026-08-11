import 'package:than_pkg_android/core/utils.dart';

class StorageInfo {
  final int totalBytes;
  final int availableBytes;
  final int freeBytes;
  const StorageInfo({
    required this.totalBytes,
    required this.availableBytes,
    required this.freeBytes,
  });

  factory StorageInfo.fromMap(Map<String, dynamic> map) {
    return StorageInfo(
      totalBytes: map['totalBytes'] ?? 0,
      availableBytes: map['availableBytes'] ?? 0,
      freeBytes: map['freeBytes'] ?? 0,
    );
  }
  String get total => Utils.formatSizeLable(totalBytes);
  String get available => Utils.formatSizeLable(availableBytes);
  String get free => Utils.formatSizeLable(freeBytes);

  @override
  String toString() =>
      'total: $total, available: $available, free: $free, - StorageInfo(totalBytes: $totalBytes, availableBytes: $availableBytes, freeBytes: $freeBytes)';
}
