package com.habib.car_rent;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 3000; // 3000ms = 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startpage); // This is your splash screen layout.

        // New Handler to start the Main Activity (activity_main) after 3 seconds.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent that will start the MainPageActivity.
                Intent mainIntent = new Intent(SplashActivity.this, MainPageActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish(); // Destroy the splash activity so the user can't return to it.
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
