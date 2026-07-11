import 'package:flutter_test/flutter_test.dart';
import 'package:than_pkg_android/than_pkg_android.dart';
import 'package:than_pkg_android/than_pkg_android_platform_interface.dart';
import 'package:than_pkg_android/than_pkg_android_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockThanPkgAndroidPlatform
    with MockPlatformInterfaceMixin
    implements ThanPkgAndroidPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final ThanPkgAndroidPlatform initialPlatform = ThanPkgAndroidPlatform.instance;

  test('$MethodChannelThanPkgAndroid is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelThanPkgAndroid>());
  });

  test('getPlatformVersion', () async {
    ThanPkgAndroid thanPkgAndroidPlugin = ThanPkgAndroid();
    MockThanPkgAndroidPlatform fakePlatform = MockThanPkgAndroidPlatform();
    ThanPkgAndroidPlatform.instance = fakePlatform;

    expect(await thanPkgAndroidPlugin.getPlatformVersion(), '42');
  });
}
