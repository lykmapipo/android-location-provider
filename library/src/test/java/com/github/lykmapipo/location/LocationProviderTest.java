package com.github.lykmapipo.location;

import android.content.Context;
import android.location.Address;
import android.location.Location;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowPreconditions.class})
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
    public void testShouldCheckLocationSettings() throws Exception {
        Task<LocationSettingsResponse> task = LocationProvider.checkLocationSettings(context);
        assertNotNull("Should check location settings", task);
    }

    @Test
    public void testShouldRequestLocation() throws Exception {
        Task<Location> task = LocationProvider.requestLocation(context);
        assertNotNull("Should request location", task);
    }

    @Test
    public void testShouldCreateLocationCallback() {
        LocationCallback callback = LocationProvider.createLocationCallback(new LocationProvider.OnLocationUpdatesListener() {
            @Override
            public void onSuccess(LocationResult result) {

            }

            @Override
            public void onFailure(Exception error) {

            }
        });
        assertNotNull("Should create location callback", callback);
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

    @Test
    public void testGetAddressFromLocation() throws Exception {
        Location location = new Location("");
        Task<Address> task =
                LocationProvider.getAddressFromLocation(context, location);
        assertNotNull("Should get location address", task);
    }

    @After
    public void cleanup() {
        LocationProvider.clear();
    }

}