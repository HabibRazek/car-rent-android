package com.habib.car_rent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddCarActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    private EditText makeInput, modelInput, dailyRateInput, statusInput, mileageInput,carMapInput;
    private ImageView carImageView;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private double latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcars);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("cars");
        storageReference = FirebaseStorage.getInstance().getReference("car_images");

        // Initialize UI elements
        makeInput = findViewById(R.id.carMakeInput);
        modelInput = findViewById(R.id.carModelInput);
        dailyRateInput = findViewById(R.id.carDailyRateInput);
        statusInput = findViewById(R.id.carStatusInput);
        mileageInput = findViewById(R.id.carMileageInput);
        carImageView = findViewById(R.id.carImageView);
        carMapInput = findViewById(R.id.carMapInput);
        Button uploadImageButton = findViewById(R.id.uploadImageButton);
        Button submitCarButton = findViewById(R.id.submitCarButton);


        // Upload Image button click listener
        uploadImageButton.setOnClickListener(v -> {
            openFileChooser();
        });

        // Submit button click listener
        submitCarButton.setOnClickListener(v -> {
            uploadCar();
        });


        carMapInput.setOnClickListener(v -> {
            String make = makeInput.getText().toString().trim();
            String model = modelInput.getText().toString().trim();
            String dailyRate = dailyRateInput.getText().toString().trim();
            String status = statusInput.getText().toString().trim();
            String mileage = mileageInput.getText().toString().trim();


            Intent intent = new Intent(AddCarActivity.this, Maps.class);
            intent.putExtra("CarMake", make);
            intent.putExtra("CarModel", model);
            intent.putExtra("CarDailyRate", dailyRate);
            intent.putExtra("CarStatus", status);
            intent.putExtra("CarMileage", mileage);

            startActivityForResult(intent, 1);
        });
    }




    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            carImageView.setImageURI(imageUri);

            Log.d("AddCarActivity", "Image URI: " + imageUri.toString());

        }

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                double latitude = data.getDoubleExtra("latitude", -1);
                double longitude = data.getDoubleExtra("longitude", -1);
                this.latitude = latitude;
                this.longitude = longitude;
                carMapInput.setText("Latitude: " + latitude + ", Longitude: " + longitude);
                Toast.makeText(this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
            }
        }
    }




    private void uploadCar() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            UploadTask uploadTask = fileReference.putFile(imageUri);

            uploadTask.addOnFailureListener(e -> {
                // Handle unsuccessful uploads
                Toast.makeText(AddCarActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(taskSnapshot -> {
                // Task completed successfully
                fileReference.getDownloadUrl().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String imageUrl = downloadUri.toString();

                        Car car = new Car(
                                makeInput.getText().toString().trim(),
                                modelInput.getText().toString().trim(),
                                Double.parseDouble(dailyRateInput.getText().toString().trim()),
                                statusInput.getText().toString().trim(),
                                Integer.parseInt(mileageInput.getText().toString().trim()),
                                imageUrl,
                                latitude,
                                longitude
                        );

                        String carId = databaseReference.push().getKey();
                        databaseReference.child(carId).setValue(car);

                        Toast.makeText(AddCarActivity.this, "Car added successfully", Toast.LENGTH_LONG).show();
                        finish(); // Go back to the previous Activity
                    } else {
                        Toast.makeText(AddCarActivity.this, "Get download URL failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }



    public static class Car {

        public String make, model, status, imageUrl;
        public double dailyRate , latitude , longitude;
        public int mileage;


        public Car(String make, String model, double dailyRate,  String status, int mileage, String imageUrl ,double latitude , double longitude) {
            this.make = make;
            this.model = model;
            this.dailyRate = dailyRate;
            this.status = status;
            this.mileage = mileage;
            this.imageUrl = imageUrl;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}