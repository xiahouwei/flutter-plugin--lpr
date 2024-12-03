import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:xtm_lpr/xtm_lpr.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String scanResult = 'Unknown';

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(children: [
            Text('扫码结果: $scanResult\n'),
            ElevatedButton(
              child: const Text('扫描'),
              onPressed: () {
                XtmLpr.showScan().then((value) {
                  if (value != null) {
                    setState(() {
                      scanResult = value;
                    });
                  }
                });
              },
            )
          ]),
        ),
      ),
    );
  }
}
