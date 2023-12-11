package com.habib.car_rent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;




public class Maps extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener {

    GoogleMap gMap;
    FrameLayout map;
    String make, model, dailyRate, status ,mileage;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        map = findViewById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent i = getIntent();
        make = i.getStringExtra("make");
        model = i.getStringExtra("model");
        dailyRate = i.getStringExtra("dailyRate");
        status = i.getStringExtra("status");
        mileage = i.getStringExtra("mileage");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMap = googleMap;
        LatLng latLng = new LatLng(36.8065, 10.1815);
        this.gMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
        this.gMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLng(latLng));
        this.gMap.setOnMapClickListener(this);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    @Override
    public void onMapClick(LatLng latLng) {
        System.out.println("onMapClick");

        double clickedLatitude = latLng.latitude;
        double clickedLongitude = latLng.longitude;

        showConfirmationDialog(clickedLatitude, clickedLongitude);
    }

    private void showConfirmationDialog(double latitude, double longitude) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Do you want to confirm the selected location?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Maps.this, "Location confirmed!", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();

                        resultIntent.putExtra("latitude", latitude);
                        resultIntent.putExtra("longitude", longitude);


                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User canceled, you can handle this if needed
                        Toast.makeText(Maps.this, "Location not confirmed.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .show();
    }
}

