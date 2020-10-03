package com.mb.memeboss;

import androidx.appcompat.app.AppCompatActivity;


import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;

import com.stephentuso.welcome.WelcomeHelper;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onStart() {
        super.onStart();

        TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(new Intent(this, MainActivity.class))
                .addNextIntent(new Intent(this, MainActivity.class))
                .startActivities();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
//        startActivity(new Intent(this,WelcomeActivitys.class));

    }

}