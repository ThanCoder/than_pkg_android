// ignore_for_file: public_member_api_docs, sort_constructors_first

class MediaFile {
  const MediaFile({
    required this.id,
    required this.name,
    required this.size,
    required this.uri,
    required this.path,
    required this.duration,
    required this.dateAdded,
    required this.dateModified,
    required this.dateTaken,
  });

  final int id;
  final String name;
  final int size;
  final String uri;
  final String path;
  final Duration duration;

  /// MediaStore.DATE_ADDED
  /// Unix timestamp in seconds.
  final DateTime dateAdded;

  /// MediaStore.DATE_MODIFIED
  /// Unix timestamp in seconds.
  final DateTime dateModified;

  /// MediaStore.DATE_TAKEN
  /// Unix timestamp in milliseconds.
  final DateTime? dateTaken;

  factory MediaFile.fromMap(Map<dynamic, dynamic> map) {
    final dateAdded = (map['dateAdded'] as int?) ?? 0;
    final dateModified = (map['dateModified'] as int?) ?? 0;
    final dateTaken = (map['dateTaken'] as int?) ?? 0;

    return MediaFile(
      id: map['id'] as int,
      name: map['name'] as String,
      size: map['size'] as int,
      uri: map['uri'] as String,
      path: map['path'] as String,

      duration: Duration(milliseconds: (map['duration'] as int?) ?? 0),

      // DATE_ADDED: seconds → milliseconds
      dateAdded: DateTime.fromMillisecondsSinceEpoch(dateAdded * 1000),

      // DATE_MODIFIED: seconds → milliseconds
      dateModified: DateTime.fromMillisecondsSinceEpoch(dateModified * 1000),

      // DATE_TAKEN: already milliseconds
      dateTaken: dateTaken == 0
          ? null
          : DateTime.fromMillisecondsSinceEpoch(dateTaken),
    );
  }

  @override
  String toString() {
    return 'MediaFile('
        'id: $id, '
        'name: $name, '
        'size: $size, '
        'uri: $uri, '
        'path: $path, '
        'duration: $duration, '
        'dateAdded: $dateAdded, '
        'dateModified: $dateModified, '
        'dateTaken: $dateTaken'
        ')';
  }
}
