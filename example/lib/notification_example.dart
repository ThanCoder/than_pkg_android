import 'package:than_pkg_android/than_pkg_android.dart';

void main() async {
  if (!await ThanPkgAndroid.getInstance.permissionHandler
      .isNotificationPermission()) {
    await ThanPkgAndroid.getInstance.permissionHandler
        .requestNotificationPermission();
  }
  // simple
  final simple = ThanPkgAndroid.getInstance.simpleNotificationHandler;
  await simple.showProgress('title', 'content');
  await simple.updateProgress(50, '1mb');
  //multi noti
  final noti = ThanPkgAndroid.getInstance.notificationHandler;
  await noti.show(1, title: 'title 1', content: 'content 1');
  await noti.show(2, title: 'title 2', content: 'content 2');
  await noti.updateProgress(1, 50);
  await noti.updateProgress(2, 10);
  await Future.delayed(Duration(seconds: 2));
  await noti.finish(1, title: 'title 1', content: 'finished');
  await noti.finish(2, title: 'title 2', content: 'finished');

  await Future.delayed(Duration(seconds: 2));
  await noti.cancel(1);
  await noti.cancel(2);
}
