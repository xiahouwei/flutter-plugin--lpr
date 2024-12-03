import 'dart:async';

import 'package:flutter/services.dart';

class XtmLpr {
  static const MethodChannel _channel = MethodChannel('xtm_lpr.methodChannel');
  static const EventChannel _eventChannel = EventChannel('xtm_lpr.eventChannel');

  static Stream<String> get eventStream {
    return _eventChannel
        .receiveBroadcastStream()
        .map((event) => event as String);
  }

  static Future<String?> showScan() async {
    final String? result = await _channel.invokeMethod('showScan');
    return result;
  }
}
