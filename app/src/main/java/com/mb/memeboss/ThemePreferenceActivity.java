package com.mb.memeboss;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class ThemePreferenceActivity
{
    private static int sTheme;
    public final static int THEME_DEFAULT = 0;
    public final static int THEME_WHITE = 1;
    public final static int THEME_BLUE = 2;
    public final static int THEME_GREEN = 3;
    public final static int THEME_YELLOW = 4;
    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeToTheme(Activity activity, int theme)
    {
        SharedPreferences sp = activity.getSharedPreferences("theme_settings",MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("key",theme);
        edit.apply();
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity)
    {
        SharedPreferences sp = activity.getSharedPreferences("theme_settings", MODE_PRIVATE);
        sTheme = sp.getInt("key",0);
        switch (sTheme)
        {
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme);
                break;
            case THEME_WHITE:
                activity.setTheme(R.style.AppTheme_White);
                break;
            case THEME_BLUE:
                activity.setTheme(R.style.AppTheme_Blue);
                break;
            case THEME_GREEN:
                activity.setTheme(R.style.AppTheme_Green);
                break;
            case THEME_YELLOW:
                activity.setTheme(R.style.AppTheme_Yellow);
                break;
        }
    }
}  