package com.github.lykmapipo.location.ui;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.lykmapipo.location.LocationProvider;
import com.github.lykmapipo.location.sample.R;
import com.google.android.gms.location.LocationResult;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView tvLongitude;
    private TextView tvLatitude;
    private TextView tvAddress;
    private Location lastKnownLocation;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // request last known location
        tvLongitude = findViewById(R.id.tvLongitude);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvAddress = findViewById(R.id.tvAddress);
        Button btnRequestLastLocation = findViewById(R.id.btnRequestLastLocation);
        btnRequestLastLocation.setOnClickListener(v -> LocationProvider.requestLastLocation(this, new LocationProvider.OnLastLocationListener() {
            @Override
            public void onSuccess(Location location) {
                Toast.makeText(MainActivity.this, "Location Success: " + location.toString(), Toast.LENGTH_SHORT).show();
                lastKnownLocation = location;
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                tvLatitude.setText(format("Latitude", latitude));
                tvLongitude.setText(format("Longitude", longitude));
            }

            @Override
            public void onFailure(Exception error) {
                Toast.makeText(MainActivity.this, "Location Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));

        // request address
        Button btnRequestAddress = findViewById(R.id.btnRequestAddress);
        btnRequestAddress.setOnClickListener(v -> {
            if (lastKnownLocation != null) {
                LocationProvider.requestAddress(this, lastKnownLocation, new LocationProvider.OnAddressListener() {

                    @Override
                    public void onSuccess(Address address) {
                        Toast.makeText(MainActivity.this, "Address Success: " + address.toString(), Toast.LENGTH_SHORT).show();
                        tvAddress.setText(format("Address", address.getAdminArea(), address.getCountryName()));
                    }

                    @Override
                    public void onFailure(Exception error) {
                        Toast.makeText(MainActivity.this, "Address Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // request location updates
        Button btnRequestLocationUpdates = findViewById(R.id.btnRequestLocationUpdates);
        btnRequestLocationUpdates.setOnClickListener(v -> LocationProvider.requestLocationUpdates(this, new LocationProvider.OnLocationUpdatesListener() {
            @Override
            public void onSuccess(LocationResult result) {
                // obtain latest location
                Location location = result.getLastLocation();

                Toast.makeText(MainActivity.this, "Location Updates Success: " + location.toString(), Toast.LENGTH_SHORT).show();
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                tvLatitude.setText(format("Latitude", latitude));
                tvLongitude.setText(format("Longitude", longitude));
            }

            @Override
            public void onFailure(Exception error) {
                Toast.makeText(MainActivity.this, "Location Updates Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));

        // stop location updates
        Button btnStopLocationUpdates = findViewById(R.id.btnStopLocationUpdates);
        btnStopLocationUpdates.setOnClickListener(view -> {
            LocationProvider.stopLocationUpdates();
            Toast.makeText(MainActivity.this, "Location Updates Stopped Successfully", Toast.LENGTH_SHORT).show();

        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationProvider.stopLocationUpdates();
    }

    private String format(@NonNull String label, @NonNull double value) {
        return String.format(Locale.ENGLISH, "%s: %f", label, value);
    }

    private String format(@NonNull String label, @NonNull String... value) {
        String s = TextUtils.join(", ", value);
        return String.format(Locale.ENGLISH, "%s: %s", label, s);
    }
}
