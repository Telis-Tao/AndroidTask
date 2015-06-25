package com.example.telis.androidtask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * 有双游标的ProgressBar
 * Created by Telis on 2015/6/25.
 */
public class DoubleCursorProgressBar extends RelativeLayout {
    public DoubleCursorProgressBar(Context context) {
        this(context, null);
    }

    public DoubleCursorProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoubleCursorProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        addCursor();
        addListener();
    }

    private void addListener() {
        mLeftButton.setOnTouchListener(new OnTouchListener() {
            float now;
            float former;
            float distance;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        former = event.getRawX();
                        distance = former - v.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        now = event.getRawX();
                        float x = now - distance;
                        if (x + v.getWidth() + mMinCursorDistance > mRightButton.getX()) {
                            moveTo(mRightButton, x + v.getWidth() + mMinCursorDistance);
                        }
                        float min = Math.min(x, mRightButton.getX() - v.getWidth());
                        moveTo(v, min);
                        break;
                }
                return false;
            }
        });
        mRightButton.setOnTouchListener(new OnTouchListener() {
            float now;
            float former;
            float distance;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        former = event.getRawX();
                        distance = former - v.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        now = event.getRawX();
                        float x = now - distance;
                        if (x - mLeftButton.getWidth() - mMinCursorDistance < mLeftButton.getX()) {
                            moveTo(mLeftButton, x - mLeftButton.getWidth() - mMinCursorDistance);
                        }
                        float max = Math.max(x, mLeftButton.getX() + v.getWidth());
                        moveTo(v, max);
                        break;
                }
                return false;
            }
        });
    }

    private void moveTo(View button, float x) {
        if (x < 0) {
            button.setX(0);
        } else if (x > getWidth() - button.getWidth()) {
            button.setX(getWidth() - button.getWidth());
        } else {
            button.setX(x);
        }
        invalidate();
    }

    public void setLeftCursorPosition(int x) {
        moveTo(mLeftButton, x);
    }

    public void setRightCursorPosition(int x) {
        moveTo(mRightButton, x);
    }

    private void addCursor() {
        mLeftButton = new ImageButton(getContext());
        mLeftButton.setBackgroundResource(mCursorResource);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_LEFT, TRUE);
        mLeftButton.setLayoutParams(params);
        addView(mLeftButton);
        mRightButton = new ImageButton(getContext());
        mRightButton.setBackgroundResource(mCursorResource);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_RIGHT, TRUE);
        mRightButton.setLayoutParams(params);
        addView(mRightButton);
    }

    private void init() {
        mBackgroundColor = Color.DKGRAY;
        mFrontColor = Color.BLUE;
        mCursorResource = R.drawable.bar;
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStrokeWidth(mStrokeWidth);
        mFrontPaint = new Paint();
        mFrontPaint.setColor(mFrontColor);
        mFrontPaint.setStrokeWidth(mStrokeWidth);
    }

    public int getmFrontColor() {
        return mFrontColor;
    }

    public int getmBackgroundColor() {
        return mBackgroundColor;
    }

    private int mBackgroundColor;
    private int mFrontColor;
    private ImageButton mLeftButton;
    private ImageButton mRightButton;
    private Paint mBackgroundPaint;
    private Paint mFrontPaint;
    private int mMiddleHeight;
    private int mCursorResource;
    private int mStrokeWidth = 20;
    private int mMinCursorDistance = 40;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mMiddleHeight = getHeight() / 2;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawLine(0, mMiddleHeight, mLeftButton.getX() + mLeftButton.getWidth() / 2,
                mMiddleHeight, mBackgroundPaint);
        canvas.drawLine(mLeftButton.getX() + mLeftButton.getWidth() / 2, mMiddleHeight,
                mRightButton.getX() + mRightButton.getWidth() / 2,
                mMiddleHeight, mFrontPaint);
        canvas.drawLine(mRightButton.getX() + mRightButton.getWidth() / 2, mMiddleHeight,
                getRight(),
                mMiddleHeight, mBackgroundPaint);
        super.dispatchDraw(canvas);
    }

    class CursorListener implements OnTouchListener {

        float now;
        float former;
        float distance;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    former = event.getRawX();
                    distance = former - v.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    now = event.getRawX();
                    moveTo(v, now - distance);
                    invalidate();
                    break;
            }
            return false;
        }
    }

}
