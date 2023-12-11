package com.habib.car_rent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class BookingActivity extends AppCompatActivity {

    private TextView carMakeTextView, carModelTextView, totalPriceTextView, quantityTextView, userNameTextView, userPhoneNumberTextView;
    private ImageView carImageView, userImageView;
    private Button decreaseButton, increaseButton, bookNowButton;
    private int quantity = 1;
    private double dailyRate;
    private DatabaseReference carsDatabaseRef, usersDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book);

        // Initialize the Firebase reference
        carsDatabaseRef = FirebaseDatabase.getInstance().getReference("cars");
        usersDatabaseRef = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI Components
        carMakeTextView = findViewById(R.id.car_selected_make);
        carModelTextView = findViewById(R.id.car_selected_model);
        totalPriceTextView = findViewById(R.id.textview_total_price);
        quantityTextView = findViewById(R.id.textview_quantity);
        carImageView = findViewById(R.id.car_selected_image);
        userImageView = findViewById(R.id.imageview_user);
        decreaseButton = findViewById(R.id.button_decrease);
        increaseButton = findViewById(R.id.button_increase);
        bookNowButton = findViewById(R.id.button_book_now);
        userNameTextView = findViewById(R.id.textview_user_name);
        userPhoneNumberTextView = findViewById(R.id.textview_number);

        // Retrieve the extras from the intent
        Intent intent = getIntent();
        String carId = intent.getStringExtra("CarId");
        dailyRate = intent.getDoubleExtra("CarDailyRate", -1);  // Default to -1 if not found

        if (carId == null || dailyRate == -1) {
            // If there's no car ID or daily rate, then don't proceed and finish the activity.
            Toast.makeText(this, "Car information is not available.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Assuming 'CarId' was passed to the activity
        carsDatabaseRef.child(carId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String carMake = dataSnapshot.child("make").getValue(String.class);
                String carModel = dataSnapshot.child("model").getValue(String.class);
                String carImageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                carMakeTextView.setText(carMake);
                carModelTextView.setText(carModel);
                Picasso.get().load(carImageUrl).into(carImageView);
                updateTotalPrice();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BookingActivity.this, "Failed to load car data.", Toast.LENGTH_SHORT).show();
            }
        });

        // Get the currently authenticated user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            usersDatabaseRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userName = dataSnapshot.child("userName").getValue(String.class);
                    String userPhone = dataSnapshot.child("phone").getValue(String.class);
                    userNameTextView.setText(userName);
                    userPhoneNumberTextView.setText(userPhone);
                    // Assuming there's a 'profileImage' field in your user's Firebase data
                    String userProfileImageUrl = dataSnapshot.child("profileImage").getValue(String.class);
                    Picasso.get().load(userProfileImageUrl).into(userImageView);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(BookingActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // No user is signed in
            Toast.makeText(this, "No user is signed in.", Toast.LENGTH_SHORT).show();
            // Redirect to login screen or handle accordingly
        }

        // Quantity button logic
        decreaseButton.setOnClickListener(view -> {
            if (quantity > 1) {
                quantity--;
                quantityTextView.setText(String.valueOf(quantity));
                updateTotalPrice();
            }
        });

        increaseButton.setOnClickListener(view -> {
            quantity++;
            quantityTextView.setText(String.valueOf(quantity));
            updateTotalPrice();
        });

        // Book Now button logic
        bookNowButton.setOnClickListener(view -> {
            // Update the status of the car to "Booked"
            carsDatabaseRef.child(carId).child("status").setValue("Booked");
            Toast.makeText(BookingActivity.this, "Car booked successfully!", Toast.LENGTH_SHORT).show();
            // Navigate to a "Booking Confirmed" screen or show a dialog message
        });

        // Call button logic
        findViewById(R.id.imageview_call).setOnClickListener(view -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + userPhoneNumberTextView.getText().toString()));
            startActivity(callIntent);
        });
    }

    private void updateTotalPrice() {
        double totalPrice = quantity * dailyRate;
        totalPriceTextView.setText(String.format("TND%.2f", totalPrice));
    }
}
