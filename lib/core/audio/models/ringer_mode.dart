// ignore_for_file: constant_identifier_names, dangling_library_doc_comments, slash_for_doc_comments

/**
 public int getRingerMode()
 
Returns the current ringtone mode.
Returns:
The current ringtone mode, one of RINGER_MODE_NORMAL, RINGER_MODE_SILENT, or RINGER_MODE_VIBRATE.
See Also:
setRingerMode(int)



Ringer mode that will be silent and will not vibrate. (This overrides the vibrate setting.)
See Also:
setRingerMode(int),
getRingerMode()

public static final int RINGER_MODE_SILENT = 0;

    /**
     * Ringer mode that will be silent and will vibrate. (This will cause the
     * phone ringer to always vibrate, but the notification vibrate to only
     * vibrate if set.)
     *
     * @see #setRingerMode(int)
     * @see #getRingerMode()
     */
    public static final int RINGER_MODE_VIBRATE = 1;

    /**
     * Ringer mode that may be audible and may vibrate. It will be audible if
     * the volume before changing out of this mode was audible. It will vibrate
     * if the vibrate setting is on.
     *
     * @see #setRingerMode(int)
     * @see #getRingerMode()
     */
    public static final int RINGER_MODE_NORMAL = 2;
    
 */

enum RingerMode {
  RINGER_MODE_SILENT(0),
  RINGER_MODE_VIBRATE(1),
  RINGER_MODE_NORMAL(2);

  final int value;
  const RingerMode(this.value);

  static RingerMode fromValue(int val) {
    return values.firstWhere((e) => e.value == val);
  }
}
