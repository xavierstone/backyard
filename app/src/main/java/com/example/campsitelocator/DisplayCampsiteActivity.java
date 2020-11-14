package com.example.campsitelocator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/*
This activity displays the selected campsite
As well as: name, photos, description, and ratings
 */
public class DisplayCampsiteActivity extends AppCompatActivity {

    // Text Fields
    TextView siteName;
    TextView siteDescription;
    TextView displayCampsiteStatus;
    ImageView campsitePhoto;
    ImageButton galleryBackButton;
    ImageButton galleryForwardButton;
    Button uploadButton;
    ShareButton shareButton;
    RatingBar ratingBar;
    RatingBar userRating;
    ImageButton favButton;

    // Static variable for tracking campsiteID and current photo
    // I've found this to be cleaner than passing data between Activities, in most cases
    public static long currentCampsite = 0;
    public static int currentPhoto = -1; // Represents position in list, not absolute ID
    public static ArrayList<DBData> photos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_campsite);

        // Load text fields
        siteName = (TextView) findViewById(R.id.siteName);
        siteDescription = (TextView) findViewById(R.id.siteDescription);
        campsitePhoto = findViewById(R.id.campsitePhoto);
        galleryBackButton = findViewById(R.id.galleryBackButton);
        galleryForwardButton = findViewById(R.id.galleryForwardButton);
        uploadButton = findViewById(R.id.uploadButton);
        displayCampsiteStatus = findViewById(R.id.displayCampsiteStatus);
        shareButton = (ShareButton)findViewById(R.id.share_btn);
        ratingBar = findViewById(R.id.rating);
        userRating = findViewById(R.id.userRating);
        favButton = findViewById(R.id.favButton);

        // Search database for current campsite (ID will have been passed by previous activity)
        DBHandler dbHandler = new DBHandler(this,null,null,1);
        DBData currentSite = dbHandler.search(DBHandler.campsitesTable, "id", ""+currentCampsite).get(0);

        // Get rating
        updateRatings();

        // Check for favorite
        if (!dbHandler.search(DBHandler.favoritesTable, "user_id == "+MainActivity.userID +
                " AND campsite_id == "+currentCampsite).isEmpty()){
            // Site is favorited, set star
            Uri uri = Uri.parse(InternalStorage.getDrawPath()+"star");
            favButton.setImageBitmap(InternalStorage.loadExternalImage(this, uri.toString()));
        }

        //Check for read external permission
        boolean permission = (ContextCompat.checkSelfPermission( this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED );

        // Search DB for campsite photos
        photos = dbHandler.search(DBHandler.photosTable,"campsite_id",""+currentCampsite);

        // Modify layout based on photo # and permissions
        if (!permission){
            // Disable upload button, do not load images
            uploadButton.setEnabled(false);
            displayCampsiteStatus.setText("File permissions disabled, images cannot be loaded");
        }else{
            // If there are any photos to display, do so
            if (!photos.isEmpty()) {
                // Display ImageView and load first photo
                campsitePhoto.setVisibility(View.VISIBLE);
                currentPhoto = 0;
                updatePhoto();

                // Hide status text
                displayCampsiteStatus.setText("");
            }

            if (photos.size() > 1){
                // If multiple photos, display navigation
                galleryForwardButton.setVisibility(View.VISIBLE);
                galleryBackButton.setVisibility(View.VISIBLE);
            }
        }

        // Set text fields with data from DB
        siteName.setText(currentSite.getData("name"));
        siteDescription.setText(currentSite.getData("description"));
    }

    // Updates the ImageView based on the current photo
    private void updatePhoto(){
        Bitmap image = getCurrentBitmap();
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        campsitePhoto.setImageBitmap(image);
        shareButton.setShareContent(content);
    }

    private void updateRatings(){
        DBHandler dbHandler = new DBHandler(this, null, null, 1);
        ArrayList<DBData> ratings = dbHandler.search(DBHandler.ratingsTable, "campsite_id", ""+currentCampsite);
        double totalRating = 0;
        for (int i = 0; i < ratings.size(); i++){
            totalRating+=Double.parseDouble(ratings.get(i).getData("stars"));
        }
        totalRating/=ratings.size();
        ratingBar.setRating((float)totalRating);
    }

    private Bitmap getCurrentBitmap(){
        DBData photo = photos.get(currentPhoto);
        if (photo.getData("type").equals("1")) {
            return InternalStorage.loadInternalImage(this, photo.getData("path"));
        }else{
            Uri uri = Uri.parse(InternalStorage.getDrawPath() + photo.getData("path"));
            return InternalStorage.loadExternalImage(this, uri.toString());
        }
    }

    // Returns to home screen
    public void goHome(View view){
        Intent intent = new Intent(DisplayCampsiteActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    // Adds the campsite as a favorite
    public void addFavorite(View view){
        DBHandler dbHandler = new DBHandler(this,null,null,1);
        if (dbHandler.search(DBHandler.favoritesTable, "user_id == "+MainActivity.userID +
                " AND campsite_id == "+currentCampsite).isEmpty()) {
            DBData favorite = new DBData(DBHandler.favoritesTable);
            favorite.addData(new String[]{"0", "" + MainActivity.userID, "" + currentCampsite});
            dbHandler.insert(favorite);

            Uri uri = Uri.parse(InternalStorage.getDrawPath() + "star");
            favButton.setImageBitmap(InternalStorage.loadExternalImage(this, uri.toString()));
        }
    }

    // Posts the campsite to Facebook
    public void facebookShare(View view){

    }

    // Gallery Navigation
    public void galleryBack(View view){
        if (currentPhoto!=-1){
            currentPhoto-=1;
            if (currentPhoto<0) currentPhoto = photos.size()-1;
            updatePhoto();
        }
    }

    public void galleryForward(View view){
        if (currentPhoto!=-1){
            currentPhoto+=1;
            if (currentPhoto>=photos.size()) currentPhoto = 0;
            updatePhoto();
        }
    }

    // Adds a rating
    public void addRating(View view){
        userRating.setVisibility(View.VISIBLE);
        DBHandler dbHandler = new DBHandler(this, null, null, 1);
        ArrayList<DBData> ratings = dbHandler.search(DBHandler.ratingsTable, "user_id", ""+MainActivity.userID);
        if (!ratings.isEmpty()){
            userRating.setRating(Float.parseFloat(ratings.get(0).getData("stars")));
        }
        userRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    ratingBar.setVisibility(View.GONE);
                    DBHandler dbHandler = new DBHandler(getApplicationContext(), null, null, 1);

                    // See if user already has rating in DB
                    ArrayList<DBData> ratings = dbHandler.search(DBHandler.ratingsTable, "user_id", ""+MainActivity.userID);
                    if (!ratings.isEmpty()){
                        dbHandler.update(ratings.get(0),"stars",""+rating);
                    }else {
                        DBData newRating = new DBData(DBHandler.ratingsTable);
                        newRating.addData(new String[]{"0", "" + MainActivity.userID, "" + currentCampsite, "" + rating});
                        dbHandler.insert(newRating);
                    }

                    updateRatings();
                }
            }
        });
    }

    // Upload a photo from the Gallery App
    public void uploadPhoto(View view){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            // Read photo path into DB
            DBHandler dbHandler = new DBHandler(this, null, null, 1);
            Uri targetUri = data.getData();

            DBData newPhoto = new DBData(DBHandler.photosTable);
            newPhoto.addData(new String[]{"0",""+currentCampsite, "1", "" });
            long photoID = dbHandler.insert(newPhoto);

            // Internal storage and update path
            String name = InternalStorage.savePhoto(this, targetUri.toString(), photoID);
            dbHandler.update(newPhoto, "path", name);

            photos.add(newPhoto);
            currentPhoto = photos.size()-1;
            updatePhoto();

            if (photos.size() <= 1){
                galleryBackButton.setEnabled(false);
                galleryForwardButton.setEnabled(false);
            }else{
                galleryBackButton.setEnabled(true);
                galleryForwardButton.setEnabled(true);
            }
        }
    }

    // Goes to the search page
    public void goToSearchOptions(View view){
        Intent intent = new Intent(DisplayCampsiteActivity.this, SearchOptionsActivity.class);
        startActivity(intent);
    }
}
