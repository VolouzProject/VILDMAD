package com.example.mth.vildmad;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.Task;
import com.ibm.cloud.appid.android.api.AppID;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{





    private final static String authTenantId = "12544206-9d74-4d25-9526-0e8a89cd4d8b";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_login:
                    //startActivity( new Intent(MainActivity.this, LoginActivity.class));
                case R.id.navigation_myfindings:
                    //startActivity( new Intent(MainActivity.this, DisplayMap.class));
                    return true;
                case R.id.navigation_landscape:
                    startActivity( new Intent(MainActivity.this, MapsActivity.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppID.getInstance().initialize(getApplicationContext(), authTenantId, AppID.REGION_UK);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



    }




}
