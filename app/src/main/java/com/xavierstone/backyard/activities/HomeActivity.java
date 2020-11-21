package com.xavierstone.backyard.activities;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.SearchView;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RatingBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xavierstone.backyard.R;
import com.xavierstone.backyard.db.DBData;
import com.xavierstone.backyard.db.DBHandler;
import com.xavierstone.backyard.models.Site;
import com.xavierstone.backyard.models.User;

import java.util.ArrayList;

/*
Provides the user with a multi-activity home screen
Integrates map functionality with main navigation and search bar
 */

public class HomeActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    // Marker list
    private final ArrayList<Marker> markers = new ArrayList<>();

    // DBHandler
    private DBHandler dbHome;

    // Search results array, can be referenced from MapsActivity
    private ArrayList<Site> searchResults = new ArrayList<>();

    // Views
    private GoogleMap googleMap;
    private Button signButton;

    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize signButton
        signButton = findViewById(R.id.signButton);

        // Initialize Search Bar
        final SearchView searchView = findViewById(R.id.searchBar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            // Activates when user clicks on search buttons
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Search for query
                searchResults = dbHome.find(query);

                // For non-empty result lists, update map
                if (!searchResults.isEmpty()) {
                    displayMapResults();
                }

                // Clear query
                searchView.setQuery("", false);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Load the Google Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMapsFrag);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //ratingBar = findViewById(R.id.ratingBarHome);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Open database
        dbHome = new DBHandler(this, null, null, 1);

        // Set text of Sign In Button to reflect sign in status
        if (User.getCurrentUser() == null)
            signButton.setText(R.string.signButtonText);
        else
            signButton.setText(R.string.signButtonTextAlt);
    }

    @Override
    protected void onPause() {
        super.onPause();

        dbHome.close();;
    }

    private void displayMapResults(){
        ArrayList<LatLng> latLngs = new ArrayList<>();
        LatLngBounds.Builder bounds = null;

        Site curSite = null;
        double curLat = 0;
        double curLng = 0;
        double spread = 0;

        markers.clear();

        // Loop through results list and add a marker for each site
        for (int i=0; i<searchResults.size(); i++){

            curSite = searchResults.get(i);

            // Add lat/long to array list
            latLngs.add(curSite.getLocation());

            // Add marker to list
            markers.add(googleMap.addMarker(new MarkerOptions().position(latLngs.get(i))
                    .title(curSite.getName())));

            // Update LatLng Bounds
            if (latLngs.size() > 1){
                assert bounds != null;
                bounds.include(latLngs.get(i));
            }else{
                bounds = new LatLngBounds.Builder().include(latLngs.get(0));
            }

            // Update spread
            Location curLoc = new Location("");
            curLoc.setLatitude(latLngs.get(i).latitude);
            curLoc.setLongitude(latLngs.get(i).longitude);
            Location centerLoc = new Location("");
            centerLoc.setLatitude(bounds.build().getCenter().latitude);
            centerLoc.setLongitude(bounds.build().getCenter().longitude);

            double curSpread = curLoc.distanceTo(centerLoc);

            if (curSpread>spread) spread = curSpread;
        }

        int zoom = (int) (14 - Math.floor(Math.log10(spread/500)/Math.log10(2)));

        assert bounds != null;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.build().getCenter(), zoom));
        googleMap.setOnInfoWindowClickListener(this);

        // Check for location permission before enabling myLocation
        if ( MainActivity.checkPermission(this, MainActivity.LOCATION_REQUEST )) {
            googleMap.setMyLocationEnabled(true);
        }

        // Hide that pesky keyboard
        hideKeyboard();
    }

    // Google Maps Stuff
    @Override
    public void onMapReady(GoogleMap newMap) {
        // Set global map to newly ready map
        googleMap = newMap;

        // Enable map click listener
        googleMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        hideKeyboard();
    }

    private void hideKeyboard(){
        // Hide the on-screen keyboard
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onInfoWindowClick(Marker marker){
        for (int i = 0; i < markers.size(); i++) {
            if (markers.get(i).equals(marker)) {
                User.getCurrentUser().setCurrentSite(searchResults.get(i));

                Intent intent = new Intent(HomeActivity.this, DisplayCampsiteActivity.class);
                startActivity(intent);
            }
        }
    }

    /*
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
    }*/
}
