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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends Activity {
    //    public static final String IMAGE_URL = "http://i2.hoopchina.com" +
    //            ".cn/blogfile/201506/02/BbsImg143322881272098_418*626.png";
    public static final String IMAGE_URL = "http://ips.chotee" +
            ".com/wp-content/uploads/2015/news/2605/macbookpro.jpg";
    private ProgressBar progressBar;
    private ImageView interImageView;
    private ImageView picassoImageView;
    private LruCache<String, Bitmap> cache;
    private Button mLoadImageButton;
    private Set<String> mWaitToBeLoad;
    private BroadcastReceiver receiver;
    private EditText imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWaitToBeLoad = new HashSet<>();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        interImageView = (ImageView) findViewById(R.id.inter_image_view);
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
        //        new ImageLoadAsyncTask().execute("http://www.baidu.com/img/bdlogo.png");
        //        new ProgressBarAsyncTask(this).execute();
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
                interImageView.setImageResource(R.drawable.error);
                mWaitToBeLoad.add(url);
            } else {
                interImageView.setImageBitmap(bitmap);
                cache.put(url, bitmap);
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
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
                    int a = conn.getContentLength();
                    progressBar.setMax(a);
                    byte[] b = new byte[a];
                    is = conn.getInputStream();
                    for (int tmp, i = 0; (tmp = is.read()) != -1; ) {
                        b[i++] = (byte) tmp;
                        onProgressUpdate(i);
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
