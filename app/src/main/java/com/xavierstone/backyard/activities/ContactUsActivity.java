package com.xavierstone.backyard.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.xavierstone.backyard.R;

/*
Contact customer service functionality
 */
public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
    }

    public void goHome(View view){
        Intent intent = new Intent(ContactUsActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
