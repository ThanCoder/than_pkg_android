// ignore_for_file: constant_identifier_names

/*
*/

enum EnviromentType {
  DIRECTORY_MUSIC("Music"),
  DIRECTORY_PODCASTS("Podcasts"),
  DIRECTORY_RINGTONES("Ringtones"),
  DIRECTORY_ALARMS("Alarms"),
  DIRECTORY_NOTIFICATIONS("Notifications"),
  DIRECTORY_PICTURES("Pictures"),
  DIRECTORY_MOVIES("Movies"),
  DIRECTORY_DOWNLOADS("Download"),
  DIRECTORY_DCIM("DCIM"),
  DIRECTORY_DOCUMENTS("Documents"),
  DIRECTORY_SCREENSHOTS("Screenshots"),
  DIRECTORY_AUDIOBOOKS("Audiobooks"),
  DIRECTORY_RECORDINGS("Recordings");

  final String value;
  const EnviromentType(this.value);
}
