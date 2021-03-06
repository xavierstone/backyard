package com.xavierstone.backyard.models;

import com.google.android.gms.maps.model.LatLng;
import com.xavierstone.backyard.BackyardApplication;
import com.xavierstone.backyard.activities.MainActivity;
import com.xavierstone.backyard.db.DBHandler;
import com.xavierstone.backyard.db.LoginResponse;

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
    public static void setCurrentUser(User currentUser) { User.currentUser = currentUser; }

    // Add favorite site
    public void addFave(Site site) { faves.add(site); }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    // Version 0.1 array list getters
    public ArrayList<Site> getFaves() { return faves; }
    public ArrayList<Rant> getRants() { return rants; }
    public ArrayList<Site> getCreatedSites() { return createdSites; }
    public ArrayList<Pic> getCreatedPics() { return createdPics; }

    // Creator methods
    // Need updating to properly handle ID field with database

    // Create a campsite
    public Site createCampsite(String id, String name, LatLng location, String skinny){
        return new Site(this, id, name, location, skinny);
    }

    // Add a rant to the current site
    public Rant addRant(byte stars, String words) {
        Rant newRant = new Rant(this, Site.getCurrentSite(), id, stars, words);       // create rant
        Site.getCurrentSite().registerRant(newRant);                                      // register with campsite
        return newRant;
    }

    // Add a pic to the current site
    public Pic addPic(String uri) {
        Pic newPic = new Pic(this, Site.getCurrentSite(), id, uri);     // create pic
        Site.getCurrentSite().registerPic(newPic);                    // register with campsite
        return newPic;
    }

    // Update site methods
    public void updateSiteName(String newName) { Site.getCurrentSite().setName(this, newName); }
    public void updateSiteLocation(LatLng newLoc) { Site.getCurrentSite().setLocation(this, newLoc); }
    public void updateSiteSkinny(String newSkinny) { Site.getCurrentSite().setSkinny(this, newSkinny); }

    // Update rant methods
    public void updateRantStars(Rant rant, byte newStars) { rant.setStars(this, newStars); }
    public void updateRantWords(Rant rant, String newWords) { rant.setWords(this, newWords); }
}
