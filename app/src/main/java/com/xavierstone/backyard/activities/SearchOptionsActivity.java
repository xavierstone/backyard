package com.xavierstone.backyard.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.xavierstone.backyard.R;
import com.xavierstone.backyard.db.DBData;
import com.xavierstone.backyard.db.DBHandler;

import java.util.ArrayList;

/*
Provides the user with options for searching the campsite database
 */
public class SearchOptionsActivity extends AppCompatActivity {

    // Text fields
    EditText searchName;
    EditText searchRadius;
    TextView searchStatus;
    TextView radiusLabel;
    Button mapSearch;

    // Location
    private FusedLocationProviderClient fusedLocationClient;

    // Search results array, can be referenced from MapsActivity
    public static ArrayList<DBData> searchResults = new ArrayList<>();

    public static boolean setRadius = false;
    public static double longitude = 0;
    public static double latitude = 0;

    boolean permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_options);

        // Load text fields
        searchName = findViewById(R.id.searchName);
        radiusLabel = findViewById(R.id.radiusLabel);
        searchRadius = findViewById(R.id.searchRadius);
        searchStatus = findViewById(R.id.searchStatus);
        mapSearch = findViewById(R.id.mapSearch);

        permission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) /*&&
                (ContextCompat.checkSelfPermission( this,
                        Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)*/;

        if (!permission) {
            mapSearch.setVisibility(View.GONE);
            searchRadius.setVisibility(View.GONE);
            radiusLabel.setVisibility(View.GONE);
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        }
    }

    // Searches for campsites based on provided data, returns results in a list
    public void searchCampsites(View view) {
        updateSearchResults();

        if (searchResults.isEmpty()) {
            // No results
            searchStatus.setText("No results found");
        } else if (searchResults.size() == 1) {
            // One result, go straight to page
            //DisplayCampsiteActivity.currentCampsite = Long.parseLong(searchResults.get(0).getData("id"));
            Intent intent = new Intent(SearchOptionsActivity.this, DisplayCampsiteActivity.class);
            startActivity(intent);
        } else {
            // Pass to list results activity
            Intent intent = new Intent(SearchOptionsActivity.this, ListResultsActivity.class);
            startActivity(intent);
        }
    }

    // Searches for campsites and displays a map
    public void mapSearch(View view) {
        updateSearchResults();

        if (searchResults.isEmpty()) {
            // No results
            searchStatus.setText("No results found");
        } else {
            // Pass control to MapResults
            Intent intent = new Intent(SearchOptionsActivity.this, MapsActivity.class);
            startActivity(intent);
        }
    }

    // Compartmentalizes the query process
    private void updateSearchResults() {
        // Construct where clause
        String whereClause = "";
        String term = searchName.getText().toString();
        String radiusText = searchRadius.getText().toString();
        int numTerms = 0; // To track num of search terms

        setRadius = false;

        if (!term.isEmpty()) {
            // If name is provided, start clause
            whereClause = "(name LIKE \"%" + term + "%\" OR " +
                    "description LIKE \"%" + term + "%\")";
            numTerms++;
        }
        if (!radiusText.isEmpty()) {
            // If radius is provided, get current location
            double radius = Double.parseDouble(radiusText);
            if (fusedLocationClient != null && permission) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Task locationTask = fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object

                                }
                            }
                        });
                if (locationTask.isSuccessful()) {
                    Location location = (Location) locationTask.getResult();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    SearchOptionsActivity.setRadius = true;
                }
            }
            if (setRadius){
                if (numTerms>0)
                    whereClause += " AND";
                whereClause += " lat > " + (latitude - radius) +
                        " AND lat < " + (latitude + radius) +
                        " AND long > " + (longitude - radius) +
                        " AND long < " + (longitude + radius);
            }
        }

        // Pass query to search method
        DBHandler dbHandler = new DBHandler(this, null, null, 1);
        searchResults = dbHandler.search(DBHandler.campsitesTable, whereClause);
    }

    // Returns to home menu
    public void goHome(View view){
        Intent intent = new Intent(SearchOptionsActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
