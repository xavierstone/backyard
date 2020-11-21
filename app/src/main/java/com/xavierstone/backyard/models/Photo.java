package com.xavierstone.backyard.models;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.xavierstone.backyard.R;
import com.xavierstone.backyard.activities.MainActivity;

import java.io.FileNotFoundException;

// Represents a photo, must be associated with a user and a campsite
public class Photo {
    // String literals
    private static final String resourcePrefix = "android.resource://";
    private static final String drawableDirectory = "/drawable/";

    // ID
    private long id;

    // URI for internal storage
    private String filename;

    // References
    private final User author;
    private final Site site;

    public Photo(User author, Site site, long id, String filename) {
        // Copy arguments
        this.author = author;
        this.site = site;
        this.id = id;
        this.filename = filename;
    }

    // Load from internal storage
    // Returns bitmap
    public Bitmap loadImage(){
        String filepath = resourcePrefix + MainActivity.PACKAGE_NAME + drawableDirectory + filename;

        Uri targetUri = Uri.parse(filepath);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(MainActivity.currentActivity.getContentResolver().openInputStream(targetUri));
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
