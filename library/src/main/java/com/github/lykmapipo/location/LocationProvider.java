package com.github.lykmapipo.location;


import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

/**
 * A pack of helpful helpers to obtain location(s) from fused {@link com.google.android.gms.location.FusedLocationProviderClient}.
 *
 * @author lally elias <lallyelias87@gmail.com>
 * @version 0.1.0
 * @since 0.1.0
 */
public class LocationProvider {
    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private static FusedLocationProviderClient fusedLocationClient;

    /**
     * Create a new instance of {@link FusedLocationProviderClient} for use in a non-activity {@link Context}
     *
     * @param context
     * @return
     */
    public static synchronized FusedLocationProviderClient createLocationClient(@NonNull Context context) {
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        }
        return fusedLocationClient;
    }

    /**
     * Clear and reset internal states
     */
    public static synchronized void clear() {
        fusedLocationClient = null;
    }

    /**
     * Get the last known location
     *
     * @param context
     * @param listener
     */
    public static synchronized void requestLastLocation(
            @NonNull Context context,
            @NonNull OnLastLocationListener listener) {

    }


    public interface OnLastLocationListener {
        void onSuccess(Location location);

        void onFailure(Exception error);
    }

}
