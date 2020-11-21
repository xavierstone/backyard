package com.xavierstone.backyard.models;

// Represents a photo, must be associated with a user and a campsite
public class Photo {
    // URI for internal storage
    private String uri;

    // References
    private final User author;
    private final Site site;

    public Photo(User author, Site site, String uri) {
        // Copy arguments
        this.author = author;
        this.site = site;
        this.uri = uri;
    }

    // Load from internal storage
    // TODO: implement image loader
}
