class BatteryInfo {
  final int level;
  final bool isCharging;
  const BatteryInfo({required this.level, required this.isCharging});

  factory BatteryInfo.fromMap(Map<String, dynamic> map) {
    return BatteryInfo(
      level: map['level'] ?? -1,
      isCharging: map['isCharging'] ?? false,
    );
  }

  @override
  String toString() => 'BatteryInfo(level: $level, isCharging: $isCharging)';
}
