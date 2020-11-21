package com.xavierstone.backyard.activities;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

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

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    // Marker list
    private ArrayList<Marker> markers = new ArrayList<>();

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        LatLngBounds.Builder bounds = null;
        mMap = googleMap;

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
            markers.add(mMap.addMarker(new MarkerOptions().position(latLngs.get(i))
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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.build().getCenter(), zoom));
        mMap.setOnInfoWindowClickListener(this);

        // Check for location permission before enabling myLocation
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION )
                == PackageManager.PERMISSION_GRANTED ) {

            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker){
        for (int i = 0; i < markers.size(); i++) {
            if (markers.get(i).equals(marker)) {
                Intent intent = new Intent(MapsActivity.this, DisplayCampsiteActivity.class);
                //DisplayCampsiteActivity.currentCampsite =
                //        Long.parseLong(SearchOptionsActivity.searchResults.get(i).getData("id"));
                startActivity(intent);
            }
        }
    }
}
