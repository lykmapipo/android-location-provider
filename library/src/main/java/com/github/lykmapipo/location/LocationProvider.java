package com.github.lykmapipo.location;


import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
     * Provides access to the Location Settings API.
     */
    private static SettingsClient settingsClient;

    /**
     * Create a new instance of {@link FusedLocationProviderClient} for use in a non-activity {@link Context}
     *
     * @param context
     * @return
     * @since 0.1.0
     */
    public static synchronized FusedLocationProviderClient createLocationClient(@NonNull Context context) {
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        }
        return fusedLocationClient;
    }

    /**
     * Create a new instance of {@link SettingsClient} for use in a non-activity {@link Context}
     *
     * @param context
     * @return
     * @since 0.1.0
     */
    public static synchronized SettingsClient createSettingsClient(@NonNull Context context) {
        if (settingsClient == null) {
            settingsClient = LocationServices.getSettingsClient(context);
        }
        return settingsClient;
    }

    /**
     * Clear and reset internal states
     *
     * @since 0.1.0
     */
    public static synchronized void clear() {
        settingsClient = null;
        fusedLocationClient = null;
    }

    /**
     * Get the last known location
     *
     * @param context
     * @param listener
     * @since 0.1.0
     */
    @RequiresPermission(
            anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}
    )
    public static synchronized void requestLastLocation(
            @NonNull Context context,
            @NonNull OnLastLocationListener listener) {
        // obtain fused location client
        FusedLocationProviderClient fusedLocationClient = createLocationClient(context);

        // request last known location
        Task<Location> lastLocation = fusedLocationClient.getLastLocation();
        lastLocation.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                // grab last known location
                Location lastLocation = task.getResult();
                // reply ok if found
                if (task.isSuccessful() && lastLocation != null) {
                    listener.onSuccess(lastLocation);
                }
                // notify error otherwise
                else {
                    listener.onFailure(task.getException());
                }
            }
        });
    }

    /**
     * Request location updates
     *
     * @param context
     * @param listener
     * @since 0.1.0
     */
    @RequiresPermission(
            anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}
    )
    public static synchronized void requestLocationUpdates(
            @NonNull Context context,
            @NonNull OnLastLocationListener listener) {

    }


    public interface OnLastLocationListener {
        void onSuccess(Location location);

        void onFailure(Exception error);
    }

    public interface OnLocationUpdatesListener {
        void onSuccess(Location location);

        void onFailure(Exception error);
    }

}
