package com.example.telis.androidtask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bm.library.PhotoView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import uk.co.senab.photoview.PhotoViewAttacher;


public class MainActivity extends Activity {
    //    public static final String IMAGE_URL = "http://i2.hoopchina.com" +
    //            ".cn/blogfile/201506/02/BbsImg143322881272098_418*626.png";
    public static final String IMAGE_URL = "http://ips.chotee" +
            ".com/wp-content/uploads/2015/news/2605/macbookpro.jpg";
    private static final String TAG = "MainActivity";
    PhotoViewAttacher mAttacher;
    private ProgressBar progressBar;
    private DarkImageView interImageView;
    private ImageView picassoImageView;
    private LruCache<String, Bitmap> cache;
    private Button mLoadImageButton;
    private Set<String> mWaitToBeLoad;
    private BroadcastReceiver receiver;
    private EditText imageView;
    private DoubleCursorProgressBar mDoubleCursorProgressBar;
    private PhotoView mRotateImageView;
    private PhotoView rotateimageview;
    private CircleImageView imageview;
    private EditText imagesite;
    private Button loadimagebutton;
    private ProgressBar progressbar;
    private ImageView interimageview;
    private MirrorView mirrorview;
    private DoubleCursorProgressBar doublecursorprogressbar;
    private Button test1;
    private Button test2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.test2 = (Button) findViewById(R.id.test2);
        this.test1 = (Button) findViewById(R.id.test1);
        this.doublecursorprogressbar = (DoubleCursorProgressBar) findViewById(R.id
                .double_cursor_progress_bar);
        this.mirrorview = (MirrorView) findViewById(R.id.mirror_view);
        //        this.interimageview = (ImageView) findViewById(R.id.inter_image_view);
        this.progressbar = (ProgressBar) findViewById(R.id.progress_bar);
        this.loadimagebutton = (Button) findViewById(R.id.load_image_button);
        this.imagesite = (EditText) findViewById(R.id.image_site);
        this.imageview = (CircleImageView) findViewById(R.id.image_view);
        this.rotateimageview = (PhotoView) findViewById(R.id.rotate_image_view);
        mWaitToBeLoad = new HashSet<>();
        mDoubleCursorProgressBar = (DoubleCursorProgressBar) findViewById(R.id
                .double_cursor_progress_bar);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        interImageView = (DarkImageView) findViewById(R.id.inter_image_view);
        //        picassoImageView = (ImageView) findViewById(R.id.picasso_image_view);
        mLoadImageButton = (Button) findViewById(R.id.load_image_button);
        imageView = (EditText) findViewById(R.id.image_site);
        imageView.setText(IMAGE_URL);
        //        Picasso.with(this).load(IMAGE_URL).into(picassoImageView);
        receiver = new ConnectionChangeReceiver(new OnNetworkChangeListener() {


            @Override
            public void onNetworkChange() {

                for (String tmp : mWaitToBeLoad) {
                    new ImageLoadAsyncTask().execute(tmp);
                }

            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
        mLoadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageLoadAsyncTask().execute(imageView.getText().toString());
            }
        });


        //test
        Button left = (Button) findViewById(R.id.test1);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoubleCursorProgressBar.setLeftCursor(60);
            }
        });
        Button right = (Button) findViewById(R.id.test2);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoubleCursorProgressBar.setRightCursor(60);
            }
        });

        //        // 从一张图片信息变化到现在的图片，用于图片点击后放大浏览，具体使用可以参照demo的使用
        //        mRotateImageView.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                Info info = mRotateImageView.getInfo();
        //                mRotateImageView.animaFrom(info);
        //            }
        //        });
        // 从现在的图片变化到所给定的图片信息，用于图片放大后点击缩小到原来的位置，具体使用可以参照demo的使用
        //        mRotateImageView.animaTo(info,new Runnable() {
        //            @Override
        //            public void run() {
        //                //动画完成监听
        //            }
        //        });
        //        mAttacher = new PhotoViewAttacher(mRotateImageView);
        interImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ImageLoadAsyncTask extends AsyncTask<String, Integer, Bitmap> {
        private String url;
        private int imageLength;

        public ImageLoadAsyncTask() {
            if (cache == null) {
                int maxMemory = (int) Runtime.getRuntime().maxMemory();
                int mCacheSize = maxMemory / 8;
                cache = new LruCache<String, Bitmap>(mCacheSize) {
                    @Override
                    protected int sizeOf(String key, Bitmap value) {
                        return value.getRowBytes() * value.getHeight();
                    }
                };
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressBar.setVisibility(View.GONE);
            if (bitmap == null) {
                //                interImageView.setImageResource(R.drawable.error);
                mWaitToBeLoad.add(url);
            } else {
                //                interImageView.setImageBitmap(bitmap);
                cache.put(url, bitmap);
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (imageLength != 0) {
                progressBar.setMax(imageLength);
            }
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(1);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap;
            url = params[0];
            bitmap = cache.get(url);
            //            progressBar.setVisibility(View.VISIBLE);
            if (bitmap == null) {
                HttpURLConnection conn;
                InputStream is = null;
                try {
                    URL url = new URL(params[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    imageLength = conn.getContentLength();
                    byte[] b = new byte[imageLength];
                    is = conn.getInputStream();
                    for (int tmp, i = 0, count = 0; (tmp = is.read()) != -1; ) {
                        b[i++] = (byte) tmp;
                        count++;
                        if (count >= imageLength / 20) {
                            count = 0;
                            publishProgress(i);
                        }
                    }
                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return bitmap;
        }
    }

}
