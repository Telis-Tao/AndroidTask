package com.example.telis.androidtask;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 圆形相册，插件直接使用com.example.telis.androidtask
 * .MyImageView，在hyman:background_src属性下引用想使用的图就可以将其变为圆形，很方便。
 * <p/>
 * 这里我尝试了很多种方法：
 * 1.直接用BITMAP加Canvas，然后用Xfermode来画图，结果会导致需要透明的地方背景是黑色，这是由于BITMAP在存储透明背景时造成的，没有很好的解决办法。
 * 2.采用原生的ImageView，在内存中直接新建一个bitmap，然后用canvas上把DST,SRC都画了也可以实现
 * 3.也就是我现在使用的方案，新建一个ImageView继承ImageView,新建了属性hyman:background_src需要被变成圆形图标的背景,然后重写onDraw,
 * onMeasure方法，这里我只写了wrap_content的方法，没有写match_parent的。
 * Created by Telis on 2015/6/2.
 */
public class MyImageView extends ImageView {
    private Bitmap backgroundSrc;
    private Paint p;
    private Bitmap mask;
    private int srcLength;

    public MyImageView(Context context) {
        this(context, null);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyImageView);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.MyImageView_background_src:
                    BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
                    backgroundSrc = drawable.getBitmap();
                    srcLength = backgroundSrc.getWidth();
                    break;
            }
        }
        a.recycle();
        init();
    }

    private void init() {
        p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        mask = BitmapFactory.decodeResource(getResources(), R.drawable.mask);
        int width = mask.getWidth();
        options.inSampleSize = width / srcLength;
        options.inJustDecodeBounds = false;
        mask = BitmapFactory.decodeResource(getResources(), R.drawable.mask);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = Bitmap.createBitmap(srcLength, srcLength, Bitmap.Config
                .ARGB_8888);
        Canvas tmp = new Canvas(bitmap);
        tmp.drawBitmap(backgroundSrc, 0, 0, null);
        tmp.drawBitmap(mask, 0, 0, p);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.makeMeasureSpec(srcLength, MeasureSpec.AT_MOST);
        int height = width;
        setMeasuredDimension(width, height);
        //        super.onMeasure(width, height);
    }
}
