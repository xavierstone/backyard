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
        setContentView(R.layout.activity_main_2);

        PACKAGE_NAME = this.getPackageName();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for location permission
        if (!checkPermission(this, LOCATION_REQUEST)) {
            // Not enabled, request location permission
            ActivityCompat.requestPermissions(this, locationRequest, LOCATION_REQUEST);
        }else{
            // Yes enabled! Check for storage permission
            if (!checkPermission(this, STORAGE_REQUEST)) {
                // Nope, request it
                ActivityCompat.requestPermissions(this, storageRequest, STORAGE_REQUEST);
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
    // Checks off the final I's and goes to the HomeActivity
    public void permissionsResult() {
        // Initialize test user
        User.signIn("test@test.com","test");

        // Transfer control to Home Activity
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public static boolean checkPermission(Activity context, int permissionID) {
        int approved = PackageManager.PERMISSION_GRANTED;
        boolean permGranted = false;

        // If permissionID is location request
        if (permissionID == LOCATION_REQUEST) {
            // Get permission strings
            String coarseLocPerm = Manifest.permission.ACCESS_COARSE_LOCATION;
            String fineLocPerm = Manifest.permission.ACCESS_FINE_LOCATION;

            // Check permission
            permGranted = ( ContextCompat.checkSelfPermission(context, coarseLocPerm) == approved
                    &&  ContextCompat.checkSelfPermission(context, fineLocPerm) == approved);

        }else if (permissionID == STORAGE_REQUEST) { // If permission ID is storage request
            // Get permission string
            String storagePerm = Manifest.permission.READ_EXTERNAL_STORAGE;

            // Check permission
            permGranted = ( ContextCompat.checkSelfPermission(context, storagePerm) == approved );
        }

        return permGranted;
    }

    /*
    // Verifies the user's credentials and signs them in
    public void signIn(View view) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Read from boxes
        String email = emailAddressBox.getText().toString();
        String password = passwordBox.getText().toString();

        // Create handler and query DB for user email address
        DBHandler dbHandler = new DBHandler(this, null, null, 1);
        ArrayList<DBData> searchResults = dbHandler.search(DBHandler.usersTable, "email", email);
        int returnCode;
        DBData user = null;

        if (!searchResults.isEmpty()) {
            user = searchResults.get(0);

            // Read credentials from internal storage
            returnCode = InternalStorage.readCredentials(this, email, password);
        }else{
            returnCode = InternalStorage.DOES_NOT_EXIST;
        }

        // Interpret result
        switch (returnCode){
            case InternalStorage.VERIFIED:
                // Pass control and user ID to Home Activity
                this.userID = Integer.parseInt(user.getData("id"));
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
            case InternalStorage.DOES_NOT_EXIST:
                // User does not exist
                emailAddressBox.setText("");
                passwordBox.setText("");
                loginFeedbackView.setText("No user account with that email address.");
                break;
            case InternalStorage.WRONG_PASSWORD:
                //Incorrect password
                passwordBox.setText("");
                loginFeedbackView.setText("Incorrect password.");
                break;
        }
    }

    // Passes control to the CreateAccount Activity
    public void createAccount(View view){
        // Start Create Account activity
        Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
        startActivity(intent);
    }*/
}
