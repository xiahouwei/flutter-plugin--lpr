package xtm.flutter.plugin.xtm_lpr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.shouzhong.scanner.IViewFinder;
import com.shouzhong.scanner.ScannerView;
import com.shouzhong.scanner.Callback;

import io.flutter.plugin.common.MethodChannel.Result;

public class NativeActivity extends AppCompatActivity {

    private ScannerView scannerView;
    private Vibrator vibrator;

    private static final String SCAN_ERROR_CODE_CANCEL = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showScan(this);
        ImageView backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Result result = XtmLprPlugin.getPendingResult();
                if (result != null) {
                    result.error(SCAN_ERROR_CODE_CANCEL, "取消识别拍照", null);
                }
                closeScan();
            }
        });
    }

    private void showScan(Context context) {
        scannerView = findViewById(R.id.sv);
        scannerView.setShouldAdjustFocusArea(true);
        scannerView.setViewFinder(new ViewFinder(context));
        scannerView.setRotateDegree90Recognition(true);
        scannerView.setEnableLicensePlate(true);
        scannerView.setCallback(new Callback() {
            @Override
            public void result(com.shouzhong.scanner.Result scanResult) {
                startVibrator();
                Result result = XtmLprPlugin.getPendingResult();
                if (result != null) {
                    result.success(scanResult.data);
                }
                closeScan();
            }
        });
        scannerView.onResume();
    }

    private void closeScan() {
        finish();
    }

    private void startVibrator() {
        if (vibrator == null)
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
    }

    class ViewFinder2 implements IViewFinder {
        @Override
        public Rect getFramingRect() {
            return new Rect(240, 240, 840, 840);
        }
    }
    class ViewFinder extends View implements IViewFinder {
        private Rect framingRect;
        private float widthRatio = 0.9f;
        private float heightRatio = 0.8f;
        private float heightWidthRatio = 0.5626f;
        private int leftOffset = -1;
        private int topOffset = -1;

        private int laserColor = 0xff008577;
        private int maskColor = 0x60000000;
        private int borderColor = 0xff008577;
        private int borderStrokeWidth = 12;
        private int borderLineLength = 72;

        private Paint laserPaint;
        private Paint maskPaint;
        private Paint borderPaint;

        private int position;

        public ViewFinder(Context context) {
            super(context);
            setWillNotDraw(false);
            laserPaint = new Paint();
            laserPaint.setColor(laserColor);
            laserPaint.setStyle(Paint.Style.FILL);
            maskPaint = new Paint();
            maskPaint.setColor(maskColor);
            borderPaint = new Paint();
            borderPaint.setColor(borderColor);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(borderStrokeWidth);
            borderPaint.setAntiAlias(true);
        }

        @Override
        protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
            updateFramingRect();
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (getFramingRect() == null) {
                return;
            }
            drawViewFinderMask(canvas);
            drawViewFinderBorder(canvas);
            drawLaser(canvas);
        }

        private void drawLaser(Canvas canvas) {
            Rect framingRect = getFramingRect();
            int top = framingRect.top + 10 + position;
            canvas.drawRect(framingRect.left + 10, top, framingRect.right - 10, top + 5, laserPaint);
            position = framingRect.bottom - framingRect.top - 25 < position ? 0 : position + 2;
            //区域刷新
            postInvalidateDelayed(20, framingRect.left + 10, framingRect.top + 10, framingRect.right - 10, framingRect.bottom - 10);
        }

        private void drawViewFinderMask(Canvas canvas) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            Rect framingRect = getFramingRect();
            canvas.drawRect(0, 0, width, framingRect.top, maskPaint);
            canvas.drawRect(0, framingRect.top, framingRect.left, framingRect.bottom, maskPaint);
            canvas.drawRect(framingRect.right, framingRect.top, width, framingRect.bottom, maskPaint);
            canvas.drawRect(0, framingRect.bottom, width, height, maskPaint);
        }

        private void drawViewFinderBorder(Canvas canvas) {
            Rect framingRect = getFramingRect();

            Path path = new Path();
            path.moveTo(framingRect.left, framingRect.top + borderLineLength);
            path.lineTo(framingRect.left, framingRect.top);
            path.lineTo(framingRect.left + borderLineLength, framingRect.top);
            canvas.drawPath(path, borderPaint);

            path.moveTo(framingRect.right, framingRect.top + borderLineLength);
            path.lineTo(framingRect.right, framingRect.top);
            path.lineTo(framingRect.right - borderLineLength, framingRect.top);
            canvas.drawPath(path, borderPaint);

            path.moveTo(framingRect.right, framingRect.bottom - borderLineLength);
            path.lineTo(framingRect.right, framingRect.bottom);
            path.lineTo(framingRect.right - borderLineLength, framingRect.bottom);
            canvas.drawPath(path, borderPaint);

            path.moveTo(framingRect.left, framingRect.bottom - borderLineLength);
            path.lineTo(framingRect.left, framingRect.bottom);
            path.lineTo(framingRect.left + borderLineLength, framingRect.bottom);
            canvas.drawPath(path, borderPaint);
        }

        private synchronized void updateFramingRect() {
            Point viewSize = new Point(getWidth(), getHeight());
            int width = getWidth() * 801 / 1080, height = getWidth() * 811 / 1080;
            width = (int) (getWidth() * widthRatio);
            height = (int) (heightWidthRatio * width);

            int left, top;
            if (leftOffset < 0) {
                left = (viewSize.x - width) / 2;
            } else {
                left = leftOffset;
            }
            if (topOffset < 0) {
                top = (viewSize.y - height) / 2;
            } else {
                top = topOffset;
            }
            framingRect = new Rect(left, top, left + width, top + height);
        }

        @Override
        public Rect getFramingRect() {
            return framingRect;
        }
    }
}