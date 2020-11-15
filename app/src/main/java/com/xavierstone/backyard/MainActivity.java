package com.xavierstone.backyard;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

/*
Page the app opens to, requests credentials or gives the user the option to create a new account
 */

public class MainActivity extends AppCompatActivity {

    // Static variable to track logged in user
    public static int userID = 0;
    public static int LOCATION_REQUEST=0;
    public static int STORAGE_REQUEST=1;
    private static final String EMAIL = "email";

    private final String locationRequest[] = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION};
    private final String storageRequest[] = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE};

    // Text fields
    TextView loginFeedbackView;
    EditText emailAddressBox;
    EditText passwordBox;

    ProgressBar progressBar;

    private CallbackManager callbackManager = CallbackManager.Factory.create();

    public static boolean dontLogIn = false;

    final DBHandler dbHandler = new DBHandler(this, null, null,1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);

        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for permissions
        checkPermissions();

        // Hardcode database values
        // Gorge Site
        String text = "";

        // Open file
        try{
            InputStream is = getAssets().open("campsites.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);
        }catch (IOException ex){
            ex.printStackTrace();
        }
        progressBar.setProgress(10);

        String c = "";
        String subStr = "";
        String[] colNames = DBHandler.campsitesTable.getColNames();
        String[] data = new String[colNames.length];
        data[0] = "0";
        int fieldPos = 1;
        long dataID = -1;
        boolean skip = false;
        boolean multi = false;

        // Parse file
        for (int i=0; i < text.length(); i++){
            progressBar.setProgress(10 + ((int) 90*(i/text.length())));
            c = text.substring(i, i+1);

            if ((!skip) && (c.equals("|") || c.equals("`"))){
                if (fieldPos < colNames.length){
                    if (fieldPos == 1){
                        skip = !dbHandler.search(DBHandler.campsitesTable, "name", subStr).isEmpty();
                    }
                    data[fieldPos] = subStr;
                }else{
                    // Image
                    if (!multi) {
                        DBData dbData = new DBData((DBHandler.campsitesTable));
                        dbData.addData(data);
                        dataID = dbHandler.insert(dbData);
                        multi = true;
                    }

                    DBData newPhoto = (new DBData(DBHandler.photosTable)).addData(new String[]{"0", ""+dataID, "0", subStr});
                    dbHandler.insert(newPhoto);
                }
                subStr = "";
                fieldPos += 1;
            }else{
                subStr += c;
            }

            if (c.equals("`")){
                fieldPos = 1;
                skip = false;
                subStr = "";
                multi = false;
            }
        }

        /*
        // Request permissions
        // Coarse location
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION )
                != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    PERMISSION_ACCESS_COARSE_LOCATION );
        }

        /*
        // Fine location
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    PERMISSION_ACCESS_FINE_LOCATION );
        }*/

        // Load fields
        //loginFeedbackView = (TextView) findViewById(R.id.loginFeedback);
        //emailAddressBox = (EditText) findViewById(R.id.loginEmail);
        //passwordBox = (EditText) findViewById(R.id.loginPassword);

        //LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        //loginButton.setReadPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermissions();
    }

    private void checkPermissions(){
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            // Location granted
            if ( ContextCompat.checkSelfPermission( this, Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED){

                // External storage granted
                goToHome();
            }else{
                ActivityCompat.requestPermissions(this, storageRequest, STORAGE_REQUEST);
            }

        }else{

            ActivityCompat.requestPermissions(this, locationRequest, LOCATION_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (grantResults[LOCATION_REQUEST] == PackageManager.PERMISSION_GRANTED) {
            // Location enabled
            if (grantResults[STORAGE_REQUEST] == PackageManager.PERMISSION_GRANTED) {
                // Storage enabled too
                goToHome();
            }else{
                // Request storage
                ActivityCompat.requestPermissions(this, storageRequest, STORAGE_REQUEST);
            }
        }else{
            // Request location
            ActivityCompat.requestPermissions(this, locationRequest, LOCATION_REQUEST);
        }
    }

    private void goToHome(){
        // God mode is hardcoded
        ArrayList<DBData> user = dbHandler.search(DBHandler.usersTable,
                    "email", "xaviermstone@gmail.com");

        if (user.isEmpty()) {
            DBData xavier = new DBData(DBHandler.usersTable);
            xavier.addData(new String[]{"0", "Xavier", "Stone", ""+DBHandler.LOCAL_ACCOUNT, "xaviermstone@gmail.com"});
            userID = (int) dbHandler.insert(xavier);
            InternalStorage.saveCredentials(this, "xaviermstone@gmail.com,Qwakmagic45\n");
        } else {
            userID = Integer.parseInt(user.get(0).getData("id"));
        }

        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }

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
    }
}
