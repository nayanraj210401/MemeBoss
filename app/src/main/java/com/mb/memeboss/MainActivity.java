package com.mb.memeboss;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stephentuso.welcome.WelcomeHelper;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    // to check if we are connected to Network
    boolean isConnected = true;
    WelcomeHelper welcomeScreen;
    String newVersion = "";
    // to check if we are monitoring Network
    private FirebaseAnalytics mFirebaseAnalytics;
    private String appUrl = null , updateLink = null;
    private boolean monitoringConnectivity = false;
    private boolean development = false;
    private int SETTINGS_ACTION = 1;
    @Override
    protected void onResume() {
        super.onResume();
        checkConnectivity();
    }

    @Override
    protected void onPause() {
        // if network is being moniterd then we will unregister the network callback
        if (monitoringConnectivity) {
            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;
        }
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("data").document("admindata");
        final String[] tempAppUrl = new String[1];
        final String[] tempupdateUrl = new String[1];
        final boolean[] tempDev = new boolean[1];
        documentReference
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
//                        Log.d("TAG", "DocumentSnapshot data: " + document.getString("url"));
                        tempAppUrl[0] = document.getString("url");
                        tempDev[0] = document.getBoolean("developement");
                        appUrl = tempAppUrl[0];
                        development = tempDev[0];
                        tempupdateUrl[0] = document.getString("update_app");
                        Log.d("TAG VALUES", "The appurl INSIDE " + tempAppUrl[0] + " "+document.getString("update_app"));
                        updateLink = tempupdateUrl[0];
                        if (development) {
                            startActivity(new Intent(getApplicationContext(), developmentMode.class));
                            finish();
                        }
                    } else {
                        Log.d("Data", "No such document");
                    }
                } else {
                    Log.d("Data", "get failed with ", task.getException());
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemePreferenceActivity.onActivityCreateSetTheme(this);

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_notifications)
                .build();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        welcomeScreen = new WelcomeHelper(this, WelcomeActivitys.class);
        welcomeScreen.show(savedInstanceState);

//        Log.d("TAG","The data"+appUrl+" Development :"+development);

        checkConnectivity();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        welcomeScreen.onSaveInstanceState(outState);
    }

    // Method to check network connectivity in Main Activity
    private void checkConnectivity() {
        // here we are getting the connectivity service from connectivity manager
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);

        // Getting network Info
        // give Network Access Permission in Manifest
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        // isConnected is a boolean variable
        // here we check if network is connected or is getting connected
        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        if (!isConnected) {
            // SHOW ANY ACTION YOU WANT TO SHOW
            // WHEN WE ARE NOT CONNECTED TO INTERNET/NETWORK
            Log.d("TAG", " NO NETWORK!");
            startActivity(new Intent(this, onInternet.class));
            finish();
// if Network is not connected we will register a network callback to  monitor network
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build(), connectivityCallback);
            monitoringConnectivity = true;
        }

    }

    private ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            isConnected = true;
            Log.d("TAG", "INTERNET CONNECTED");

        }

        @Override
        public void onLost(Network network) {
            isConnected = false;
            Log.d("TAG", "INTERNET LOST");
            startActivity(new Intent(getApplicationContext(), onInternet.class));
            finish();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.shareTo:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, appUrl);
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                break;
            case R.id.lsc:
                startActivity(new Intent(this, OssLicensesMenuActivity.class));
                break;
            case R.id.dev:
                startActivity(new Intent(this, devPage.class));
                break;
            case R.id.update:
                Toast.makeText(this,"Checking...",Toast.LENGTH_LONG).show();
                new GetVersionCode().execute();
                break;
            case R.id.settings:
                String[] colors = {"Maroon Red", "Choco White", "Pretty violet", "Minty Green","Sunny Yellow"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Pick a Theme");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        switch (which){
                            case 0:
                                ThemePreferenceActivity.changeToTheme(MainActivity.this,ThemePreferenceActivity.THEME_DEFAULT);
                                break;
                            case 1:
                                ThemePreferenceActivity.changeToTheme(MainActivity.this,ThemePreferenceActivity.THEME_WHITE);
                                break;
                            case 2:
                                ThemePreferenceActivity.changeToTheme(MainActivity.this,ThemePreferenceActivity.THEME_BLUE);
                                break;
                            case 3:
                                ThemePreferenceActivity.changeToTheme(MainActivity.this,ThemePreferenceActivity.THEME_GREEN);
                                break;
                            case 4:
                                ThemePreferenceActivity.changeToTheme(MainActivity.this,ThemePreferenceActivity.THEME_YELLOW);
                                break;
                        }

                    }
                });
                builder.show();
                break;
            case R.id.thridPartyLicenses:
                // launch thrid
                startActivity(new Intent(this,ScrollingActivity.class));
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            Log.d("Delete","Cache Delete");
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
//        Toast.makeText(,"deleting Cache",Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onStop() {
        super.onStop();
//        deleteCache(getApplicationContext());
//        Log.d("Delete","Cache Delete in onStop" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteCache(getApplicationContext());
        Log.d("Delete","Cache Delete in onDestroy" );
    }

    class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName() + "&hl=it")
                        .timeout(1500)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }


        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);

            String currentVersion = "";
            try {
                currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Log.d("CURRENT","Current Version: "+currentVersion+"onlineVersion: "+onlineVersion);

            if (!currentVersion.equalsIgnoreCase(onlineVersion)) {
                //show dialog
                Log.d("ICURRENT","Current Version: "+currentVersion+"onlineVersion: "+onlineVersion);
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert
                        .setTitle("Updated app available!")
                        .setMessage("Want to update app?")
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    Toast.makeText(getApplicationContext(), "App is in BETA version cannot update", Toast.LENGTH_SHORT).show();
                                    Log.d("link","link"+updateLink);
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(updateLink+"")));
                                } catch (ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        })
                        .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert);
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
            }
        }
    }

}

