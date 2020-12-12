package com.xavierstone.backyard.models;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.xavierstone.backyard.activities.DisplayCampsiteActivity;
import com.xavierstone.backyard.activities.HomeActivity;
import com.xavierstone.backyard.activities.MainActivity;
import com.xavierstone.backyard.db.DBHandler;

import java.util.ArrayList;

// Represents the campsite
public class Site {
    // Attributes
    private String id; // Mongo ID
    private String name; // campsite name/title
    private LatLng location; // site location implemented as Google LatLng object (latitude/longitude pair)
    private String skinny; // site description

    // Current Photo tracker
    private int currentPic;

    // References
    private final User author;
    private final ArrayList<Pic> pics; // site photos in order to be displayed
    private final ArrayList<Rant> rants; // site reviews

    // Marker for site the user is currently viewing/interacting with
    private static Site currentSite;

    // Constructor should be called before adding any photos or reviews
    public Site(User author, String id, String name, LatLng location, String skinny) {
        // Copy arguments
        this.author = author;
        this.id = id;
        this.name = name;
        this.location = location;
        this.skinny = skinny;

        this.currentPic = 0;

        // Initialize ArrayLists
        pics = new ArrayList<>();
        rants = new ArrayList<>();
    }

    // Getters
    public User getAuthor() { return author; }
    public String getId() { return id; }
    public String getName() { return name; }
    public LatLng getLocation() { return location; }
    public String getSkinny() { return skinny; }
    public ArrayList<Pic> getPics() { return pics; }
    public ArrayList<Rant> getRants() { return rants; }
    public static Site getCurrentSite() { return currentSite; }

    // Get star rating
    public float getStars() {
        // initialize rating sum
        float totalRating = 0.0f;

        // Loop through ArrayList and add values
        for (Rant rant : rants) {
            totalRating += rant.getStars();
        }

        // Divide by total number of reviews and return
        return totalRating/rants.size();
    }

    // Check permissions
    public boolean hasPermission(User author) { return (this.author == author); }

    // Setters, check permission first
    public void setName(User author, String name) { if (hasPermission(author)) this.name = name; }
    public void setLocation(User author, LatLng location) { if (hasPermission(author)) this.location = location; }
    public void setSkinny(User author, String skinny) { if (hasPermission(author)) this.skinny = skinny; }

    // Load campsite info
    public void loadCampsite() {
        // Just photos for now
        // TODO: add rants

        // Kick off Async loadPics
        //MainActivity.dbHandler.loadSitePics(User.getCurrentUser().getCurrentSite());
    }

    // Set current site, forces a full load of site data
    public static void setCurrentSite(Site site) {
        currentSite = site;
        //currentSite.loadCampsite();
    }

    // Get current photo
    public Bitmap getCurrentPic(Activity currentActivity) {
        if (pics.isEmpty())
            return null;

        return pics.get(currentPic).loadImage(currentActivity);
    }

    // Moves the current photo by an increment, only designed for 1 or -1
    public void scrollGallery(int increment) {
        if (Math.abs(increment) == 1) {
            currentPic += increment;

            // Wrap around photos array
            if (currentPic < 0) currentPic = pics.size() - 1;
            if (currentPic >= pics.size()) currentPic = 0;
        }
    }

    // Registers
    public void registerPic(Pic pic) { pics.add(pic); }
    public void registerRant(Rant rant) { rants.add(rant); }
}
