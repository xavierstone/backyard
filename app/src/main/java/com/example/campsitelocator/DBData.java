package com.example.campsitelocator;

import android.content.ContentValues;

/*
Flexible type that stores all the data in the database
For now the table field is necessary to identify the type of data,
but it is also usually evident from the context

Assumes data is supplied in the same order as the columns of the table
 */

public class DBData {
    // Table the data belongs to and an array of column values
    // Data is represented as Strings, but can be converted as needed
    private Table table;
    private String[] data;

    // Constructor requires table because the table determines the type of DBData
    public DBData(Table table){
        this.table = table;
        this.data = new String[table.columns()];
    }

    // Getters
    public String getTableName(){return this.table.getName();}
    public String[] getColNames(){return this.table.getColNames();}
    public String[] getColTypes(){return this.table.getColTypes();}
    public int numColumns(){return this.table.columns(); }

    // Seeks out the data in a particular column
    public String getData(String colName){
        for (int i=0; i < this.table.columns(); i++){
            if (this.table.getColNames()[i]==colName){
                return this.data[i];
            }
        }
        return "";
    }

    // Sets the data in a particular column
    public void setData(String colName, String value){
        for (int i=0; i < this.table.columns(); i++){
            if (this.table.getColNames()[i]==colName){
                this.data[i] = value;
            }
        }
    }

    // Prints the data in a single line separated by commas
    public String printData(){
        String printout = "";
        for (int i=0; i < this.table.columns(); i++){
            if (i>0)
                printout += ", ";
            printout += this.data[i];
        }

        return printout + "\n";
    }

    // Formats the data for the SQLiteDatabase insert method
    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        for (int i = 0; i < this.table.columns(); i++){
            if (this.table.getColNames()[i] != "id") {
                // Parse the data based on the column type
                switch (this.table.getColTypes()[i]) {
                    case "INTEGER":
                        values.put(this.table.getColNames()[i], Integer.parseInt(data[i]));
                        break;
                    case "TEXT":
                        values.put(this.table.getColNames()[i], data[i]);
                        break;
                    case "DOUBLE":
                        values.put(this.table.getColNames()[i], Double.parseDouble(data[i]));
                        break;
                }
            }
        }

        return values;
    }

    // Replaces entire data array with new data
    public DBData addData(String[] data){
        this.data = data;
        return this;
    }
}
