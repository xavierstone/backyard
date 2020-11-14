package com.xavierstone.backyard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.RatingBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/*
Provides the user with a convenient home screen
 */

public class HomeActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    // Marker list
    private ArrayList<Marker> markers = new ArrayList<>();

    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMapsFrag);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);*/

        MapFragment mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.googleMapsFrag, mMapFragment);
        fragmentTransaction.commit();

        //ratingBar = findViewById(R.id.ratingBarHome);
    }

    // Google Maps Stuff
    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*
        ArrayList<LatLng> latLngs = new ArrayList<>();
        LatLngBounds.Builder bounds = null;

        DBData curData = null;
        double curLat = 0;
        double curLng = 0;
        double spread = 0;

        // Loop through results list and add a marker for each site
        for (int i=0; i<SearchOptionsActivity.searchResults.size(); i++){

            curData = SearchOptionsActivity.searchResults.get(i);
            curLat = Double.parseDouble(curData.getData("lat"));
            curLng = Double.parseDouble(curData.getData("long"));

            // Add lat/long to array list
            latLngs.add(new LatLng(curLat, curLng));

            // Add marker to list
            markers.add(googleMap.addMarker(new MarkerOptions().position(latLngs.get(i))
                    .title(curData.getData("name"))));

            // Update LatLng Bounds
            if (latLngs.size() > 1){
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

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.build().getCenter(), zoom));
        googleMap.setOnInfoWindowClickListener(this);

        // Check for location permission before enabling myLocation
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION )
                == PackageManager.PERMISSION_GRANTED ) {

            googleMap.setMyLocationEnabled(true);
        }
        */

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onInfoWindowClick(Marker marker){
        for (int i = 0; i < markers.size(); i++) {
            if (markers.get(i).equals(marker)) {
                Intent intent = new Intent(HomeActivity.this, DisplayCampsiteActivity.class);
                DisplayCampsiteActivity.currentCampsite =
                        Long.parseLong(SearchOptionsActivity.searchResults.get(i).getData("id"));
                startActivity(intent);
            }
        }
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
