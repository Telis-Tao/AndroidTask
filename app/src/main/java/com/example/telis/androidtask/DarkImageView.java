package com.example.telis.androidtask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by Telis on 2015/12/29.
 */
public class DarkImageView extends Button {
    private int COVER_ALPHA = 48;
    private Paint mPressedPaint;
    private Paint mUnPressedPaint;
    private Bitmap bitmap;
    private boolean pressed = false;

    public DarkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void init() {
        mPressedPaint = new Paint();
        mPressedPaint.setStyle(Paint.Style.FILL);
        mPressedPaint.setColor(Color.BLACK);
        mPressedPaint.setAlpha(COVER_ALPHA);
        mPressedPaint.setAntiAlias(true);
        mUnPressedPaint = new Paint();
        mUnPressedPaint.setStyle(Paint.Style.FILL);
        Drawable drawable = getBackground();
        bitmap = ((BitmapDrawable) drawable).getBitmap();
        mPressedPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //        if (pressed) {
        //            canvas.drawBitmap(bitmap, 0, 0, mPressedPaint);
        //        } else {
        //            canvas.drawBitmap(bitmap, 0, 0, mUnPressedPaint);
        //        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressed = true;
                invalidate();
                setFilter();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                pressed = false;
                invalidate();
                removeFilter();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置滤镜
     */
    private void setFilter() {
        //先获取设置的src图片
        Drawable drawable = getBackground();
        //当src图片为Null，获取背景图片
        if (drawable == null) {
            drawable = getBackground();
        }
        if (drawable != null) {
            //设置滤镜
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }
    }

    /**
     * 清除滤镜
     */
    private void removeFilter() {
        //先获取设置的src图片
        Drawable drawable = getBackground();
        //当src图片为Null，获取背景图片
        if (drawable == null) {
            drawable = getBackground();
        }
        if (drawable != null) {
            //清除滤镜
            drawable.clearColorFilter();
        }
    }
}
