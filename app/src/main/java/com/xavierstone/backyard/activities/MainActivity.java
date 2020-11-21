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

public class MainActivity extends AppCompatActivity {

    // Permission managment
    public static int LOCATION_REQUEST=0;
    public static int STORAGE_REQUEST=1;

    private final String[] locationRequest = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION};
    private final String[] storageRequest = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE};

    //private CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for permissions, global variable used in onRequestPermissionsResult
        checkPermissions();

        // Initialize test user
        User.signIn("test@test.com","test");

        // Go to Home
        goToHome();
    }

    private void checkPermissions(){
        // Determine Initial Permission to request
        // Request is processed in onRequestPermissionsResult,
        // then second permission is requested if applicable

        // If location permission
        if (checkPermission(this, LOCATION_REQUEST)) {
            // Check storage permission
            if (!checkPermission(this, STORAGE_REQUEST)) {
                // If not, request storage permission
                ActivityCompat.requestPermissions(this, storageRequest, STORAGE_REQUEST);
            }
        }else {
            // Otherwise, request location permission
            ActivityCompat.requestPermissions(this, locationRequest, LOCATION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        // Request storage after Location request has resulted
        if (requestCode == LOCATION_REQUEST){
            // Request storage
            ActivityCompat.requestPermissions(this, storageRequest, STORAGE_REQUEST);
        }
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

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    public void goToHome() {
        // God mode is hardcoded
        /*ArrayList<DBData> user = dbHandler.search(DBHandler.usersTable,
                    "email", "xaviermstone@gmail.com");

        if (user.isEmpty()) {
            DBData xavier = new DBData(DBHandler.usersTable);
            xavier.addData(new String[]{"0", "Xavier", "Stone", ""+DBHandler.LOCAL_ACCOUNT, "xaviermstone@gmail.com"});
            userID = (int) dbHandler.insert(xavier);
            InternalStorage.saveCredentials(this, "xaviermstone@gmail.com,Qwakmagic45\n");
        } else {
            userID = Integer.parseInt(user.get(0).getData("id"));
        }*/

        // Transfer control to Home Activity
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
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
