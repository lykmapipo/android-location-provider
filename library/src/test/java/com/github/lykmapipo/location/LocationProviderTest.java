package com.github.lykmapipo.location;

import android.content.Context;
import android.location.Location;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class LocationProviderTest {
    Context context;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testShouldCreateLocationClient() {
        FusedLocationProviderClient client = LocationProvider.createLocationClient(context);
        assertNotNull("Should create location client", client);
    }

    @Test
    public void testShouldCreateSettingsClient() {
        SettingsClient client = LocationProvider.createSettingsClient(context);
        assertNotNull("Should create settings client", client);
    }

    @Test
    public void testShouldCreateLocationRequest() {
        LocationRequest request = LocationProvider.createLocationRequest();
        assertNotNull("Should create location request", request);
    }

    @Test
    public void testShouldCreateLocationSettingRequest() {
        LocationSettingsRequest request = LocationProvider.createLocationSettingsRequest();
        assertNotNull("Should create location settings request", request);
    }

    @Test
    public void testShouldRequestLastKnownLocation() {
        LocationProvider.requestLastLocation(context, new LocationProvider.OnLastLocationListener() {
            @Override
            public void onSuccess(Location location) {
                assertNotNull("Should request last known location", location);
            }

            @Override
            public void onFailure(Exception error) {
                assertNull("Should request last known location", error);
            }
        });
    }

    @After
    public void cleanup() {
        LocationProvider.clear();
    }

}