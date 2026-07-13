// ignore_for_file: public_member_api_docs, sort_constructors_first
class MediaFile {
  final int id;
  final String name;
  final int size;
  final String uri;
  final String path;
  final Duration duration;

  MediaFile({
    required this.id,
    required this.name,
    required this.size,
    required this.uri,
    required this.path,
    required this.duration,
  });

  factory MediaFile.fromMap(Map<dynamic, dynamic> map) {
    return MediaFile(
      id: map['id'] as int,
      name: map['name'] as String,
      size: map['size'] as int,
      uri: map['uri'] as String,
      path: map['path'] as String,
      duration: Duration(milliseconds: map['duration'] as int),
    );
  }

  @override
  String toString() {
    return 'MediaFile(id: $id, name: $name, size: $size, uri: $uri, path: $path, duration: $duration)';
  }
}
