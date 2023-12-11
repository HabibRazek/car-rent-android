package com.habib.car_rent;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button; // Import the Button class

public class MainPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the button by its ID
        Button getStartedButton = findViewById(R.id.buttonGetStarted);

        // Set an OnClickListener on the button
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the SignInActivity
                Intent intent = new Intent(MainPageActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}
