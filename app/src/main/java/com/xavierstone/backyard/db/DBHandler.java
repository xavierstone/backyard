package com.xavierstone.backyard.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.print.pdf.PrintedPdfDocument;
import android.util.JsonReader;

import com.google.android.gms.maps.model.LatLng;
import com.xavierstone.backyard.activities.MainActivity;
import com.xavierstone.backyard.models.Pic;
import com.xavierstone.backyard.models.Site;
import com.xavierstone.backyard.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/*
The DBHandler class handles relations with the Database
It also contains hard-coded static representations of the SQLite tables

Currently upgrading to MongoDB
 */

public class DBHandler /*extends SQLiteOpenHelper*/ {

    // database name and version
    //private static final int DB_VER = 1;
    //private static final String DB_NAME = "Backyard";

    private final String dbUrl = "http://10.0.2.2:8080/";

    /*
    // user account types
    public static final int LOCAL_ACCOUNT = 0;
    public static final int FACEBOOK_ACCOUNT = 1;

    // Hard-coded SQLite Tables
    public static final Table campsitesTable = new Table("campsites",
            new String[]{"id","name","description","lat","long"},
            new String[]{"INTEGER", "TEXT", "TEXT", "DOUBLE", "DOUBLE"},
            "PRIMARY KEY (id)");

    public static final Table usersTable = new Table("users",
            new String[]{"id","first_name","last_name","type","email"},
            new String[]{"INTEGER", "TEXT", "TEXT", "INTEGER", "TEXT"},
            "PRIMARY KEY (id)");

    // Photo types: 0 - Drawable, 1 - Internal Storage
    public static final Table photosTable = new Table("photos",
            new String[]{"id","campsite_id","type","path"},
            new String[]{"INTEGER", "INTEGER", "INTEGER", "TEXT"},
            "PRIMARY KEY (id), FOREIGN KEY (campsite_id) REFERENCES campsites(id)");

    public static final Table ratingsTable = new Table("ratings",
            new String[]{"id","user_id","campsite_id","stars"},
            new String[]{"INTEGER","INTEGER","INTEGER","DOUBLE"},
            "PRIMARY KEY (id), FOREIGN KEY (user_id) REFERENCES users(id), " +
                    "FOREIGN KEY (campsite_id) REFERENCES campsites(id)");

    public static final Table favoritesTable = new Table("favorites",
            new String[]{"id","user_id","campsite_id"},
            new String[]{"INTEGER","INTEGER","INTEGER"},
            "PRIMARY KEY (id), FOREIGN KEY (user_id) REFERENCES users(id), " +
                    "FOREIGN KEY (campsite_id) REFERENCES campsites(id)");

    // Instance variable for context and writable database
    Context context;
    SQLiteDatabase db;

    // constructor
    public DBHandler(Context context, String name,
                        SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, DB_NAME + ".db", factory, DB_VER);
        this.context = context;
        db = getWritableDatabase();
        db.close();
    }

    // assumed constructor
    public DBHandler() {
        super(MainActivity.currentActivity, null, null, 1);
        this.context = MainActivity.currentActivity;
        db = getWritableDatabase();
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        // Creates the tables
        db.execSQL(campsitesTable.create());
        db.execSQL(usersTable.create());
        db.execSQL(photosTable.create());
        db.execSQL(ratingsTable.create());
        db.execSQL(favoritesTable.create());

        // Open campsites.txt and load content into a string
        String text = "";
        try{
            InputStream is = context.getAssets().open("campsites.txt");

            // Create byte buffer array
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Translate bytes to String
            text = new String(buffer);
        }catch (IOException ex){
            ex.printStackTrace();
        }

        // Parse text string
        String c = "";
        String subStr = "";
        String[] colNames = DBHandler.campsitesTable.getColNames();
        String[] data = new String[colNames.length];
        data[0] = "0";
        int fieldPos = 1;
        long dataID = -1;
        boolean multi = false;

        // Parse file
        for (int i=0; i < text.length(); i++){
            c = text.substring(i, i+1);

            if (c.equals("|") || c.equals("`")){
                if (fieldPos < colNames.length){
                    data[fieldPos] = subStr;
                }else{
                    // Image
                    if (!multi) {
                        DBData dbData = new DBData(DBHandler.campsitesTable);
                        dbData.addData(data);
                        dataID = db.insert(dbData.getTableName(), null, dbData.getValues());
                        multi = true;
                    }

                    DBData newPhoto = (new DBData(DBHandler.photosTable)).addData(new String[]{"0", ""+dataID, "0", subStr});
                    db.insert(newPhoto.getTableName(), null, newPhoto.getValues());
                }
                subStr = "";
                fieldPos += 1;
            }else{
                subStr += c;
            }

            if (c.equals("`")){
                fieldPos = 1;
                subStr = "";
                multi = false;
            }
        }

        int i =1;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // Upgrade method
        String query = "DROP TABLE IF EXISTS "+DB_NAME+";";
        db.execSQL(query);
        onCreate(db);
    }

    @Override
    public synchronized void close() {
        super.close();
    }*/

