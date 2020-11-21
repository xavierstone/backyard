package com.xavierstone.backyard.models;

import android.app.Activity;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.xavierstone.backyard.db.DBHandler;

import java.util.ArrayList;

// Represents the campsite
public class Site {
    // Attributes
    private long id;
    private String name; // campsite name/title
    private LatLng location; // site location implemented as Google LatLng object (latitude/longitude pair)
    private String skinny; // site description

    // Current Photo tracker
    private int currentPhoto;

    // References
    private final User author;
    private final ArrayList<Photo> photos; // site photos in order to be displayed
    private final ArrayList<Rant> rants; // site reviews

    // Constructor should be called before adding any photos or reviews
    public Site(User author, long id, String name, LatLng location, String skinny) {
        // Copy arguments
        this.author = author;
        this.id = id;
        this.name = name;
        this.location = location;
        this.skinny = skinny;

        this.currentPhoto = 0;

        // Initialize ArrayLists
        photos = new ArrayList<>();
        rants = new ArrayList<>();
    }

    // Getters
    public User getAuthor() { return author; }
    public long getId() { return id; }
    public String getName() { return name; }
    public LatLng getLocation() { return location; }
    public String getSkinny() { return skinny; }
    public ArrayList<Photo> getPhotos() { return photos; }
    public ArrayList<Rant> getRants() { return rants; }

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
    public void loadCampsite(Activity context) {
        // Just photos for now
        // TODO: add rants

        // Load photos from DB
        DBHandler dbHandler = new DBHandler(context, null, null, 1);
        dbHandler.loadSitePhotos(this);
    }

    // Get current photo
    public Bitmap loadCurrentPhoto(Activity context) {
        if (photos.isEmpty())
            return null;

        return photos.get(currentPhoto).loadImage(context);
    }

    // Moves the current photo by an increment, only designed for 1 or -1
    public void adjustCurrentPhoto(int increment) {
        if (Math.abs(increment) == 1) {
            currentPhoto += increment;

            // Wrap around photos array
            if (currentPhoto < 0) currentPhoto = photos.size() - 1;
            if (currentPhoto >= photos.size()) currentPhoto = 0;
        }
    }

    // Registers
    public void registerPhoto(Photo photo) { photos.add(photo); }
    public void registerRant(Rant rant) { rants.add(rant); }
}
