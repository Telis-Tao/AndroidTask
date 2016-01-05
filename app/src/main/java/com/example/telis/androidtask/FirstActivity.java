package com.example.telis.androidtask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Telis on 2015/12/31.
 */
public class FirstActivity extends Activity {
    private android.widget.ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
        setContentView(R.layout.activity_first);
        this.imageview = (ImageView) findViewById(R.id.image_view);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FirstActivity.this, SecondActivity.class);
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