    public DBHandler(){
        // do something?
    }

    /*
    // Inserts a row into the DB
    public long insert(DBData dbData){
        db = getWritableDatabase();

        long rowID = db.insert(dbData.getTableName(), null, dbData.getValues());

        db.close();

        // IDs are generated automatically, so update the ID in the program code
        dbData.setData("id", ""+rowID);

        // ID is also returned for use by other classes
        return rowID;
    }

    public void update(DBData dbData, String colName, String value){
        String query = "UPDATE "+dbData.getTableName()+
                " SET "+colName+" = \""+value+"\""+
                " WHERE id = "+dbData.getData("id");

        db = getWritableDatabase();
        db.execSQL(query);
        db.close();

        dbData.setData(colName, value);
    }*/

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
                    results.add(User.getCurrentUser().createCampsite(currentJson.getString("_id"),
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
                    parent.registerPic(new Pic(User.getCurrentUser(), parent,
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

    /*
    // Searches for a single query in any column. Only exact matches can be used, no inequalities
    // or regex. Returns a list of results, even if there is only one.
    public ArrayList<DBData> search(Table table, String colName, String term){
        return this.search(table, colName + " = \"" +
                term + "\"");
    }

    // Searches for all results in a table
    public ArrayList<DBData> search(Table table){
        return this.search(table, "");
    }

    // Searches a table with a provided WHERE clause (omit the WHERE keyword)
    public ArrayList<DBData> search(Table table, String whereClause){
        // Set query
        String query = "SELECT * FROM " +
                table.getName();

        // Check for WHERE clause
        if (!whereClause.equals("")){
            query += " WHERE " + whereClause + ";";
        }else{
            query += ";";
        }

        // Create cursor from query
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<DBData> resultsList = new ArrayList<DBData>();

        // Cursor moves through the data
        if (cursor.moveToFirst()) {
            do {
                // Translate the data from the DB into the list
                DBData dbData = new DBData(table);
                String[] data = new String[dbData.numColumns()];
                for (int i = 0; i < dbData.numColumns(); i++) {
                    // Parse to double if necessary
                    if (table.getColTypes()[i].equals("DOUBLE")) {
                        data[i] = "" + cursor.getDouble(i);
                    }else{
                        data[i] = cursor.getString(i);
                    }
                }
                dbData.addData(data);
                resultsList.add(dbData);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();

        // Return the list
        return resultsList;
    }*/

    /*
    public boolean delete(Table table, String colName, String term) {
        boolean result = false;

        String query = "SELECT * FROM " +
                table.getName() + " WHERE " + colName +
                " = \"" + term + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        DBData dbData = new DBData(table);

        if (cursor.moveToFirst()) {
            String[] data = new String[dbData.numColumns()];
            for (int i = 0; i < dbData.numColumns(); i++){
                data[i] = cursor.getString(i);
            }
            dbData.addData(data);
            db.delete(table.getName(),  "id = ?",
                    new String[] {String.valueOf(dbData.getData("id"))});
            cursor.close();
            result = true;
        }

        db.close();
        return result;
    }*/
}
