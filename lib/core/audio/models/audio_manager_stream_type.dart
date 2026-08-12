// ignore_for_file: constant_identifier_names

/// Android [AudioManager] audio stream types.
///
/// Maps Dart enum values to the corresponding Android
/// `AudioManager.STREAM_*` constants.
enum AudioManagerStreamType {
  /// Music and media audio.
  ///
  /// Android: `AudioManager.STREAM_MUSIC`
  STREAM_MUSIC(3),

  /// Ringtone audio.
  ///
  /// Android: `AudioManager.STREAM_RING`
  STREAM_RING(2),

  /// Alarm audio.
  ///
  /// Android: `AudioManager.STREAM_ALARM`
  STREAM_ALARM(4),

  /// Notification audio.
  ///
  /// Android: `AudioManager.STREAM_NOTIFICATION`
  STREAM_NOTIFICATION(5),

  /// System sound audio.
  ///
  /// Android: `AudioManager.STREAM_SYSTEM`
  STREAM_SYSTEM(1),

  /// Voice call audio.
  ///
  /// Android: `AudioManager.STREAM_VOICE_CALL`
  STREAM_VOICE_CALL(0);

  /// The corresponding Android `AudioManager` stream constant value.
  final int value;

  const AudioManagerStreamType(this.value);

  /// Returns the stream type associated with the given Android value.
  ///
  /// Throws a [StateError] if no matching stream type is found.
  static AudioManagerStreamType fromValue(int val) {
    return values.firstWhere((e) => e.value == val);
  }
}
