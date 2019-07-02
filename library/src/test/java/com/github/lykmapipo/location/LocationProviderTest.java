package com.github.lykmapipo.location;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.location.FusedLocationProviderClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

    @After
    public void cleanup() {
        LocationProvider.clear();
    }
}