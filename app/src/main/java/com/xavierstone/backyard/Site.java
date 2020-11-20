package com.xavierstone.backyard;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

// Represents the campsite
public class Site {
    // Attributes
    private String name; // campsite name/title
    private LatLng location; // site location implemented as Google LatLng object (latitude/longitude pair)
    private String skinny; // site description

    // References
    private final User author;
    private final ArrayList<Photo> photos; // site photos in order to be displayed
    private final ArrayList<Rant> rants; // site reviews

    // Constructor should be called before adding any photos or reviews
    public Site(User author, String name, LatLng location, String skinny) {
        // Copy arguments
        this.author = author;
        this.name = name;
        this.location = location;
        this.skinny = skinny;

        // Initialize ArrayLists
        photos = new ArrayList<>();
        rants = new ArrayList<>();
    }

    // Getters
    public User getAuthor() { return author; }
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

    // Registers
    public void registerPhoto(Photo photo) { photos.add(photo); }
    public void registerRant(Rant rant) { rants.add(rant); }

    // Load photo from internal storage
    // TODO: implement photo loader, calls method in Photo
}
