// ignore_for_file: public_member_api_docs, sort_constructors_first
class AppInfo {
  final String packageName;
  final String appName;
  final int versionName;
  final int versionCode;
  final int firstInstallTime;
  final int lastUpdateTime;
  final String sourceDir;
  final String publicSourceDir;
  final String dataDir;
  final String nativeLibraryDir;
  final String installer;
  const AppInfo({
    required this.packageName,
    required this.appName,
    required this.versionName,
    required this.versionCode,
    required this.firstInstallTime,
    required this.lastUpdateTime,
    required this.sourceDir,
    required this.publicSourceDir,
    required this.dataDir,
    required this.nativeLibraryDir,
    required this.installer,
  });

  factory AppInfo.fromMap(Map<String, dynamic> map) {
    return AppInfo(
      packageName: map['packageName'] ?? '',
      appName: map['appName'] ?? '',
      versionName: map['versionName'] ?? '',
      versionCode: map['versionCode'] ?? -1,
      firstInstallTime: map['firstInstallTime'] ?? -1,
      lastUpdateTime: map['lastUpdateTime'] ?? -1,
      sourceDir: map['sourceDir'] ?? '',
      publicSourceDir: map['publicSourceDir'] ?? '',
      dataDir: map['dataDir'] ?? '',
      nativeLibraryDir: map['nativeLibraryDir'] ?? '',
      installer: map['installer'] ?? '',
    );
  }

  @override
  String toString() {
    return 'AppInfo(packageName: $packageName, appName: $appName,versionName $versionName versionCode: $versionCode, firstInstallTime: $firstInstallTime, lastUpdateTime: $lastUpdateTime, sourceDir: $sourceDir, publicSourceDir: $publicSourceDir, dataDir: $dataDir, nativeLibraryDir: $nativeLibraryDir, installer: $installer)';
  }
}
