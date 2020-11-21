package com.xavierstone.backyard.models;

// Represents a photo, must be associated with a user and a campsite
public class Photo {
    // ID
    private long id;

    // URI for internal storage
    private String uri;

    // References
    private final User author;
    private final Site site;

    public Photo(User author, Site site, long id, String uri) {
        // Copy arguments
        this.author = author;
        this.site = site;
        this.id = id;
        this.uri = uri;
    }

    // Load from internal storage
    // TODO: implement image loader
}
