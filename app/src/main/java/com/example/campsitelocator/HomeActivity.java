package com.example.campsitelocator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;

/*
Provides the user with a convenient home screen
 */

public class HomeActivity extends AppCompatActivity {

    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ratingBar = findViewById(R.id.ratingBarHome);
    }

    // Goes to Favorites Activity
    public void goToFavorites(View view){
        // Search for favorites
        DBHandler dbHandler = new DBHandler(this, null, null,1);
        ArrayList<DBData> resultsList = dbHandler.search(DBHandler.favoritesTable,
                "user_id", ""+MainActivity.userID);

        // Create another ArrayList with all the corresponding sites
        SearchOptionsActivity.searchResults = new ArrayList<>();
        for (int i=0; i<resultsList.size(); i++){
            SearchOptionsActivity.searchResults.add(dbHandler.search(DBHandler.campsitesTable,
                    "id", resultsList.get(i).getData("campsite_id")).get(0));
        }

        // Pass control to ListResults
        Intent intent = new Intent(HomeActivity.this, ListResultsActivity.class);
        startActivity(intent);
    }

    // Goes to Add Campsite Activity
    public void addCampsite(View view){
        // Transfer control to Add Campsite Activity
        Intent intent = new Intent(HomeActivity.this, AddCampsiteActivity.class);
        startActivity(intent);
    }

    public void rateUs(View view){
        ratingBar.setVisibility(View.VISIBLE);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setVisibility(View.GONE);
            }
        });
    }

    // Goes to Search Options Activity
    public void goToSearchOptions(View view){
        // Transfer control to Search Options Activity
        Intent intent = new Intent(HomeActivity.this, SearchOptionsActivity.class);
        startActivity(intent);
    }

    // Goes to About This App Activity
    public void goToAboutThisApp(View view){
        // Transfer control to About This App Activity
        Intent intent = new Intent(HomeActivity.this, AboutThisAppActivity.class);
        startActivity(intent);
    }

    // Goes to Contact Us Activity
    public void goToContactUs(View view){
        // Transfer control to Contact Us Activity
        Intent intent = new Intent(HomeActivity.this, ContactUsActivity.class);
        startActivity(intent);
    }

    // Logs Out
    public void logout(View view){
        // Transfer control to Contact Us Activity
        MainActivity.dontLogIn = true;
        MainActivity.userID = 0;
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
