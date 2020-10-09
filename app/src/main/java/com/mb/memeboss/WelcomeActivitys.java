package com.mb.memeboss;
import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;


public class WelcomeActivitys extends WelcomeActivity {


    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultTitleTypefacePath("Montserrat-Bold.ttf")
                .defaultHeaderTypefacePath("Montserrat-Bold.ttf")

                .page(new BasicPage(R.drawable.icon_round,
                        "Welcome to MemeBoss",
                        "This an App gives you random Meme. Swipe to know Features")
                        .background(R.color.first_slide_background)
                )

                .page(new BasicPage(R.drawable.ic_dashboard_black_24dp,
                        "Simple to use",
                        "Scroll memes across the app")
                        .background(R.color.second_slide_background)
                )

                .page(new BasicPage(R.drawable.ic_baseline_account_box_24,
                        "No Login Required",
                        "Free to use and Open Source")
                        .background(R.color.third_slide_buttons)
                )


                .page(new BasicPage(R.drawable.ic_baseline_bookmark_border_24,
                        "Bookmarks",
                        "♥ the meme swipe to save it and you can find it in Bookmarks .")
                        .background(R.color.third_slide_background)
                )


                .page(new BasicPage(R.drawable.ic_baseline_share_24,
                        "Share it",
                        "Share the direct meme .")
                        .background(R.color.fourth_slide_background)
                )

                .page(new BasicPage(R.drawable.ic_baseline_all_inclusive_24,
                        "Thank you to use MemeBoss",
                        "Made with ♥")
                        .background(R.color.custom_slide_background)
                )


                .swipeToDismiss(true)
                .exitAnimation(android.R.anim.fade_out)
                .build();
    }

    public static String welcomeKey() {
        return "WelcomeScreen";
    }
}
