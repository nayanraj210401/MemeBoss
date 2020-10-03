package com.mb.memeboss;

import android.os.Bundle;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.ParallaxPage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;
import com.stephentuso.welcome.WelcomeHelper;

public class WelcomeActivitys extends WelcomeActivity {





    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultTitleTypefacePath("Montserrat-Bold.ttf")
                .defaultHeaderTypefacePath("Montserrat-Bold.ttf")

                .page(new BasicPage(R.drawable.ic_baseline_signal_wifi_off_24,
                        "Welcome",
                        "An Android library for onboarding, instructional screens, and more")
                        .background(R.color.colorPrimaryDark)
                )

                .page(new BasicPage(R.drawable.ic_baseline_account_box_24,
                        "Simple to use",
                        "Add a welcome screen to your app with only a few lines of code.")
                        .background(R.color.colorPrimary)
                )

//                .page(new ParallaxPage(R.layout.parallax_example,
//                        "Easy parallax",
//                        "Supply a layout and parallax effects will automatically be applied")
//                        .lastParallaxFactor(2f)
//                        .background(R.color.purple_background)
//                )

                .page(new BasicPage(R.drawable.ic_dashboard_black_24dp,
                        "Customizable",
                        "All elements of the welcome screen can be customized easily.")
                        .background(R.color.colorAccent)
                )

                .swipeToDismiss(true)
                .exitAnimation(android.R.anim.fade_out)
                .build();
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//         welcomeScreen =new WelcomeHelper(this,WelcomeActivitys.class);
//        welcomeScreen.show(savedInstanceState);
//    }
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        welcomeScreen.onSaveInstanceState(outState);
//    }
    public static String welcomeKey() {
        return "WelcomeScreen";
    }
}
