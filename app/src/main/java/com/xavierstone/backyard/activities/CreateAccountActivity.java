package com.xavierstone.backyard.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xavierstone.backyard.R;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/*
This activity allows the user to create a new user account
Requires first and last name, email address, and password
 */
public class CreateAccountActivity extends AppCompatActivity {

    // Text boxes
    TextView createAccountStatus;
    EditText firstNameBox;
    EditText lastNameBox;
    EditText emailAddressBox;
    EditText passwordBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Locate boxes
        createAccountStatus = findViewById(R.id.createAccountStatus);
        firstNameBox = (EditText) findViewById(R.id.firstName);
        lastNameBox = (EditText) findViewById(R.id.lastName);
        emailAddressBox = (EditText) findViewById(R.id.emailAddress);
        passwordBox = (EditText) findViewById(R.id.password);
    }

    public void createAccount(View view) throws NoSuchAlgorithmException, InvalidKeySpecException {
        /*
        TODO: implement create account
        // Open DB
        DBHandler dbHandler = new DBHandler(this, null, null, 1);

        // Verify email address is not registered to a user already
        // Queries the email address and checks to see if the resulting ArrayList is empty
        if (dbHandler.search(DBHandler.usersTable, "email", emailAddressBox.getText().toString()).isEmpty()) {
            // Create new user with the provided data
            DBData user = new DBData(DBHandler.usersTable);
            user.addData(new String[]{"0", firstNameBox.getText().toString(), lastNameBox.getText().toString(), "" + DBHandler.LOCAL_ACCOUNT,
                    emailAddressBox.getText().toString()});

            // Insert into database
            MainActivity.userID = (int) dbHandler.insert(user);

            String password = passwordBox.getText().toString();

            // Write password to locally storage file
            InternalStorage.saveCredentials(this, emailAddressBox.getText().toString() +
                    "," + password + "\n");

            // Pass control to the home activity
            Intent intent = new Intent(CreateAccountActivity.this, HomeActivity.class);
            startActivity(intent);
        }else{
            // Otherwise, inform user that email address is already taken
            createAccountStatus.setText("There is already a user with that email address.");

            // Clear email and password fields
            emailAddressBox.setText("");
            passwordBox.setText("");
        }*/
    }
}
