package com.xavierstone.backyard;

/*
This class holds information about a Table in the database
 */

public class Table {

    // Name, column info, and constraints
    private String name;
    private String[] colNames;
    private String[] colTypes;
    private String constraints;

    // Full constructor, used by DBHandler to hard-code the schema
    public Table(String aName, String[] colNames, String[] colTypes, String someConstraints){
        this.colNames = colNames;
        this.colTypes = colTypes;
        this.name = aName;
        this.constraints = someConstraints;
    }

    // Returns the SQLite command to create the calling Table
    public String create(){
        String query = "CREATE TABLE " + this.name + "(";

        for (int i = 0; i < this.colNames.length; i++){
            if (i > 0){
                query+=", ";
            }
            query += (colNames[i] + " " + colTypes[i]);
        }

        if (this.constraints != ""){
            query += (", " + this.constraints);
        }

        query += ");";

        return query;
    }

    // Getters
    public String[] getColNames(){return this.colNames;}
    public String[] getColTypes(){return this.colTypes;}
    public String getName(){return this.name;}
    public int columns(){return this.getColNames().length;}
}
