package com.xavierstone.backyard.models;

import com.google.android.gms.maps.model.LatLng;
import com.xavierstone.backyard.activities.MainActivity;

import java.util.ArrayList;

// Represents the user
public class User {
    // Static current user
    private static User currentUser = null;

    // Attributes
    private String id; // SQLite ID
    private String name; // single field for first name, last name, nickname etc.
    private String email; // email address

    // ArrayLists
    private final ArrayList<Site> faves; // favorite sites
    private final ArrayList<Rant> rants; // reviews
    private final ArrayList<Site> createdSites;
    private final ArrayList<Pic> createdPics;

    // Marker for site the user is currently viewing/interacting with
    private Site currentSite;

    public User(String id, String name, String email){
        // create user with specified attributes
        this.id = id;
        this.name = name;
        this.email = email;

        // initialize lists
        faves = new ArrayList<>();
        rants = new ArrayList<>();
        createdSites = new ArrayList<>();
        createdPics = new ArrayList<>();
    }

    // Static getUser method
    public static User getCurrentUser() { return currentUser; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    // Set current site, forces a full load of site data
    public void setCurrentSite(Site site) {
        this.currentSite = site;
        currentSite.loadCampsite();
    }

    // Add favorite site
    public void addFave(Site site) { faves.add(site); }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Site getCurrentSite() { return currentSite; }

    // Version 0.1 array list getters
    public ArrayList<Site> getFaves() { return faves; }
    public ArrayList<Rant> getRants() { return rants; }
    public ArrayList<Site> getCreatedSites() { return createdSites; }
    public ArrayList<Pic> getCreatedPics() { return createdPics; }

    // Sign In
    // TODO: correctly implement sign in
    public static boolean signIn(String email, String password) {
        User result = MainActivity.dbHandler.validateUser(email, password);
        if (result != null) {
            currentUser = result;
            return true;
        }else
            return false;
    }

    public static User createAccount(String name, String email, String password){
        return MainActivity.dbHandler.createAccount(name, email, password);
    }

    // Creator methods
    // Need updating to properly handle ID field with database

    // Create a campsite
    public Site createCampsite(String id, String name, LatLng location, String skinny){
        return new Site(this, id, name, location, skinny);
    }

    // Add a rant to the current site
    public Rant addRant(byte stars, String words) {
        Rant newRant = new Rant(this, currentSite, id, stars, words);       // create rant
        currentSite.registerRant(newRant);                                      // register with campsite
        return newRant;
    }

    // Add a pic to the current site
    public Pic addPic(String uri) {
        Pic newPic = new Pic(this, currentSite, id, uri);     // create pic
        currentSite.registerPic(newPic);                    // register with campsite
        return newPic;
    }

    // Update site methods
    public void updateSiteName(String newName) { currentSite.setName(this, newName); }
    public void updateSiteLocation(LatLng newLoc) { currentSite.setLocation(this, newLoc); }
    public void updateSiteSkinny(String newSkinny) { currentSite.setSkinny(this, newSkinny); }

    // Update rant methods
    public void updateRantStars(Rant rant, byte newStars) { rant.setStars(this, newStars); }
    public void updateRantWords(Rant rant, String newWords) { rant.setWords(this, newWords); }
}
