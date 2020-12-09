package com.xavierstone.backyard.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.xavierstone.backyard.BackyardApplication;
import com.xavierstone.backyard.R;
import com.xavierstone.backyard.models.Site;
import com.xavierstone.backyard.models.User;

/*
This activity displays the selected campsite
As well as: name, photos, description, and ratings
 */
public class DisplayCampsiteActivity extends AppCompatActivity {

    // DB Handler
    private Site currentSite;
    private boolean picsLoaded = false;

    // Views
    TextView siteName;
    TextView siteSkinny;
    TextView displayCampsiteStatus;
    ImageView currentPic;
    ImageButton galleryBackButton;
    ImageButton galleryForwardButton;
    Button uploadButton;
    RatingBar ratingBar;
    RatingBar userRating;
    ImageButton favButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_campsite);

        //MainActivity.currentActivity = this;

        // Get current!
        currentSite = User.getCurrentUser().getCurrentSite();

        // Start picsloader
        new loadPics().execute();

        // Load text fields
        siteName = findViewById(R.id.siteName);
        siteSkinny = findViewById(R.id.siteDescription);
        currentPic = findViewById(R.id.currentPic);
        galleryBackButton = findViewById(R.id.galleryBackButton);
        galleryForwardButton = findViewById(R.id.galleryForwardButton);
        uploadButton = findViewById(R.id.uploadButton);
        displayCampsiteStatus = findViewById(R.id.displayCampsiteStatus);
        ratingBar = findViewById(R.id.rating);
        userRating = findViewById(R.id.userRating);
        favButton = findViewById(R.id.favButton);

        // Get rating
        //updateRatings();

        // Check for favorite
        // TODO: implement check for favorite
        /*
        if (!dbHandler.search(DBHandler.favoritesTable, "user_id == "+MainActivity.userID +
                " AND campsite_id == "+currentCampsite).isEmpty()){
            // Site is favorited, set star
            Uri uri = Uri.parse(InternalStorage.getDrawPath()+"star");
            favButton.setImageBitmap(InternalStorage.loadExternalImage(this, uri.toString()));
        }*/

        // Configure photo display
        updatePicDisplay();

        // Set text fields with data from DB
        siteName.setText(currentSite.getName());
        siteSkinny.setText(currentSite.getSkinny());
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    protected void updatePicDisplay() {
        //Check for read external permission
        boolean storagePermission = MainActivity.checkPermission(this, MainActivity.STORAGE_REQUEST);

        int numPics = currentSite.getPics().size();

        // Modify layout based on file permissions
        if (!storagePermission){
            // Disable upload button, do not load images
            displayCampsiteStatus.setText(R.string.noFilePerms);
            setVisibilities(false, false);
        }else{
            // Check for pics load
            if (!picsLoaded){
                displayCampsiteStatus.setText(R.string.picsLoading);
                setVisibilities(false, false);
            }else {
                // adjust display based on number of photos
                if (numPics == 0) {
                    displayCampsiteStatus.setText(R.string.noPicsFound);
                    setVisibilities(false, false);
                } else setVisibilities(true, numPics != 1);
            }
        }
    }

    private void setVisibilities(boolean showCurrentPic, boolean showGallery) {
        if (showCurrentPic) {
            currentPic.setVisibility(View.VISIBLE);
            currentPic.setImageBitmap(currentSite.getCurrentPic(this));

            // Hide status text
            displayCampsiteStatus.setVisibility(View.GONE);
        }else{
            currentPic.setVisibility(View.GONE);

            // Show status text
            displayCampsiteStatus.setVisibility(View.VISIBLE);

            // Hide gallery no matter what if photo is hidden
            showGallery = false;
        }
        if (showGallery){
            galleryForwardButton.setVisibility(View.VISIBLE);
            galleryBackButton.setVisibility(View.VISIBLE);
        }else{
            galleryForwardButton.setVisibility(View.GONE);
            galleryBackButton.setVisibility(View.GONE);
        }
    }

    private void updateRatings(){/*
        ArrayList<DBData> ratings = dbSite.search(DBHandler.ratingsTable, "campsite_id", ""+ currentSite);
        double totalRating = 0;
        for (int i = 0; i < ratings.size(); i++){
            totalRating+=Double.parseDouble(ratings.get(i).getData("stars"));
        }
        totalRating/=ratings.size();
        ratingBar.setRating((float)totalRating);*/
    }

    /*
    private Bitmap getBitmap(Photo photo){
        if (photo.getData("type").equals("1")) {
            return InternalStorage.loadInternalImage(this, photo.getData("path"));
        }else{
            Uri uri = Uri.parse(InternalStorage.getDrawPath() + photo.getUri());
            return InternalStorage.loadExternalImage(this, uri.toString());
        }
    }*/

    // Adds the campsite as a favorite
    public void addFavorite(View view){
        /*
        TODO: implement add favorite
        DBHandler dbHandler = new DBHandler(this,null,null,1);
        if (dbHandler.search(DBHandler.favoritesTable, "user_id == "+MainActivity.userID +
                " AND campsite_id == "+currentCampsite).isEmpty()) {
            DBData favorite = new DBData(DBHandler.favoritesTable);
            favorite.addData(new String[]{"0", "" + MainActivity.userID, "" + currentCampsite});
            dbHandler.insert(favorite);

            Uri uri = Uri.parse(InternalStorage.getDrawPath() + "star");
            favButton.setImageBitmap(InternalStorage.loadExternalImage(this, uri.toString()));
        }*/
    }

    // Gallery Navigation
    public void galleryBack(View view){
        // Decrement photo
        currentSite.scrollGallery(-1);

        // Load photo
        currentPic.setImageBitmap(currentSite.getCurrentPic(this));
    }

    public void galleryForward(View view){
        // Increment photo
        currentSite.scrollGallery(-1);

        // Load photo
        currentPic.setImageBitmap(currentSite.getCurrentPic(this));
    }

    // Adds a rating
    public void addRating(View view){
        /* TODO: add rating
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
        });*/
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

        /*
        if (resultCode == RESULT_OK){
            // Read photo path into DB
            DBHandler dbHandler = new DBHandler(this, null, null, 1);
            Uri targetUri = data.getData();

            DBData newPhoto = new DBData(DBHandler.photosTable);
            newPhoto.addData(new String[]{"0",""+ currentSite, "1", "" });
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
        }*/
    }

    private class loadPics extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            BackyardApplication.getDB().loadSitePics(currentSite);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            picsLoaded = true;
            updatePicDisplay();

            super.onPostExecute(aVoid);
        }
    }

    @Override
    protected void onDestroy() {
        //dbSite.close();
        super.onDestroy();
    }
}
