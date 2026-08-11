// ignore_for_file: public_member_api_docs, sort_constructors_first
class AndroidInfo {
  final String version;
  final int sdkInt;
  final String securityPatch;
  final String incremental;
  final String releaseOrCodename;
  const AndroidInfo({
    required this.version,
    required this.sdkInt,
    required this.securityPatch,
    required this.incremental,
    required this.releaseOrCodename,
  });
  factory AndroidInfo.fromMap(Map<String, dynamic> map) {
    return AndroidInfo(
      version: map['version'] ?? '',
      sdkInt: map['sdkInt'] ?? -1,
      securityPatch: map['securityPatch'] ?? '',
      incremental: map['incremental'] ?? '',
      releaseOrCodename: map['releaseOrCodename'] ?? '',
    );
  }

  @override
  String toString() {
    return 'AndroidInfo(version: $version, sdkInt: $sdkInt, securityPatch: $securityPatch, incremental: $incremental, releaseOrCodename: $releaseOrCodename)';
  }
}
