package com.xavierstone.backyard.db;

import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;
import com.xavierstone.backyard.models.Pic;
import com.xavierstone.backyard.models.Site;
import com.xavierstone.backyard.models.User;
import com.xavierstone.backyard.security.HashHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/*
The DBHandler class handles relations with the MongoDB server
 */

public class DBHandler {

    // database URL (for testing only)
    // TODO: update url once server has permanent home
    private final String dbUrl = "http://10.0.2.2:8080/";

    // TODO: does the constructor actually need to do anything?
    public DBHandler(){
        // do something?
    }

    // Find methods
    // TODO: create find methods as needed

    // Find based on "term", only implemented for Site
    // Returns no results with an empty term
    public ArrayList<Site> findSites(String term) {
        ArrayList<Site> results = new ArrayList<>();

        String dataString;
        String[] jsonResults;
        try {
            // Try connection to DB url
            dataString = readUrl(dbUrl + "/sites?term="+term);

            // If successful, try to convert result to Json
            try{
                // parse out results
                jsonResults = dataString.split("\\|");

                for (String jsonResult : jsonResults){
                    JSONObject currentJson = new JSONObject(jsonResult);

                    JSONObject latLongJson = (JSONObject) currentJson.get("location");

                    // Convert to Site type
                    // TODO: update for users
                    results.add(new Site(null, currentJson.getString("_id"),
                            currentJson.getString("name"),
                            new LatLng(Double.parseDouble(latLongJson.getString("lat")),
                                    Double.parseDouble(latLongJson.getString("long"))),
                            currentJson.getString("skinny")));
                }
            } catch (JSONException e) {
                // On error, print message and return empty results list
                System.out.println("Error, could not convert to Json");
            }
        } catch (Exception e) {
            // On error, print message and return empty results list
            System.out.println("Error, could not connect to "+dbUrl);
        }

        return results;
    }

    // Find photos based on the parent campsite
    // Registers to campsite, no return value
    public void loadSitePics(Site parent) {
        String parentId = parent.getId();

        String dataString;
        String[] jsonResults;
        try {
            // Try connection to DB url
            dataString = readUrl(dbUrl + "/pics?site="+parentId);

            // If successful, try to convert result to Json
            try{
                // parse out results
                jsonResults = dataString.split("\\|");

                for (String jsonResult : jsonResults){
                    JSONObject currentJson = new JSONObject(jsonResult);

                    // Convert to Site type
                    // TODO: update for users
                    parent.registerPic(new Pic(null, parent,
                            currentJson.getString("_id"),
                            currentJson.getString("data")));
                }
            } catch (JSONException e) {
                // On error, print message and return empty results list
                System.out.println("Error, could not convert to Json");
            }
        } catch (Exception e) {
            // On error, print message and return empty results list
            System.out.println("Error, could not connect to "+dbUrl);
            e.printStackTrace();
        }
    }

    // Loads a user, verifies ID
    public User validateUser(String email, String password){
        String dataString;
        String[] jsonResults;
        try {
            // Try connection to DB url
            dataString = readUrl(dbUrl + "/users/get?email="+email);

            // If successful, try to convert result to Json
            try{
                // parse out results
                jsonResults = dataString.split("\\|");

                for (String jsonResult : jsonResults){
                    JSONObject currentJson = new JSONObject(jsonResult);

                    byte[] salt = Base64.decode(currentJson.getString("salt"), Base64.DEFAULT);
                    HashHelper hashHelper = new HashHelper(password, salt);
                    if (hashHelper.checkMatch(Base64.decode(currentJson.getString("hash"),Base64.DEFAULT))) {
                        User signedUser = new User(currentJson.getString("_id"),
                                currentJson.getString("name"),
                                email);
                        User.setCurrentUser(signedUser);
                        return signedUser;
                    }else
                        return null;
                }
            } catch (JSONException e) {
                // On error, print message and return empty results list
                System.out.println("Error, could not convert to Json");
            }
        } catch (Exception e) {
            // On error, print message and return empty results list
            System.out.println("Error, could not connect to "+dbUrl);
            e.printStackTrace();
        }
        return null;
    }

    // Generic function for reading from the server
    private String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    // Create a new user, hash password and push to Mongo
    public User createAccount(String name, String email, String password){
        // Hash the password
        HashHelper hashHelper = new HashHelper(password);
        BufferedReader reader = null;

        try {
            byte[] rawSalt = hashHelper.getSalt();
            byte[] rawHash = hashHelper.getHash();

            String salt = Base64.encodeToString(rawSalt, Base64.DEFAULT);
            String hash = Base64.encodeToString(rawHash, Base64.DEFAULT);

            try{
                // Thanks to user Ferrybig, StackOverflow
                URL url = new URL(dbUrl + "/users/add");
                URLConnection con = url.openConnection();
                HttpURLConnection http = (HttpURLConnection)con;
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                Map<String,String> arguments = new HashMap<>();
                arguments.put("name", name);
                arguments.put("email", email);
                arguments.put("hash", hash);
                arguments.put("salt", salt);
                StringJoiner sj = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    sj = new StringJoiner("&");
                    for (Map.Entry<String, String> entry : arguments.entrySet())
                        sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                                + URLEncoder.encode(entry.getValue(), "UTF-8"));
                    byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
                    int length = out.length;
                    http.setFixedLengthStreamingMode(length);
                    http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    http.connect();
                    try(OutputStream os = http.getOutputStream()) {
                        os.write(out);
                    }
                    reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                    StringBuffer buffer = new StringBuffer();
                    int read;
                    char[] chars = new char[1024];
                    while ((read = reader.read(chars)) != -1)
                        buffer.append(chars, 0, read);

                    reader.close();

                    return new User(reader.toString(), name, email);
                }


            } catch (Exception e){
                e.printStackTrace();
            }

            // In the middle of this;
            //   - connect to server
            //   - finish server side implementation
            //   - test it
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
}
