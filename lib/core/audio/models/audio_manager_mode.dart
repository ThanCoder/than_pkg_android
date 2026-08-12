// ignore_for_file: constant_identifier_names, dangling_library_doc_comments, slash_for_doc_comments

/**
/* modes for setPhoneState, must match AudioSystem.h audio_mode */
    /** @hide */
    public static final int MODE_INVALID            = -2;
    /** @hide */
    public static final int MODE_CURRENT            = -1;
    /** @hide */
    public static final int MODE_NORMAL             = 0;
    /** @hide */
    public static final int MODE_RINGTONE           = 1;
    /** @hide */
    public static final int MODE_IN_CALL            = 2;
    /** @hide */
    public static final int MODE_IN_COMMUNICATION   = 3;
    /** @hide */
    public static final int MODE_CALL_SCREENING     = 4;
    /** @hide */
    public static final int MODE_CALL_REDIRECT     = 5;
    /** @hide */
    public static final int MODE_COMMUNICATION_REDIRECT  = 6;
    /** @hide */
    public static final int NUM_MODES               = 7;

 
 */

enum AudioManagerMode {
  MODE_INVALID(-2),
  MODE_CURRENT(-1),
  MODE_NORMAL(0),
  MODE_RINGTONE(1),
  MODE_IN_CALL(2),
  MODE_IN_COMMUNICATION(3),
  MODE_CALL_SCREENING(4),
  MODE_CALL_REDIRECT(5),
  MODE_COMMUNICATION_REDIRECT(6),
  NUM_MODES(7);

  final int value;
  const AudioManagerMode(this.value);

  static AudioManagerMode fromValue(int val) {
    return values.firstWhere((e) => e.value == val);
  }
}
