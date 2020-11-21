package com.xavierstone.backyard.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.xavierstone.backyard.R;

/*
This activity presents the user with an About this App page
Some things to include
-Creator name and copyright date
-Short description of app purpose
 */
public class AboutThisAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_this_app);
    }

    // Transfers control to the Home activity
    public void goHome(View view){
        Intent intent = new Intent(AboutThisAppActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
