package com.example.telis.androidtask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
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
    private static final String DEBUG = "double_cursor";
    private int mMax = 100;
    private int mCursorOutsideColor;
    private int mCursorBetweenColor;
    private ImageButton mLeftCursor;
    private ImageButton mRightButton;
    private Paint mCursorOutsidePaint;
    private Paint mCursorBetweenPaint;
    private int mMiddleHeight;
    private int mCursorResourceId;
    private int mStrokeWidth;
    private int mMinCursorDistance;
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

    /**
     * set the cursor image resource
     *
     * @param cursorResourceId
     */
    public void setCursorResourceId(int cursorResourceId) {
        mCursorResourceId = cursorResourceId;
    }

    /**
     * return the max value of the progress bar
     *
     * @return
     */
    public int getMax() {
        return mMax;
    }

    /**
     * set the max value of the progress bar
     *
     * @param max
     */
    public void setMax(int max) {
        mMax = max;
    }

    private void addListener() {
        mLeftCursor.setOnTouchListener(new OnTouchListener() {
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
                        moveTo(mLeftCursor, min);
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
                        if (x - mLeftCursor.getWidth() - mMinCursorDistance < mLeftCursor.getX()) {
                            moveTo(mLeftCursor, x - mLeftCursor.getWidth() - mMinCursorDistance);
                        }
                        float max = Math.max(x, mLeftCursor.getX() + v.getWidth());
                        moveTo(mRightButton, max);
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

    private void addCursor() {
        mLeftCursor = new ImageButton(getContext());
        mLeftCursor.setBackgroundResource(mCursorResourceId);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_LEFT, TRUE);
        mLeftCursor.setLayoutParams(params);
        addView(mLeftCursor);
        mRightButton = new ImageButton(getContext());
        mRightButton.setBackgroundResource(mCursorResourceId);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_RIGHT, TRUE);
        mRightButton.setLayoutParams(params);
        addView(mRightButton);
    }

    /**
     * init the colors, paints.<br>
     * default:<br>
     * outside color : DKGRAY<br>
     * between color : BLUE<br>
     * CursorResource : bar<br>
     * stroke width : 20<br>
     * cursors' distance : 40
     */
    private void init() {
        mCursorOutsideColor = Color.DKGRAY;
        mCursorBetweenColor = Color.BLUE;
        mCursorResourceId = R.drawable.bar;
        mStrokeWidth = 20;
        mCursorOutsidePaint = new Paint();
        mCursorOutsidePaint.setColor(mCursorOutsideColor);
        mCursorOutsidePaint.setStrokeWidth(mStrokeWidth);
        mCursorBetweenPaint = new Paint();
        mCursorBetweenPaint.setColor(mCursorBetweenColor);
        mCursorBetweenPaint.setStrokeWidth(mStrokeWidth);
        mMinCursorDistance = 40;
    }

    /**
     * get color between cursors
     *
     * @return
     */
    public int getCursorBetweenColor() {
        return mCursorBetweenColor;
    }

    public void setCursorBetweenColor(int cursorBetweenColor) {
        mCursorBetweenColor = cursorBetweenColor;
    }

    /**
     * get color outside cursors
     *
     * @return
     */
    public int getCursorOutsideColor() {
        return mCursorOutsideColor;
    }

    public void setCursorOutsideColor(int cursorOutsideColor) {
        mCursorOutsideColor = cursorOutsideColor;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mMiddleHeight = getHeight() / 2;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawLine(0, mMiddleHeight, mLeftCursor.getX() + mLeftCursor.getWidth() / 2,
                mMiddleHeight, mCursorOutsidePaint);
        canvas.drawLine(mLeftCursor.getX() + mLeftCursor.getWidth() / 2, mMiddleHeight,
                mRightButton.getX() + mRightButton.getWidth() / 2,
                mMiddleHeight, mCursorBetweenPaint);
        canvas.drawLine(mRightButton.getX() + mRightButton.getWidth() / 2, mMiddleHeight,
                getRight(),
                mMiddleHeight, mCursorOutsidePaint);
        super.dispatchDraw(canvas);
    }

    /**
     * get left cursor position
     *
     * @return
     */
    public int getLeftCursor() {
        return (int) (mLeftCursor.getX() / getWidth() * mMax);
    }

    /**
     * set left cursor position
     *
     * @param x
     */
    public void setLeftCursor(int x) {
        int realPix = x * getWidth() / mMax;
        Log.d(DEBUG, "" + getWidth());
        if (realPix > mRightButton.getX()) {
            Log.d(DEBUG, "couldn't place left cursor right of right cursor!");
        } else {
            moveTo(mLeftCursor, realPix);
        }
    }

    /**
     * @return right cursor position
     */
    public int getRightCursor() {
        return (int) ((mRightButton.getX() + mRightButton.getWidth()) / getWidth() * mMax);
    }

    /**
     * @param x right cursor position
     */
    public void setRightCursor(int x) {
        int realPix = x * getWidth() / mMax;
        if (realPix < mLeftCursor.getX() + mLeftCursor.getWidth()) {
            Log.d(DEBUG, "couldn't place right cursor left of left cursor!");
        } else {
            moveTo(mRightButton, realPix);
        }
    }

}
