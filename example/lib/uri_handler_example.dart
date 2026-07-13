// ignore_for_file: avoid_print

import 'package:than_pkg_android/than_pkg_android.dart';

void main() async {
  final handler = ThanPkgAndroid.getInstance.uriHandler;

  await handler.copyContentToFile(
    '[content://com.android.fileexplorer.myprovider/external_files]',
    '[output]',
  );
  await handler.moveContentToFile(
    '[content://com.android.fileexplorer.myprovider/external_files]',
    '[output]',
  );
}
