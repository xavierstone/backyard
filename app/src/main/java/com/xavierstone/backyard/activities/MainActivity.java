package com.xavierstone.backyard.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.xavierstone.backyard.BackyardApplication;
import com.xavierstone.backyard.R;
import com.xavierstone.backyard.db.DBHandler;
import com.xavierstone.backyard.models.User;

/*
Page the app opens to, asks permissions
 */

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    // Package name
    public static String PACKAGE_NAME;

    // Permission managment
    public static int LOCATION_REQUEST=0;
    public static int STORAGE_REQUEST=1;

    private final String[] locationRequest = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION};
    private final String[] storageRequest = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PACKAGE_NAME = this.getPackageName();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for location permission
        if (!checkPermission(MainActivity.this, LOCATION_REQUEST)) {
            // Not enabled, request location permission
            ActivityCompat.requestPermissions(MainActivity.this, locationRequest, LOCATION_REQUEST);
        }else{
            // Yes enabled! Check for storage permission
            if (!checkPermission(MainActivity.this, STORAGE_REQUEST)) {
                // Nope, request it
                ActivityCompat.requestPermissions(MainActivity.this, storageRequest, STORAGE_REQUEST);
            }else{
                // Yay!!! Both permissions! Let's go!
                permissionsResult();
            }
        }

        // Rest of code is in permission result functions
    }

    // This function waits for a result from the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        // Check for requestCode
        if (requestCode == LOCATION_REQUEST){
            // Location was requested; Check to see if storage was granted
            if (checkPermission(this, STORAGE_REQUEST)){
                // Yes!!! Finish this off
                permissionsResult();
            }else{
                // Nope, ask for it
                ActivityCompat.requestPermissions(this, storageRequest, STORAGE_REQUEST);
            }
        }else if (requestCode == STORAGE_REQUEST){
            // Storage was requested
            // Location must have at least been asked in order to get here,
            // so it doesn't matter what they said, we'll take it and go on
            permissionsResult();
        }
    }

    // This function is the final result of the permissions labyrinth
    // Passes control to home activity
    public void permissionsResult() {
        // Transfer control to Home Activity
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    // Handles the actual permission checking
    public static boolean checkPermission(Activity currentActivity, int permissionID) {
        int approved = PackageManager.PERMISSION_GRANTED;
        boolean permGranted = false;

        // If permissionID is location request
        if (permissionID == LOCATION_REQUEST) {
            // Get permission strings
            String coarseLocPerm = Manifest.permission.ACCESS_COARSE_LOCATION;
            String fineLocPerm = Manifest.permission.ACCESS_FINE_LOCATION;

            // Check permission
            permGranted = ( ContextCompat.checkSelfPermission(currentActivity, coarseLocPerm) == approved
                    &&  ContextCompat.checkSelfPermission(currentActivity, fineLocPerm) == approved);

        }else if (permissionID == STORAGE_REQUEST) { // If permission ID is storage request
            // Get permission string
            String storagePerm = Manifest.permission.READ_EXTERNAL_STORAGE;

            // Check permission
            permGranted = ( ContextCompat.checkSelfPermission(currentActivity, storagePerm) == approved );
        }

        return permGranted;
    }
}
