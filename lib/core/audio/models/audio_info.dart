import 'audio_manager_mode.dart';
import 'ringer_mode.dart';

class AudioInfo {
  final bool isMusicActive;
  final bool isSpeakerphoneOn;
  final bool isBluetoothA2dpOn;
  final bool isBluetoothScoOn;
  final bool isWiredHeadsetOn;
  final bool isVolumeFixed;

  ///   Returns the current ringtone mode.
  /// Returns:
  /// The current ringtone mode, one of `RINGER_MODE_NORMAL`, `RINGER_MODE_SILENT`, or `RINGER_MODE_VIBRATE`.

  final RingerMode ringerMode;
  final AudioManagerMode mode;
  const AudioInfo({
    required this.isMusicActive,
    required this.isSpeakerphoneOn,
    required this.isBluetoothA2dpOn,
    required this.isBluetoothScoOn,
    required this.isWiredHeadsetOn,
    required this.isVolumeFixed,
    required this.ringerMode,
    required this.mode,
  });

  factory AudioInfo.fromMap(Map<String, dynamic> map) {
    return AudioInfo(
      isMusicActive: map['isMusicActive'] ?? false,
      isSpeakerphoneOn: map['isSpeakerphoneOn'] ?? false,
      isBluetoothA2dpOn: map['isBluetoothA2dpOn'] ?? false,
      isBluetoothScoOn: map['isBluetoothScoOn'] ?? false,
      isWiredHeadsetOn: map['isWiredHeadsetOn'] ?? false,
      isVolumeFixed: map['isVolumeFixed'] ?? false,
      ringerMode: RingerMode.fromValue(map['ringerMode']),
      mode: AudioManagerMode.fromValue(map['mode']),
    );
  }

  @override
  String toString() {
    return 'AudioInfo(isMusicActive: $isMusicActive, isSpeakerphoneOn: $isSpeakerphoneOn, isBluetoothA2dpOn: $isBluetoothA2dpOn, isBluetoothScoOn: $isBluetoothScoOn, isWiredHeadsetOn: $isWiredHeadsetOn, isVolumeFixed: $isVolumeFixed, ringerMode: $ringerMode, mode: $mode)';
  }
}
