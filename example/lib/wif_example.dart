// ignore_for_file: avoid_print

import 'package:than_pkg_android/than_pkg_android.dart';

void main() async {
  final handler = ThanPkgAndroid.getInstance.wifiHandler;

  final details = await handler.getWifiDetails();

  print('ThanDev: $details');

  //ThanDev: WifiDetail(mainWifiIp: 10.145.108.47, wifiBroadcastAddress: 10.145.108.255, wifiSSid: Unknown, ipdAddressList: [10.145.108.47, 10.21.152.126])
}
