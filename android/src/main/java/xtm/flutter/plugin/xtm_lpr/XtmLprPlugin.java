package xtm.flutter.plugin.xtm_lpr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Vibrator;

import androidx.annotation.NonNull;

import com.shouzhong.scanner.ScannerView;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import android.util.Log;

/** XtmLprPlugin */
public class XtmLprPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, EventChannel.StreamHandler {
  private Context context;
  private MethodChannel channel;

  private EventChannel.EventSink events;

  private Activity activity;


  private static Result pendingResult;

  public static Result getPendingResult() {
    return pendingResult;
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    this.context = flutterPluginBinding.getApplicationContext();
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "xtm_lpr.methodChannel");
    channel.setMethodCallHandler(this);
    EventChannel eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "xtm_lpr.eventChannel");
    eventChannel.setStreamHandler((EventChannel.StreamHandler) this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("showScan")) {
      showScan(result);
    } else {
      result.notImplemented();
    }
  }

  private void showScan(@NonNull Result result) {
    this.activity.runOnUiThread(() -> {
      pendingResult = result;
      Intent intent = new Intent(context, NativeActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
    });
  }

  @Override
  public void onListen(Object arguments, EventChannel.EventSink events) {
    this.events = events;
  }

  @Override
  public void onCancel(Object arguments) {
  }
  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    Log.d("TAG", "==============onAttachedToActivity=========");
    this.activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    Log.d("TAG", "==============onDetachedFromActivityForConfigChanges=========");
    this.activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    Log.d("TAG", "==============onReattachedToActivityForConfigChanges=========");
    this.activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivity() {
    Log.d("TAG", "==============onDetachedFromActivity=========");
    this.activity = null;
  }
}
