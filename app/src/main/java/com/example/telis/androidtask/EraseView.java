package com.example.telis.androidtask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Telis on 2015/6/6.
 */
public class EraseView extends View {
    private Bitmap srcBitmap;
    private Bitmap maskBitmap;
    private Paint p;
    private float x, y, preX, preY;
    private Canvas maskCanvas;
    private Path path;

    public EraseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint();
        p.setColor(Color.BLACK);
        p.setAntiAlias(true);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(20);
        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        maskBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap
                .Config.ARGB_8888);
        maskCanvas = new Canvas(maskBitmap);
        maskCanvas.drawColor(Color.DKGRAY);
        path = new Path();
    }

    public EraseView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(srcBitmap.getWidth(), srcBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(srcBitmap, 0, 0, null);
        canvas.drawBitmap(maskBitmap, 0, 0, null);
        maskCanvas.drawPath(path, p);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.reset();
                preX = x;
                preY = y;
                path.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                //                Log.i("EraseView", String.valueOf(x) + "-" + String.valueOf(y));
                path.quadTo(preX, preY, (preX + x) / 2, (preY + y) / 2);
                preX = x;
                preY = y;
                invalidate();
                break;
        }
        return true;
    }
}
