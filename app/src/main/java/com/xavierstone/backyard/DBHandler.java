package com.xavierstone.backyard;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/*
The DBHandler class handles relations with the Database
It also contains hard-coded static representations of the SQLite tables
 */

public class DBHandler extends SQLiteOpenHelper {

    // database name and version
    private static final int DB_VER = 1;
    private static final String DB_NAME = "CampsiteLocator.db";

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

    // constructor
    public DBHandler(Context context, String name,
                        SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, DB_NAME, factory, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        // Creates the tables
        db.execSQL(campsitesTable.create());
        db.execSQL(usersTable.create());
        db.execSQL(photosTable.create());
        db.execSQL(ratingsTable.create());
        db.execSQL(favoritesTable.create());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // Upgrade method
        String query = "DROP TABLE IF EXISTS CampsiteLocator;";
        db.execSQL(query);
        onCreate(db);
    }

    // Inserts a row into the DB
    public long insert(DBData dbData){
        SQLiteDatabase db = this.getWritableDatabase();

        long rowID = db.insert(dbData.getTableName(), null, dbData.getValues());

        // IDs are generated automatically, so update the ID in the program code
        dbData.setData("id", ""+rowID);
        db.close();

        // ID is also returned for use by other classes
        return rowID;
    }

    public void update(DBData dbData, String colName, String value){
        String query = "UPDATE "+dbData.getTableName()+
                " SET "+colName+" = \""+value+"\""+
                " WHERE id = "+dbData.getData("id");

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        dbData.setData(colName, value);
    }

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

        SQLiteDatabase db = this.getWritableDatabase();

        // Create cursor from query
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<DBData> resultsList = new ArrayList<DBData>();

        // Cursor moves through the data
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
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
    }

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
