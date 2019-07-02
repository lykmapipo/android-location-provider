package com.github.lykmapipo.location.ui;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.lykmapipo.location.LocationProvider;
import com.github.lykmapipo.location.sample.R;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView tvLongitude;
    private TextView tvLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // request last known location
        tvLongitude = findViewById(R.id.tvLongitude);
        tvLatitude = findViewById(R.id.tvLatitude);
        Button btnRequestLastLocation = findViewById(R.id.btnRequestLastLocation);
        btnRequestLastLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationProvider.requestLastLocation(getApplicationContext(), new LocationProvider.OnLastLocationListener() {
                    @Override
                    public void onSuccess(Location location) {
                        Toast.makeText(MainActivity.this, "Location Success: " + location.toString(), Toast.LENGTH_SHORT).show();
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();
                        tvLatitude.setText(String.format(Locale.ENGLISH, "%s: %f", "Latitude", latitude));
                        tvLongitude.setText(String.format(Locale.ENGLISH, "%s: %f", "Longitude", longitude));
                    }

                    @Override
                    public void onFailure(Exception error) {
                        Toast.makeText(MainActivity.this, "Location Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
