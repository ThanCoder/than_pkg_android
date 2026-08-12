/*
{path: /storage/emulated/0/test.opus, name: test.opus, extension: opus, size: 2938855, lastModified: 1779944023342, duration: 174934, durationMs: 174934, bitrate: 134398, sampleRate: 48000, mimeType: audio/ogg, hasAudio: yes, hasVideo: null, title: Better Days Ahead – Uplifting Melodic EDM Pop | Love Song | Elara June × DT, artist: DT, album: Better Days Ahead – Uplifting Melodic EDM Pop | Love Song | Elara June × DT, albumArtist: null, composer: null, genre: Music, year: null, date: 20260102, discNumber: null, trackNumber: null}
 */

class AudioFileInfo {
  final String path;
  final String name;
  final String extension;
  final int size;
  final int lastModified;
  final int duration;
  final int durationMs;
  final int bitrate;
  final int sampleRate;
  final int date;
  final String mimeType;
  final String hasAudio;
  final String title;
  final String artist;
  final String album;
  final String albumArtist;
  final String composer;
  final String genre;
  final String year;
  final String trackNumber;
  const AudioFileInfo({
    required this.path,
    required this.name,
    required this.extension,
    required this.size,
    required this.lastModified,
    required this.duration,
    required this.durationMs,
    required this.bitrate,
    required this.sampleRate,
    required this.date,
    required this.mimeType,
    required this.hasAudio,
    required this.title,
    required this.artist,
    required this.album,
    required this.albumArtist,
    required this.composer,
    required this.genre,
    required this.year,
    required this.trackNumber,
  });

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'path': path,
      'name': name,
      'extension': extension,
      'size': size,
      'lastModified': lastModified,
      'duration': duration,
      'durationMs': durationMs,
      'bitrate': bitrate,
      'sampleRate': sampleRate,
      'date': date,
      'mimeType': mimeType,
      'hasAudio': hasAudio,
      'title': title,
      'artist': artist,
      'album': album,
      'albumArtist': albumArtist,
      'composer': composer,
      'genre': genre,
      'year': year,
      'trackNumber': trackNumber,
    };
  }

  factory AudioFileInfo.fromMap(Map<String, dynamic> map) {
    return AudioFileInfo(
      path: map['path'] ?? '',
      name: map['name'] ?? '',
      extension: map['extension'] ?? '',
      size: int.tryParse(map['size']) ?? -1,
      lastModified: int.tryParse(map['lastModified']) ?? -1,
      duration: int.tryParse(map['duration']) ?? -1,
      durationMs: int.tryParse(map['durationMs']) ?? -1,
      bitrate: int.tryParse(map['bitrate']) ?? -1,
      sampleRate: int.tryParse(map['sampleRate']) ?? -1,
      date: int.tryParse(map['date']) ?? -1,
      mimeType: map['mimeType'] ?? '',
      hasAudio: map['hasAudio'] ?? '',
      title: map['title'] ?? '',
      artist: map['artist'] ?? '',
      album: map['album'] ?? '',
      albumArtist: map['albumArtist'] ?? '',
      composer: map['composer'] ?? '',
      genre: map['genre'] ?? '',
      year: map['year'] ?? '',
      trackNumber: map['trackNumber'] ?? '',
    );
  }

  @override
  String toString() {
    return 'AudioFileInfo(path: $path, name: $name, extension: $extension, size: $size, lastModified: $lastModified, duration: $duration, durationMs: $durationMs, bitrate: $bitrate, sampleRate: $sampleRate, date: $date, mimeType: $mimeType, hasAudio: $hasAudio, title: $title, artist: $artist, album: $album, albumArtist: $albumArtist, composer: $composer, genre: $genre, year: $year, trackNumber: $trackNumber)';
  }
}
