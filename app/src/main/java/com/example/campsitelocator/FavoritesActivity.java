package com.example.campsitelocator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

/*
Lists favorite campsites for the current user
 */

public class FavoritesActivity extends AppCompatActivity {

    // Results view
    TextView favoriteResultsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Load results view
        favoriteResultsView = findViewById(R.id.favoriteResults);

        // Search for favorites
        DBHandler dbHandler = new DBHandler(this, null, null,1);
        ArrayList<DBData> resultsList = dbHandler.search(DBHandler.favoritesTable,
                "user_id", ""+MainActivity.userID);

        // Create another ArrayList with all the corresponding sites
        ArrayList<DBData> resultSites = new ArrayList<DBData>();
        for (int i=0; i<resultsList.size(); i++){
            resultSites.add(dbHandler.search(DBHandler.campsitesTable,
                    "id", resultsList.get(i).getData("campsite_id")).get(0));
        }

        // Display results
        String resultString = "";
        for (int i=0; i < resultSites.size(); i++){
            resultString += resultSites.get(i).printData()+"\n";
        }

        if (resultString.equals("")){
            resultString = "No results.";
        }

        favoriteResultsView.setText(resultString);
    }

    // Goes to home screen
    public void goHome(View view){
        Intent intent = new Intent(FavoritesActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    // Goes to search screen
    public void goToSearchOptions(View view){
        Intent intent = new Intent(FavoritesActivity.this, SearchOptionsActivity.class);
        startActivity(intent);
    }
}
