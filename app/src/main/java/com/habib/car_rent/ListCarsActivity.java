package com.habib.car_rent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListCarsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CarAdapter adapter;
    private List<Car> carList, filteredCarList;
    private DatabaseReference databaseReference;
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listcars);

        searchBar = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.car_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        carList = new ArrayList<>();
        filteredCarList = new ArrayList<>();
        adapter = new CarAdapter(this, filteredCarList);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("cars");

        // Get the data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Car car = postSnapshot.getValue(Car.class);
                    if (car != null) {
                        car.setId(postSnapshot.getKey());
                        carList.add(car);
                    }
                }
                filteredCarList.addAll(carList); // Initially, the filtered list has all cars
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ListCarsActivity", "Failed to read cars", databaseError.toException());
            }
        });

        // Set up the search bar to filter results
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this implementation
            }
        });
    }

    private void filter(String text) {
        filteredCarList.clear();
        for (Car car : carList) {
            if (car.getMake().toLowerCase().contains(text.toLowerCase()) ||
                    car.getModel().toLowerCase().contains(text.toLowerCase())) {
                filteredCarList.add(car);
            }
        }
        adapter.notifyDataSetChanged();
    }
    public static class Car {
        private String id; // Assuming you have a unique ID for each car

        private String make, model, status, imageUrl;
        private double dailyRate;
        private int mileage;

        // Default constructor required for calls to DataSnapshot.getValue(Car.class)
        public Car() {
        }

        // Constructor with parameters
        public Car(String make, String model, double dailyRate, String status, int mileage, String imageUrl) {
            this.make = make;
            this.model = model;
            this.dailyRate = dailyRate;
            this.status = status;
            this.mileage = mileage;
            this.imageUrl = imageUrl;
        }



        public String getId() {
            return id;
        }

        public String getMake() {
            return make;
        }

        public String getModel() {
            return model;
        }

        public String getStatus() {
            return status;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public double getDailyRate() {
            return dailyRate;
        }

        public int getMileage() {
            return mileage;
        }

        // Setters


        public void setId(String id) {
            this.id = id;
        }
        public void setMake(String make) {
            this.make = make;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public void setDailyRate(double dailyRate) {
            this.dailyRate = dailyRate;
        }

        public void setMileage(int mileage) {
            this.mileage = mileage;
        }





        // toString method for debugging
        @Override
        public String toString() {
            return "Car{" +
                    "make='" + make + '\'' +
                    ", model='" + model + '\'' +
                    ", status='" + status + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", dailyRate=" + dailyRate +
                    ", mileage=" + mileage +
                    '}';
        }
    }
}
