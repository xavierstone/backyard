package com.xavierstone.backyard.db;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/*
This class is uninstantiated. It contains only static methods for dealing with internal (and external, whoops) storage.
 */
public class InternalStorage {
    // Hard-coded internal directories
    private static final String INTERNAL_DIRECTORY = "private_dir";
    private static final String PASSWORD_FILE = "passwords.txt";
    private static final String IMAGE_DIRECTORY = INTERNAL_DIRECTORY+"/images";
    private static final String IMAGE_PREFIX = "img";
    private static final String DRAWABLE_DIRECTORY = "android.resource://com.example.campsitelocator/drawable/";
    private static final long IMAGE_SERIAL_DIGITS = 10;

    // Credential return codes
    public static final int VERIFIED = 0;
    public static final int DOES_NOT_EXIST = 1;
    public static final int WRONG_PASSWORD = 2;

    // Returns the image path
    public static String getImagePath(){
        return IMAGE_DIRECTORY;
    }

    // Returns the drawable path
    public static String getDrawPath(){return DRAWABLE_DIRECTORY;}

    // Codes the image name in order to preserve alphabetical ordering
    private static String getImageName(long imageID){
        String imageName = IMAGE_PREFIX;
        int digits = (""+imageID).length();

        // Add the appropriate number of leading zeros
        for (int i = 0; i < IMAGE_SERIAL_DIGITS - digits; i++){
            imageName += "0";
        }

        // Add ID and file extension and return
        return imageName + imageID + ".jpg";
    }

    // Saves a user name/password combo into internal storage
    public static void saveCredentials(Context mcoContext, String sBody){
        // Load/create internal directory
        File file = new File(mcoContext.getFilesDir(),INTERNAL_DIRECTORY);
        if(!file.exists()){
            file.mkdir();
        }

        try{
            // Open passwords file in append mode
            File gpxfile = new File(file, PASSWORD_FILE);
            FileWriter writer = new FileWriter(gpxfile, true);

            // Append data and close file
            writer.append(sBody);
            writer.flush();
            writer.close();

        }catch (Exception e){
            // Exception handling
            e.printStackTrace();

        }
    }

    // Reads credentials and returns a code based on the success of the operation
    // VERIFIED (0) = successful credentials
    // DOES_NOT_EXIST (1) = user does not exist
    // WRONG_PASSWORD (2) = wrong password
    public static int readCredentials(Context mcoContext, String email, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Read from a file
        boolean userExists = false;

        File file = new File(mcoContext.getFilesDir(), INTERNAL_DIRECTORY);
        if (file.exists()){
            try {
                File readFile = new File(file, PASSWORD_FILE);
                FileReader reader = new FileReader(readFile);

                // Read into list
                int i = 0;
                char c;
                String curLine = "";
                while ((i=reader.read()) != -1){
                    c = (char) i;
                    if (c == ','){
                        if (curLine.equals(email)){
                            // User exists
                            userExists = true;
                            curLine = "";
                        }
                    }else if (c == '\n'){
                        if (userExists){
                            // Compare password
                            if (curLine.equals(password)){
                                // Return success
                                return VERIFIED;
                            }else{
                                //Incorrect password
                                return WRONG_PASSWORD;
                            }
                        }else{
                            curLine = "";
                        }
                    }else{
                        curLine += c;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();

            }
        }else{
            // Create file
            saveCredentials(mcoContext,"");
        }
        return DOES_NOT_EXIST;
    }

    // Saves an image to internal storage, returns name of new image
    public static String savePhoto(Context mcoContext, String externalPath, long imageID){
        // Load/create internal directory
        File file = new File(mcoContext.getFilesDir(),IMAGE_DIRECTORY);
        if(!file.exists()){
            file.mkdir();
        }

        // Create image, assign name
        String imageName = getImageName(imageID);

        // Load external image to copy over
        Bitmap bitmapImage = loadExternalImage(mcoContext, externalPath);

        saveBitmap(bitmapImage, file, imageName);

        // Return name
        return imageName;
    }

    // Loads an image from external storage
    public static Bitmap loadExternalImage(Context context, String uri){
        Uri targetUri = Uri.parse(uri);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(targetUri));
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Loads an image from internal storage
    public static Bitmap loadInternalImage(Context mcoContext, String name)
    {
        try {
            File f=new File(mcoContext.getFilesDir() + "/" + IMAGE_DIRECTORY, name);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }

    }

    public static String saveBitmap(Context mcoContext, Bitmap bitmapImage, long imageID){
        File file = new File(mcoContext.getFilesDir(),IMAGE_DIRECTORY);
        if(!file.exists()){
            file.mkdir();
        }

        String name = getImageName(imageID);

        saveBitmap(bitmapImage, file, name);
        return name;
    }

    private static void saveBitmap(Bitmap bitmapImage, File directory, String name){
        File mypath = new File(directory, name);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 30, fos);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
