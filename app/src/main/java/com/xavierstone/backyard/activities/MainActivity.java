package com.xavierstone.backyard.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.xavierstone.backyard.R;

/*
Page the app opens to, asks permissions
 */

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    // Package name
    public static String PACKAGE_NAME;

    // Permission managment
    public static int INITIAL_PERMISSION_REQUEST=0;
    public static final String[] permissions = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PACKAGE_NAME = this.getPackageName();
    }

    @Override
    protected void onStart() {
        super.onStart();

        ActivityCompat.requestPermissions(MainActivity.this, permissions, INITIAL_PERMISSION_REQUEST);

        // Rest of code is in permission result functions
    }

    // This function waits for a result from the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        // Transfer control to Home Activity
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
