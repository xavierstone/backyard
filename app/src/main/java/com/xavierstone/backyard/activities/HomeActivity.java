package com.xavierstone.backyard.activities;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.SearchView;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.xavierstone.backyard.BackyardApplication;
import com.xavierstone.backyard.R;
import com.xavierstone.backyard.db.DBCallback;
import com.xavierstone.backyard.db.DBHandler;
import com.xavierstone.backyard.db.LoginRepository;
import com.xavierstone.backyard.db.LoginResponse;
import com.xavierstone.backyard.db.Result;
import com.xavierstone.backyard.models.Site;
import com.xavierstone.backyard.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;

/*
Provides the user with a multi-activity home screen
Integrates map functionality with main navigation and search bar
 */

public class HomeActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationButtonClickListener {

    // Marker list
    private final ArrayList<Marker> markers = new ArrayList<>();

    // DBHandler
    //private DBHandler dbHome;

    // Search results array
    private ArrayList<Site> searchResults = new ArrayList<>();

    // Views
    private GoogleMap googleMap;
    private Button signButton;

    private String query;

    // Location
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    // Sign In
    LoginRepository loginRepository;
    Observer<Result<LoginResponse>> resultObserver;

    RatingBar ratingBar;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Login initialization
        resultObserver = new Observer<Result<LoginResponse>>() {
            @Override
            public void onChanged(Result<LoginResponse> result) {
                if (result instanceof Result.Success) {
                    if (((Result.Success<LoginResponse>) result).data.getResult()) {
                        User.setCurrentUser(((Result.Success<LoginResponse>) result).data.getData());
                        signButton.setText(R.string.signButtonTextAlt);

                        // toast to our success
                        Toast signInSuccess = Toast.makeText(HomeActivity.this, "Sign In Successful!", Toast.LENGTH_LONG);
                        signInSuccess.setGravity(Gravity.CENTER, 0, 0);
                        signInSuccess.show();
                    }else{
                        signButton.setText(R.string.signButtonText);

                        // toast to our... failure?
                        Toast signInFailure = Toast.makeText(HomeActivity.this,"Do You Even Go Here?",Toast.LENGTH_LONG);
                        signInFailure.setGravity(Gravity.CENTER, 0, 0);
                        signInFailure.show();
                    }
                }
            }
        };

        // Initialize repo
        loginRepository = new LoginRepository(BackyardApplication.getExecutorService(), BackyardApplication.getThreadHandler(), this, resultObserver);

        // Initialize test user
        loginRepository.signIn("test","test");

        // Initialize signButton
        signButton = findViewById(R.id.signButton);

        // Initialize Search Bar
        searchView = findViewById(R.id.searchBar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            // Activates when user clicks on search buttons
            @Override
            public boolean onQueryTextSubmit(String querySource) {
                query = querySource;
                new searchSites().execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Initialize Location Provider
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                    }
                }
            }
        };

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, MainActivity.permissions[0]) == PermissionChecker.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //TODO: UI updates.
                    currentLocation = location;

                    // Load the Google Map Fragment
                    loadMap();
                }
            });
            /*
            // if location enabled, wait for location check to come through to create map
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){
                        currentLocation = location;

                        // Load the Google Map Fragment
                        loadMap();
                    }
                }
            });

             */
        }else{
            // Otherwise just DO it!
            Toast noLocPerm = Toast.makeText(HomeActivity.this,"You're Not Letting Us Spy on You...",Toast.LENGTH_LONG);
            noLocPerm.setGravity(Gravity.CENTER, 0, 0);
            noLocPerm.show();
            loadMap();
        }
    }

    private void loadMap() {
        // Load the Google Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMapsFrag);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set text of Sign In Button to reflect sign in status
        if (User.getCurrentUser() == null)
            signButton.setText(R.string.signButtonText);
        else
            signButton.setText(R.string.signButtonTextAlt);
    }

    private void updateMapResults(){
        // Clear all markers from map
        for (Marker marker : markers)
            marker.remove();
        markers.clear();

        // Initialize Camera Bound Guide
        ArrayList<LatLng> latLngs = new ArrayList<>();
        LatLngBounds.Builder bounds = null;
        Site curSite = null;
        double curLat = 0;
        double curLng = 0;
        double spread = 0;

        // Loop through results list and add a marker for each site
        for (int i=0; i<searchResults.size(); i++){

            curSite = searchResults.get(i);

            // Add lat/long to array list
            latLngs.add(curSite.getLocation());

            // Add marker to list
            Marker newMarker = googleMap.addMarker(new MarkerOptions().position(latLngs.get(i))
                    .title(curSite.getName()));
            newMarker.setTag(searchResults.get(i)); // tag with campsite
            markers.add(newMarker);

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

        // Calculate zoom level
        // Zoom limits for Google Camera
        int MAX_ZOOM = 14;
        int zoom = (int) (MAX_ZOOM - Math.floor(Math.log10(spread/500)/Math.log10(2)));
        if (zoom > MAX_ZOOM) zoom = MAX_ZOOM;

        assert bounds != null;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.build().getCenter(), zoom));
        googleMap.setOnInfoWindowClickListener(this);

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

        // If location permission available
        if (currentLocation != null) {
            // Center on current location
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            //MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
            //googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            //googleMap.addMarker(markerOptions);

            // Enable location
            if (ActivityCompat.checkSelfPermission(this, MainActivity.permissions[0]) == PermissionChecker.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                googleMap.setOnMyLocationButtonClickListener(this);
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        hideKeyboard();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Center on current location
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        //MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        //googleMap.addMarker(markerOptions);

        return true;
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
        assert marker.getTag() != null;

        // Verify marker is tagged with a Site object
        if (marker.getTag().getClass() == Site.class) {
            Site current = (Site) marker.getTag();
            // retrieve campsite tag and set current campsite; kicks off async transfer
            User.getCurrentUser().setCurrentSite((Site) marker.getTag());

            // Transfer to DisplayCampsiteActivity
            Intent intent = new Intent(HomeActivity.this, DisplayCampsiteActivity.class);
            startActivity(intent);
        }
    }

    private class searchSites extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            searchResults = BackyardApplication.getDB().findSites(query);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            // For non-empty result lists, update map
            if (!searchResults.isEmpty()) {
                updateMapResults();
            }else{
                // Otherwise, make a quick toast
                Toast noResults = Toast.makeText(HomeActivity.this,"No Sites Found",Toast.LENGTH_LONG);
                hideKeyboard();
                noResults.setGravity(Gravity.CENTER, 0, 0);
                noResults.show();
            }

            // Clear query
            searchView.setQuery("", false);

            super.onPostExecute(aVoid);
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
