package com.habib.car_rent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PanelActivity extends AppCompatActivity {

    private CircleImageView imageView;
    private TextView textLoggedUser;
    private Button buttonList, buttonAdd;
    private ImageButton buttonExit;
    private DatabaseReference usersDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel);

        // Initialize views
        imageView = findViewById(R.id.imageView);
        textLoggedUser = findViewById(R.id.textLoggedUser);
        buttonList = findViewById(R.id.button_list);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonExit = findViewById(R.id.buttonExit);
        usersDatabaseRef = FirebaseDatabase.getInstance().getReference("users");

        // Load user data from Firebase
        loadUserData();

        // Set up click listeners for buttons
        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to List Cars Activity
                Intent intent = new Intent(PanelActivity.this, ListCarsActivity.class);
                startActivity(intent);
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to Add Car Activity
                Intent intent = new Intent(PanelActivity.this, AddCarActivity.class);
                startActivity(intent);
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show exit confirmation dialog
                showExitConfirmation();
            }
        });
    }

    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            usersDatabaseRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userName = dataSnapshot.child("userName").getValue(String.class);
                    String userPhone = dataSnapshot.child("phone").getValue(String.class);
                    String userProfileImageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                    // Set the user's name
                    if (userName != null && !userName.trim().isEmpty()) {
                        textLoggedUser.setText("👋 Hi, " + userName + "!");
                    } else {
                        textLoggedUser.setText("👋 Welcome Guest!");
                    }

                    // Load the user's profile image
                    if (userProfileImageUrl != null && !userProfileImageUrl.trim().isEmpty()) {
                        Picasso.get().load(userProfileImageUrl).into(imageView);
                    } else {
                        imageView.setImageResource(R.drawable.user_icon);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(PanelActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // No user is signed in
            Toast.makeText(this, "No user is signed in.", Toast.LENGTH_SHORT).show();
            // Redirect to login screen or handle accordingly
        }
    }

    private void showExitConfirmation() {
        // Show an exit confirmation dialog
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Finish activity
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
