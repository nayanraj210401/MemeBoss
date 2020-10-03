package com.mb.memeboss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class onInternet extends AppCompatActivity {
    // to check if we are connected to Network
    boolean isConnected = true;
    Button checker ;
    // to check if we are monitoring Network
    private boolean monitoringConnectivity = false;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_internet);

        checker = findViewById(R.id.check);
      checker.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              checkConnectivity();
          }
      });
    }
    private ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            isConnected = true;
            Log.d("TAG", "INTERNET CONNECTED");
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        @Override
        public void onLost(Network network) {
            isConnected = false;
            Log.d("TAG", "INTERNET LOST");
            Toast.makeText(getApplicationContext(),"Internet Lost",Toast.LENGTH_SHORT).show();
        }
    };
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
            Toast.makeText(getApplicationContext(),"No Network !",Toast.LENGTH_SHORT).show();
// if Network is not connected we will register a network callback to  monitor network
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build(), connectivityCallback);
            monitoringConnectivity = true;
        }

    }
}