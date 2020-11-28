package com.xavierstone.backyard.models;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.xavierstone.backyard.R;
import com.xavierstone.backyard.activities.MainActivity;

import java.io.FileNotFoundException;

// Represents a photo, must be associated with a user and a campsite
public class Pic {
    // String literals
    private static final String resourcePrefix = "android.resource://";
    private static final String drawableDirectory = "/drawable/";

    // ID
    private String id;

    // URI for internal storage
    private String filename;

    // References
    private final User author;
    private final Site site;

    public Pic(User author, Site site, String id, String filename) {
        // Copy arguments
        this.author = author;
        this.site = site;
        this.id = id;
        this.filename = filename;
    }

    // Load from internal storage
    // Returns bitmap
    public Bitmap loadImage(){
        // Construct filepath from components
        String filepath = resourcePrefix + MainActivity.PACKAGE_NAME + drawableDirectory + filename;

        // Parse into Uri
        Uri targetUri = Uri.parse(filepath);

        // Try opening bitmap
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(MainActivity.currentActivity.getContentResolver().openInputStream(targetUri));

            // Return if successful
            return bitmap;
        } catch (FileNotFoundException e) {
            // Otherwise print error
            // TODO: handle 'no Bitmap found' gracefully
            e.printStackTrace();
            return null;
        }
    }
}
