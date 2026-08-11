import 'package:flutter/services.dart';
// import 'index.dart';

class AudioInfoHandler {
  final MethodChannel _channel;
  final String _key;
  const AudioInfoHandler(this._channel, {this._key = 'AudioHandler'});

  String _getMethod(String methodName) => '$_key/$methodName';
}

/*
// Audio state
            "getAudioInfo" -> {
                getAudioInfo(ctx, result)
            }

            "getVolumeInfo" -> {
                getVolumeInfo(ctx, result)
            }

            "getAudioDevices" -> {
                getAudioDevices(ctx, result)
            }

            "isMusicActive" -> {
                isMusicActive(ctx, result)
            }

            "isSpeakerphoneOn" -> {
                isSpeakerphoneOn(ctx, result)
            }

            // Volume
            "getVolume" -> {
                getVolume(ctx, call, result)
            }

            "setVolume" -> {
                setVolume(ctx, call, result)
            }
 */
