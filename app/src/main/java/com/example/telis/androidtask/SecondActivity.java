package com.example.telis.androidtask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Telis on 2015/12/31.
 */
public class SecondActivity extends Activity {
    private ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
        setContentView(R.layout.activity_second);
        this.imageview = (ImageView) findViewById(R.id.image_view);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SecondActivity.this, FirstActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
    }
}
