package com.example.telis.androidtask;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Telis on 2015/12/19.
 */
public class RotateImageView extends ImageView {
    private static final String TAG = "RotateImageView";
    private Bitmap mBitmap;
    private Matrix mMatrix;
    private int duration = 300;
    private ValueAnimator mRotateAnimator;
    private int mCurrentRotate;

    public RotateImageView(Context context) {
        this(context, null);
    }

    public RotateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RotateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMatrix = new Matrix();
        mCurrentRotate = 0;
        //        BitmapDrawable mDrawable = (BitmapDrawable) getDrawable();
        //        mBitmap = mDrawable.getBitmap();
    }


    public void rotate() {
        if (mBitmap == null) {
            BitmapDrawable mDrawable = (BitmapDrawable) getDrawable();
            mBitmap = mDrawable.getBitmap();
        }
        mRotateAnimator = ObjectAnimator.ofFloat(this, "ss", 0, 90);
        mRotateAnimator.setDuration(duration);
        mRotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                float rotateDegree = mCurrentRotate + value;
                Log.i(TAG, "onAnimationUpdate: " + rotateDegree);
                mMatrix.setRotate(mCurrentRotate + value);
                setImageBitmap(Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap
                                .getHeight(),
                        mMatrix, true));
            }
        });
        mRotateAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentRotate = (mCurrentRotate + 90) % 360;
            }
        });
        mRotateAnimator.start();
    }
}
