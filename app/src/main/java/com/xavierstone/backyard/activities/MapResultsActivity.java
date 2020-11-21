package com.xavierstone.backyard.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.xavierstone.backyard.R;
import com.xavierstone.backyard.db.DBData;

import java.util.ArrayList;

/*
This activity displays the results of a search
 */

public class MapResultsActivity extends AppCompatActivity {

    // Static ArrayList to hold search results
    public static ArrayList<DBData> searchResults = new ArrayList<>();

    // Results Field
    TextView mapResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_results);

        // Load field
        mapResults = findViewById(R.id.mapResults);

        // Load data from static variable
        String resultsString = "";
        for (int i=0; i<searchResults.size(); i++){
            resultsString+=searchResults.get(i).printData()+"\n";
        }

        mapResults.setText(resultsString);
    }
}
