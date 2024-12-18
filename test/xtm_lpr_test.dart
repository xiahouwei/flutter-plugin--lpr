import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:xtm_lpr/xtm_lpr.dart';

void main() {
  const MethodChannel channel = MethodChannel('xtm_lpr');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });
}
