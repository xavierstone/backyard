package com.xavierstone.backyard.models;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;
import com.xavierstone.backyard.db.DBHandler;

import java.util.ArrayList;

// Represents the user
public class User {
    // Static current user
    private static User currentUser;

    // Attributes
    private long id; // SQLite ID
    private String name; // single field for first name, last name, nickname etc.
    private String email; // email address
    private String password; // plain text password

    // ArrayLists
    private final ArrayList<Site> faves; // favorite sites
    private final ArrayList<Rant> rants; // reviews
    private final ArrayList<Site> createdSites;
    private final ArrayList<Photo> createdPhotos;

    // Marker for site the user is currently viewing/interacting with
    private Site currentSite;

    public User(long id, String name, String email, String password){
        // create user with specified attributes
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;

        // initialize lists
        faves = new ArrayList<>();
        rants = new ArrayList<>();
        createdSites = new ArrayList<>();
        createdPhotos = new ArrayList<>();
    }

    // Static getUser method
    public static User getCurrentUser() { return currentUser; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    // Set current site, forces a full load of site data
    public void setCurrentSite(Activity context, Site site) {
        this.currentSite = site;
        currentSite.loadCampsite(context);
    }

    // Add favorite site
    public void addFave(Site site) { faves.add(site); }

    // Getters
    public long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Site getCurrentSite() { return currentSite; }

    // Version 0.1 array list getters
    public ArrayList<Site> getFaves() { return faves; }
    public ArrayList<Rant> getRants() { return rants; }
    public ArrayList<Site> getCreatedSites() { return createdSites; }
    public ArrayList<Photo> getCreatedPhotos() { return createdPhotos; }

    // Sign In
    // TODO: correctly implement sign in
    public static User signIn(String email, String password) {
        if (currentUser == null)
            currentUser = new User(0,"test", email, password);

        return currentUser;
    }

    // Validate credentials
    public boolean validateSignIn() {
        // TODO: implement sign in
        return true;
    }

    // Creator methods
    // Need updating to properly handle ID field with database

    // Create a campsite
    public Site createCampsite(long id, String name, LatLng location, String skinny){
        return new Site(this, id, name, location, skinny);
    }

    // Add a rant to the current site
    public Rant addRant(byte stars, String words) {
        Rant newRant = new Rant(this, currentSite, id, stars, words);       // create rant
        currentSite.registerRant(newRant);                                      // register with campsite
        return newRant;
    }

    // Add a photo to the current site
    public Photo addPhoto(String uri) {
        Photo newPhoto = new Photo(this, currentSite, id, uri);     // create photo
        currentSite.registerPhoto(newPhoto);                    // register with campsite
        return newPhoto;
    }

    // Update site methods
    public void updateSiteName(String newName) { currentSite.setName(this, newName); }
    public void updateSiteLocation(LatLng newLoc) { currentSite.setLocation(this, newLoc); }
    public void updateSiteSkinny(String newSkinny) { currentSite.setSkinny(this, newSkinny); }

    // Update rant methods
    public void updateRantStars(Rant rant, byte newStars) { rant.setStars(this, newStars); }
    public void updateRantWords(Rant rant, String newWords) { rant.setWords(this, newWords); }
}
