package com.mb.memeboss;

import androidx.appcompat.app.AppCompatActivity;


import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onStart() {
        super.onStart();
////        TaskStackBuilder.create(this)
////                .addNextIntentWithParentStack(new Intent(this, MainActivity.class))
////                .addNextIntent(new Intent(this, MainActivity.class))
////                .startActivities();
////        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView memeBoss;
        Handler handler;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        },380);
    }

}