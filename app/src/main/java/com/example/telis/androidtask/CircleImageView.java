package com.example.telis.androidtask;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.LruCache;
import android.widget.ImageView;

/**
 * 圆形相册，插件直接使用com.example.telis.androidtask
 * .MyImageView，在hyman:background_src属性下引用想使用的图就可以将其变为圆形，很方便。
 * <p/>
 * 这里我尝试了很多种方法：
 * 1.直接用BITMAP加Canvas，然后用Xfermode来画图，如果一开始画原图，再画MASK的画会没用（不知道为什么），但是一开始画MASK,再画原图的话就可以了
 * 2.采用原生的ImageView，在内存中直接新建一个bitmap，然后用canvas上把DST,SRC都画了也可以实现
 * 3.也就是我现在使用的方案，新建一个ImageView继承ImageView,新建了属性hyman:background_src需要被变成圆形图标的背景,然后重写onDraw,
 * onMeasure方法，这里我只写了wrap_content的方法，没有写match_parent的。
 * Created by Telis on 2015/6/10.
 */
public class CircleImageView extends ImageView {
    private int resId;
    private BitmapFactory.Options options;
    private int radius;
    private static final Paint maskPaint;
    private static final Paint srcPaint;
    private Canvas tmp;
    //    private SoftReference<Bitmap> src;
    //    private Bitmap srcBitmap;
    private Bitmap src;
    private Bitmap bitmap;
    private int length;
    private static LruCache<Integer, Bitmap> cache;

    static {
        int size = (int) (Runtime.getRuntime().maxMemory() / 8);
        int targetSize = size / 8;
        cache = new LruCache<Integer, Bitmap>(targetSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                return value.getByteCount();
            }
        };


        maskPaint = new Paint();
        maskPaint.setColor(Color.BLUE);
        maskPaint.setAntiAlias(true);
        srcPaint = new Paint();
        srcPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    public CircleImageView(Context context) {
        this(context, null);
    }


    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CircleImageView_background_src:
                    resId = a.getResourceId(attr, 0);
                    //                    options = new BitmapFactory.Options();
                    break;
            }
        }
        a.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (options == null) {
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), resId, options);
            int height = options.outHeight;
            int width = options.outWidth;
            int min = Math.min(height, width);
            options.inSampleSize = min / length;
            options.inJustDecodeBounds = false;
        }
        src = cache.get(resId);
        if (src == null) {
            src = BitmapFactory.decodeResource(getResources(), resId,
                    options);
            cache.put(resId, src);
        }
        radius = length / 2;
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_4444);
            tmp = new Canvas(bitmap);
        }
        tmp.drawCircle(radius, radius, radius, maskPaint);
        tmp.drawBitmap(src, radius - src.getWidth() / 2, radius - src.getHeight() / 2, srcPaint);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    public void setBackgroundResId(int backgroundResId) {
        this.resId = backgroundResId;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        length = Math.min(height, width);
        setMeasuredDimension(length, length);
    }

}
