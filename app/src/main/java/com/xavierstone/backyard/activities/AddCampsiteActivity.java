package com.xavierstone.backyard.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xavierstone.backyard.db.DBData;
import com.xavierstone.backyard.db.DBHandler;
import com.xavierstone.backyard.db.InternalStorage;
import com.xavierstone.backyard.R;
import com.xavierstone.backyard.models.Site;
import com.xavierstone.backyard.models.User;

import java.util.ArrayList;

/*
This activity allows the user to add a new campsite to the database
 */
public class AddCampsiteActivity extends AppCompatActivity {

    // Current User
    private Site currentCampsite;

    // Text Fields
    TextView addCampsiteStatus;
    EditText campsiteNameBox;
    EditText descriptionBox;
    EditText latitudeBox;
    EditText longitudeBox;
    ImageButton addPhotosBack;
    ImageButton addPhotosForward;
    ImageView addPhotosDisplay;
    Button addPhotosButton;

    // List to hold photo paths
    private ArrayList<String> photos = new ArrayList<>();
    private int currentPhoto = -1; //current position in list for navigation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_campsite);

        // Locate text fields
        campsiteNameBox = (EditText) findViewById(R.id.campsiteName);
        descriptionBox = (EditText) findViewById(R.id.siteDescription);
        latitudeBox = (EditText) findViewById(R.id.latitude);
        longitudeBox = (EditText) findViewById(R.id.longitude);
        addPhotosBack = findViewById(R.id.addPhotosBack);
        addPhotosForward = findViewById(R.id.addPhotosForward);
        addPhotosDisplay = findViewById(R.id.addPhotosDisplay);
        addCampsiteStatus = findViewById(R.id.addCampsiteStatus);
        addPhotosButton = findViewById(R.id.addPhotosButton);

        //Check for read external permission
        boolean permission = (ContextCompat.checkSelfPermission( this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED );

        // If no permission, hide add photo button, set status
        if (!permission){
            addPhotosButton.setVisibility(View.GONE);
            addCampsiteStatus.setText("File permission disabled, cannot add images");
        }else{
            addPhotosButton.setVisibility(View.VISIBLE);
            addCampsiteStatus.setText("");
        }

        currentCampsite = User.getCurrentUser().getCurrentSite();
    }

    // Returns to the home screen
    public void goBack(View view){
        Intent intent = new Intent(AddCampsiteActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    // Add photos
    public void addPhotos(View view){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            // Read photo path into array list
            Uri targetUri = data.getData();
            photos.add(targetUri.toString());

            // Make views visible (if necessary)
            if (photos.size() > 1){
                addPhotosBack.setVisibility(View.VISIBLE);
                addPhotosForward.setVisibility(View.VISIBLE);
            }else{
                addPhotosDisplay.setVisibility(View.VISIBLE);
            }

            // Update current photo and load image
            currentPhoto+=1;
            updatePhoto();
        }
    }

    // Updates the ImageView based on the current photo
    private void updatePhoto(){
        addPhotosDisplay.setImageBitmap(InternalStorage.loadExternalImage(this, photos.get(currentPhoto)));
    }

    // Gallery navigation
    // Go Back
    public void goBackGallery(View view){
        if (currentPhoto!=-1){
            currentPhoto-=1;
            if (currentPhoto<0) currentPhoto = photos.size()-1;
            updatePhoto();
        }
    }

    // Go Forward
    public void goForward(View view){
        if (currentPhoto!=-1){
            currentPhoto+=1;
            if (currentPhoto>=photos.size()) currentPhoto = 0;
            updatePhoto();
        }
    }

    // Triggered when the Submit button is clicked
    public void addCampsite(View view){
        /*
        // Open DB and insert new campsite
        DBHandler dbHandler = new DBHandler(this, null, null, 1);
        DBData campsite = new DBData(DBHandler.campsitesTable);
        campsite.addData(new String[]{"0", campsiteNameBox.getText().toString(),
            descriptionBox.getText().toString(), latitudeBox.getText().toString(),
            longitudeBox.getText().toString()});

        // Insert DBData into DB
        long campsiteID = dbHandler.insert(campsite);

        // Insert photos into DB and save to internal storage
        for (int i=0; i<photos.size(); i++){
            DBData newPhoto = new DBData(DBHandler.photosTable);
            newPhoto.addData(new String[]{"0",""+campsiteID, "1", ""});
            long photoID = dbHandler.insert(newPhoto);

            // Internal storage and update path
            String name = InternalStorage.savePhoto(this, photos.get(i), photoID);
            dbHandler.update(newPhoto, "path", name);
        }

        // Pass control
        Intent intent = new Intent(AddCampsiteActivity.this, DisplayCampsiteActivity.class);
        currentCampsite = campsiteID;
        startActivity(intent);*/
    }
}
