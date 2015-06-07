package com.example.telis.androidtask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Telis on 2015/6/7.
 * 镜面效果View
 */
public class ReflectView extends View {
    private final Bitmap resultBitmap;
    private Bitmap srcBitmap;
    private Paint mirrorPaint, shaderPaint;
    private Canvas refCanvas;

    public int getShaderLength() {
        return shaderLength;
    }

    public void setShaderLength(int shaderLength) {
        this.shaderLength = shaderLength;
    }

    private int shaderLength = 100;

    public ReflectView(Context context) {
        this(context, null);
    }

    public ReflectView(Context context, AttributeSet attrs) {
        super(context, attrs);

        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        resultBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight()
                + shaderLength, Bitmap.Config.ARGB_8888);
        refCanvas = new Canvas(resultBitmap);
        mirrorPaint = new Paint();
        shaderPaint = new Paint();
        mirrorPaint.setShader(new BitmapShader(srcBitmap, Shader.TileMode.MIRROR, Shader.TileMode
                .MIRROR));
        shaderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        shaderPaint.setShader(new LinearGradient(0, srcBitmap.getHeight(), 0, srcBitmap.getHeight
                () + shaderLength, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.MIRROR));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //        refCanvas.drawBitmap(srcBitmap, 0, 0, null);
        refCanvas.drawRect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight() +
                shaderLength, mirrorPaint);
        refCanvas.drawRect(0, srcBitmap.getHeight(), srcBitmap.getWidth(), srcBitmap.getHeight() +
                shaderLength, shaderPaint);
        canvas.drawBitmap(resultBitmap, 0, 0, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(srcBitmap.getWidth(), srcBitmap.getHeight() + shaderLength);
    }
}
